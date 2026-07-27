package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * システムからの問い合わせや各種通知メールの非同期送信を担うアプリケーションサービス。
 */
@Service
public class EmailNotificationService {

    /** 開発・運用管理者のメールアドレス（バグ報告等の宛先） */
    private static final String SYSTEM_ADMIN_EMAIL = "admin@tokyohackgroup.com";

    /** システム送信元メールアドレス */
    private static final String SYSTEM_SENDER_EMAIL = "noreply@tokyohackgroup.com";

    /** 管理者通知メール件名の接頭辞 */
    private static final String CONTACT_SUBJECT_PREFIX = "【ポータルシステム】";

    /** お知らせ通知メール件名の接頭辞 */
    private static final String NOTICE_SUBJECT_PREFIX = "【新規お知らせ】";

    /** お知らせ通知の固定本文テンプレート */
    private static final String NOTICE_BODY_TEMPLATE = "新しいお知らせが投稿されました。ポータルサイトからご確認ください。";

    /** ユーザー招待メール件名の接頭辞 */
    private static final String INVITE_SUBJECT_PREFIX = "【ポータル招待】";

    private final JavaMailSender javaMailSender;

    public EmailNotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * ユーザーからの問い合わせ・バグ報告を管理者のメールアドレスへ送信する。
     *
     * <p>画面のレスポンス向上を図るため、@Async アノテーションにより別スレッドで非同期実行する。</p>

     * @param senderName 送信者の表示名（ニックネーム）
     * @param content    問い合わせの本文
     */
    @Async
    public void sendContactToAdmin(String senderName, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SYSTEM_SENDER_EMAIL);
        message.setTo(SYSTEM_ADMIN_EMAIL);
        message.setSubject(CONTACT_SUBJECT_PREFIX + senderName + "様からの問い合わせ");
        message.setText(content);

        try {
            javaMailSender.send(message);
        } catch (Exception exception) {
            // ローカル開発環境等でSMTPサーバーに接続できない場合でも、アプリ動作を途絶させないよう例外を安全に捕捉する
            System.err.println("メール送信処理をスキップしました: " + exception.getMessage());
        }
    }

    /**
     * 特定のメンバー宛てに新規お知らせの更新通知メールを送信する。
     *
     * @param targetEmail 宛先メールアドレス
     * @param noticeTitle お知らせのタイトル
     */
    @Async
    public void sendNoticeNotification(String targetEmail, String noticeTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SYSTEM_SENDER_EMAIL);
        message.setTo(targetEmail);
        message.setSubject(NOTICE_SUBJECT_PREFIX + noticeTitle);
        message.setText(NOTICE_BODY_TEMPLATE);

        try {
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.err.println("メール送信処理をスキップしました: " + exception.getMessage());
        }
    }

    /**
     * 管理者設定画面からの招待登録時に、仮パスワードを新規ユーザーへ通知する。
     *
     * @param targetEmail        宛先メールアドレス
     * @param displayName        招待対象の表示名
     * @param temporaryPassword  発行された仮パスワード（平文）
     */
    @Async
    public void sendInviteEmail(String targetEmail, String displayName, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SYSTEM_SENDER_EMAIL);
        message.setTo(targetEmail);
        message.setSubject(INVITE_SUBJECT_PREFIX + "アカウントが発行されました");
        message.setText(displayName + " 様\n\nTokyo Hack Group Portal のアカウントが発行されました。\n"
                + "メールアドレス: " + targetEmail + "\n"
                + "仮パスワード: " + temporaryPassword + "\n\n"
                + "ログイン後、マイページから速やかにパスワードを変更してください。");

        try {
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.err.println("メール送信処理をスキップしました: " + exception.getMessage());
        }
    }
}