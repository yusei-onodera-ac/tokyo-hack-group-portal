package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * システムからの問い合わせや各種通知メールの非同期送信を担うアプリケーションサービス。
 */
@Service
public class EmailNotificationService {

    /** 管理者通知メール件名の接頭辞 */
    private static final String CONTACT_SUBJECT_PREFIX = "【ポータルシステム】";

    /** お知らせ通知メール件名の接頭辞 */
    private static final String NOTICE_SUBJECT_PREFIX = "【新規お知らせ】";

    /** ユーザー招待メール件名の接頭辞 */
    private static final String INVITE_SUBJECT_PREFIX = "【ポータル招待】";

    /** 日程調整確定メール件名の接頭辞 */
    private static final String POLL_CONFIRMED_SUBJECT_PREFIX = "【日程確定】";

    /** 日程調整開始メール件名の接頭辞 */
    private static final String POLL_OPENED_SUBJECT_PREFIX = "【日程調整】";

    /** パスワード再設定メール件名の接頭辞 */
    private static final String PASSWORD_RESET_SUBJECT_PREFIX = "【パスワード再設定】";

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm");

    private static final String BRAND_FOOTER = "\n\n"
            + "――――――――――――――――――\n"
            + "Tokyo Hack Group Portal\n"
            + "――――――――――――――――――";

    private final JavaMailSender javaMailSender;
    private final String systemSenderEmail;
    private final String systemAdminEmail;
    private final String baseUrl;

