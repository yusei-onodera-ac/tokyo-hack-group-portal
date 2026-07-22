package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.Notice;
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
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Notice> findNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId);
    }

    @Transactional
    public void createNotice(String title, String content, UserAccount author) {
        // 標準では全体公開のお知らせとして作成・永続化する
        Notice newNotice = new Notice(title, content, author);
        noticeRepository.save(newNotice);
    }

    @Transactional
    public void updateNotice(Long noticeId, String newTitle, String newContent) {
        Optional<Notice> existingNoticeOptional = noticeRepository.findById(noticeId);

        if (existingNoticeOptional.isEmpty()) {
            throw new IllegalArgumentException("指定されたお知らせが見つかりません。ID: " + noticeId);
        }

        Notice existingNotice = existingNoticeOptional.get();
        existingNotice.modifyContent(newTitle, newContent);

        noticeRepository.save(existingNotice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }
}