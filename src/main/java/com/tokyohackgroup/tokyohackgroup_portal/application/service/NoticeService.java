package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.Notice;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.NoticeCategory;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.NoticeReaction;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.NotificationType;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.CommentRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.NoticeReactionRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.NoticeRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.ProjectRepository;

/**
 * お知らせ（Notice）に関するビジネスロジックを統括するサービス。
 */
@Service
@Transactional(readOnly = true)
public class NoticeService {

    /** リアクションとして選択できる絵文字（表示順を兼ねる） */
    public static final List<String> REACTION_EMOJIS = List.of("👍", "❤️", "😂", "😮", "😢");

    private final NoticeRepository noticeRepository;
    private final ProjectRepository projectRepository;
    private final NoticeReactionRepository noticeReactionRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final EmailNotificationService emailNotificationService;

    public NoticeService(
            NoticeRepository noticeRepository,
            ProjectRepository projectRepository,
            NoticeReactionRepository noticeReactionRepository,
            CommentRepository commentRepository,
            UserService userService,
            NotificationService notificationService,
            EmailNotificationService emailNotificationService) {
        this.noticeRepository = noticeRepository;
        this.projectRepository = projectRepository;
        this.noticeReactionRepository = noticeReactionRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.emailNotificationService = emailNotificationService;
    }

    public List<Notice> fetchAllNotices() {
        List<Notice> notices = noticeRepository.findAllByOrderByCreatedAtDesc();
        initializeLazyAssociations(notices);
        return notices;
    }

    /**
     * 指定カテゴリに絞り込んだお知らせ一覧を取得する。カテゴリが null の場合は全件返す。
     */
    public List<Notice> fetchNoticesByCategory(NoticeCategory category) {
        if (category == null) {
            return fetchAllNotices();
        }
        List<Notice> notices = noticeRepository.findAllByCategoryOrderByCreatedAtDesc(category);
        initializeLazyAssociations(notices);
        return notices;
    }

    /**
     * open-in-view を無効化しているため、ビュー描画時の LazyInitializationException を防ぐべく
     * トランザクション境界内で作成者・関連プロジェクト（メンバー判定用）の遅延ロードプロキシを初期化しておく。
     */
    private void initializeLazyAssociations(List<Notice> notices) {
        notices.forEach(notice -> {
            notice.getAuthor().getDisplayName();
            if (notice.getRelatedProject() != null) {
                notice.getRelatedProject().getMembers().size();
            }
        });
    }

    public Optional<Notice> findNoticeById(Long noticeId) {
        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        noticeOptional.ifPresent(notice -> initializeLazyAssociations(List.of(notice)));
        return noticeOptional;
    }

    @Transactional
    public void createNotice(String title, String content, UserAccount author, NoticeCategory category, String tags, Long relatedProjectId) {
        Notice newNotice = new Notice(title, content, author, category, tags);
        if (relatedProjectId != null) {
            projectRepository.findById(relatedProjectId).ifPresent(newNotice::assignRelatedProject);
        }
        noticeRepository.save(newNotice);

        List<UserAccount> recipients = newNotice.isPublicToAll()
                ? userService.fetchAllActiveUsers()
                : List.copyOf(newNotice.getAllowedMembers());

        for (UserAccount recipient : recipients) {
            if (recipient.getId().equals(author.getId())) {
                continue;
            }
            notificationService.notify(recipient, NotificationType.NOTICE, "新しいお知らせ: " + title, null, "/notices");
            if (notificationService.isNoticeEmailEnabled(recipient)) {
                emailNotificationService.sendNoticeNotification(
                        recipient.getEmailAddress(), recipient.getDisplayName(), title, author.getDisplayName(), newNotice.getCreatedAt());
            }
        }
    }

    @Transactional
    public void updateNotice(Long noticeId, String newTitle, String newContent, NoticeCategory newCategory, String newTags, Long relatedProjectId) {
        Optional<Notice> existingNoticeOptional = noticeRepository.findById(noticeId);

        if (existingNoticeOptional.isEmpty()) {
            throw new IllegalArgumentException("指定されたお知らせが見つかりません。ID: " + noticeId);
        }

        Notice existingNotice = existingNoticeOptional.get();
        existingNotice.modifyContent(newTitle, newContent, newCategory, newTags);

        Project relatedProject = (relatedProjectId != null) ? projectRepository.findById(relatedProjectId).orElse(null) : null;
        existingNotice.assignRelatedProject(relatedProject);

        noticeRepository.save(existingNotice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (noticeOptional.isEmpty()) {
            return;
        }
        Notice notice = noticeOptional.get();

        // FK制約に抵触しないよう、先に紐づくコメント・リアクションを削除する
        commentRepository.deleteAll(commentRepository.findByNoticeOrderByCreatedAtAsc(notice));
        noticeReactionRepository.deleteAll(noticeReactionRepository.findByNoticeOrderByReactedAtAsc(notice));

        noticeRepository.delete(notice);
    }

    /**
     * リアクションをトグルする。同じ絵文字を選び直した場合は取り消し、別の絵文字を選んだ場合は変更する。
     * リアクション（何であれ）が存在すること自体を「既読」の簡易的な確認としても扱う。
     */
    @Transactional
    public void toggleReaction(Long noticeId, UserAccount user, String emoji) {
        if (!REACTION_EMOJIS.contains(emoji)) {
            throw new IllegalArgumentException("不正な絵文字が指定されました。");
        }
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたお知らせが見つかりません。ID: " + noticeId));

        Optional<NoticeReaction> existingReaction = noticeReactionRepository.findByNoticeAndUser(notice, user);
        if (existingReaction.isPresent()) {
            NoticeReaction reaction = existingReaction.get();
            if (reaction.getEmoji().equals(emoji)) {
                noticeReactionRepository.delete(reaction);
            } else {
                reaction.changeEmoji(emoji);
                noticeReactionRepository.save(reaction);
            }
        } else {
            noticeReactionRepository.save(new NoticeReaction(notice, user, emoji));
        }
    }

    /**
     * 絵文字ごとのリアクション者の表示名一覧を取得する（0件の絵文字も含め、表示順を保持する）。
     */
    public Map<String, List<String>> summarizeReactionsByEmoji(Notice notice) {
        Map<String, List<String>> summary = new LinkedHashMap<>();
        for (String emoji : REACTION_EMOJIS) {
            summary.put(emoji, new ArrayList<>());
        }
        for (NoticeReaction reaction : noticeReactionRepository.findByNoticeOrderByReactedAtAsc(notice)) {
            summary.get(reaction.getEmoji()).add(reaction.getUser().getDisplayName());
        }
        return summary;
    }

    /**
     * 現在のログインユーザーがこのお知らせに付けているリアクション（未リアクションの場合は null）を取得する。
     */
    public String findMyReaction(Notice notice, UserAccount user) {
        return noticeReactionRepository.findByNoticeAndUser(notice, user)
                .map(NoticeReaction::getEmoji)
                .orElse(null);
    }

}