    public EmailNotificationService(
            JavaMailSender javaMailSender,
            @Value("${app.mail.sender-email:noreply@tokyohackgroup.com}") String systemSenderEmail,
            @Value("${app.mail.admin-email:admin@tokyohackgroup.com}") String systemAdminEmail,
            @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.javaMailSender = javaMailSender;
        this.systemSenderEmail = systemSenderEmail;
        this.systemAdminEmail = systemAdminEmail;
        this.baseUrl = baseUrl;
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
        String body = senderName + " 様より、Tokyo Hack Group Portal にお問い合わせが届きました。\n\n"
                + "【送信者】" + senderName + "\n"
                + "【受信日時】" + LocalDateTime.now().format(DATE_TIME_FORMAT) + "\n\n"
                + "【内容】\n" + content
                + BRAND_FOOTER;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(systemSenderEmail);
        message.setTo(systemAdminEmail);
        message.setSubject(CONTACT_SUBJECT_PREFIX + senderName + "様からの問い合わせ");
        message.setText(body);

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
     * @param targetEmail   宛先メールアドレス
     * @param recipientName 宛先ユーザーの表示名
     * @param noticeTitle   お知らせのタイトル
     * @param authorName    投稿者の表示名
     * @param postedAt      投稿日時
     */
    @Async
    public void sendNoticeNotification(String targetEmail, String recipientName, String noticeTitle, String authorName, LocalDateTime postedAt) {
        String body = recipientName + " 様\n\n"
                + "Tokyo Hack Group Portal で新しいお知らせが投稿されました。\n\n"
                + "【タイトル】" + noticeTitle + "\n"
                + "【投稿者】" + authorName + "\n"
                + "【投稿日時】" + postedAt.format(DATE_TIME_FORMAT) + "\n\n"
                + "▼お知らせ一覧はこちら\n" + baseUrl + "/notices"
                + BRAND_FOOTER;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(systemSenderEmail);
        message.setTo(targetEmail);
        message.setSubject(NOTICE_SUBJECT_PREFIX + noticeTitle);
        message.setText(body);

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
        String body = displayName + " 様\n\n"
                + "Tokyo Hack Group Portal のアカウントが発行されました。\n\n"
                + "【メールアドレス】" + targetEmail + "\n"
                + "【仮パスワード】" + temporaryPassword + "\n\n"
                + "ログイン後、マイページから速やかにパスワードを変更してください。\n\n"
                + "▼ログインはこちら\n" + baseUrl + "/login"
                + BRAND_FOOTER;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(systemSenderEmail);
        message.setTo(targetEmail);
        message.setSubject(INVITE_SUBJECT_PREFIX + "アカウントが発行されました");
        message.setText(body);

        try {
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.err.println("メール送信処理をスキップしました: " + exception.getMessage());
        }
    }

    /**
     * 日程調整の開始（招待）時に、招待者へ通知する。
     *
     * @param targetEmail       宛先メールアドレス
     * @param displayName       宛先ユーザーの表示名
     * @param pollId            日程調整のID（回答ページへのリンク生成用）
     * @param pollTitle         日程調整のタイトル
     * @param organizerName     主催者の表示名
     * @param responseDeadline  回答期限（未設定の場合はnull）
     */
    @Async
    public void sendPollOpenedEmail(String targetEmail, String displayName, Long pollId, String pollTitle, String organizerName, LocalDateTime responseDeadline) {
        StringBuilder body = new StringBuilder()
                .append(displayName).append(" 様\n\n")
                .append("Tokyo Hack Group Portal で新しい日程調整が開始されました。\n\n")
                .append("【件名】").append(pollTitle).append('\n')
                .append("【主催者】").append(organizerName).append('\n');
        if (responseDeadline != null) {
            body.append("【回答期限】").append(responseDeadline.format(DATE_TIME_FORMAT)).append('\n');
        }
        body.append("\n候補日時への回答をお願いします。\n\n")
                .append("▼回答はこちら\n").append(baseUrl).append("/polls/").append(pollId)
                .append(BRAND_FOOTER);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(systemSenderEmail);
        message.setTo(targetEmail);
        message.setSubject(POLL_OPENED_SUBJECT_PREFIX + pollTitle);
        message.setText(body.toString());

        try {
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.err.println("メール送信処理をスキップしました: " + exception.getMessage());
        }
    }

    /**
     * パスワード再設定用のリンクをユーザーへ送信する。
     *
     * @param targetEmail 宛先メールアドレス
     * @param displayName 宛先ユーザーの表示名
     * @param resetLink   再設定画面への完全なURL（トークン付き）
     */
    @Async
    public void sendPasswordResetEmail(String targetEmail, String displayName, String resetLink) {
        String body = displayName + " 様\n\n"
                + "Tokyo Hack Group Portal のパスワード再設定リクエストを受け付けました。\n"
                + "以下のリンクから新しいパスワードを設定してください（このリンクは30分間有効です）。\n\n"
                + "▼パスワード再設定はこちら\n" + resetLink + "\n\n"
                + "心当たりがない場合は、このメールを破棄してください。パスワードは変更されません。"
                + BRAND_FOOTER;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(systemSenderEmail);
        message.setTo(targetEmail);
        message.setSubject(PASSWORD_RESET_SUBJECT_PREFIX + "リンクのご案内");
        message.setText(body);

        try {
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.err.println("メール送信処理をスキップしました: " + exception.getMessage());
        }
    }

    /**
     * 日程調整の確定時に、招待者へ確定日時を通知する。
     *
     * @param targetEmail       宛先メールアドレス
     * @param displayName       宛先ユーザーの表示名
     * @param pollId            日程調整のID（詳細ページへのリンク生成用）
     * @param pollTitle         日程調整のタイトル
     * @param confirmedDateTime 確定した開催日時
     */
    @Async
    public void sendPollConfirmedEmail(String targetEmail, String displayName, Long pollId, String pollTitle, LocalDateTime confirmedDateTime) {
        String body = displayName + " 様\n\n"
                + "Tokyo Hack Group Portal の日程調整「" + pollTitle + "」の開催日時が確定しました。\n\n"
                + "【確定日時】" + confirmedDateTime.format(DATE_TIME_FORMAT) + "\n\n"
                + "ポータルサイトのカレンダーからもご確認いただけます。\n\n"
                + "▼詳細はこちら\n" + baseUrl + "/polls/" + pollId
                + BRAND_FOOTER;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(systemSenderEmail);
        message.setTo(targetEmail);
        message.setSubject(POLL_CONFIRMED_SUBJECT_PREFIX + pollTitle);
        message.setText(body);

        try {
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.err.println("メール送信処理をスキップしました: " + exception.getMessage());
        }
    }
}
