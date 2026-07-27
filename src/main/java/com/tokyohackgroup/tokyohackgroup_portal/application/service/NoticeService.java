package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.Notice;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.NoticeCategory;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.NoticeRepository;

/**
 * お知らせ（Notice）に関するビジネスロジックを統括するサービス。
 */
@Service
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<Notice> fetchAllNotices() {
        List<Notice> notices = noticeRepository.findAllByOrderByCreatedAtDesc();
        initializeAuthors(notices);
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
        initializeAuthors(notices);
        return notices;
    }

    /**
     * open-in-view を無効化しているため、ビュー描画時の LazyInitializationException を防ぐべく
     * トランザクション境界内で作成者の遅延ロードプロキシを初期化しておく。
     */
    private void initializeAuthors(List<Notice> notices) {
        notices.forEach(notice -> notice.getAuthor().getDisplayName());
    }

    public Optional<Notice> findNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId);
    }

    @Transactional
    public void createNotice(String title, String content, UserAccount author, NoticeCategory category, String tags) {
        Notice newNotice = new Notice(title, content, author, category, tags);
        noticeRepository.save(newNotice);
    }

    @Transactional
    public void updateNotice(Long noticeId, String newTitle, String newContent, NoticeCategory newCategory, String newTags) {
        Optional<Notice> existingNoticeOptional = noticeRepository.findById(noticeId);

        if (existingNoticeOptional.isEmpty()) {
            throw new IllegalArgumentException("指定されたお知らせが見つかりません。ID: " + noticeId);
        }

        Notice existingNotice = existingNoticeOptional.get();
        existingNotice.modifyContent(newTitle, newContent, newCategory, newTags);

        noticeRepository.save(existingNotice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }
}