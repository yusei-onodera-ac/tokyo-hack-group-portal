package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserRole;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.UserAccountRepository;

/**
 * ユーザーアカウントの登録・検索・アクティブ判定・管理者操作を統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    /** 管理者設定画面での1ページあたりの表示件数 */
    public static final int ADMIN_PAGE_SIZE = 15;

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificationService emailNotificationService;

    public UserService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            EmailNotificationService emailNotificationService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailNotificationService = emailNotificationService;
    }

    /**
     * メールアドレスを指定して有効なユーザーを取得する。
     *
     * @param emailAddress 検索対象のメールアドレス
     * @return 該当するユーザーアカウント（存在しない場合は empty）
     */
    public Optional<UserAccount> findActiveUserByEmail(String emailAddress) {
        return userAccountRepository.findByEmailAddress(emailAddress);
    }

    /**
     * IDを指定してユーザーを取得する。
     *
     * @param userId 検索対象のユーザーID
     * @return 該当するユーザーアカウント（存在しない場合は empty）
     */
    public Optional<UserAccount> findById(Long userId) {
        return userAccountRepository.findById(userId);
    }

    /**
     * マイページからの表示名変更を反映する。
     *
     * @param userId         対象ユーザーID
     * @param newDisplayName 変更後の表示名
     * @return 更新後のユーザーアカウント
     */
    @Transactional
    public UserAccount updateDisplayName(Long userId, String newDisplayName) {
        UserAccount targetUser = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたユーザーが見つかりません。ID: " + userId));

        targetUser.changeDisplayName(newDisplayName);
        return userAccountRepository.save(targetUser);
    }

    /**
     * 現在のパスワードを検証したうえでパスワードを変更する。
     *
     * @param userId          対象ユーザーID
     * @param currentRawPassword 検証用の現在のパスワード（平文）
     * @param newRawPassword     変更後の新しいパスワード（平文）
     * @return 変更に成功した場合は更新後のユーザーアカウント、現パスワードが一致しない場合は empty
     */
    @Transactional
    public Optional<UserAccount> changePassword(Long userId, String currentRawPassword, String newRawPassword) {
        UserAccount targetUser = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたユーザーが見つかりません。ID: " + userId));

        if (!passwordEncoder.matches(currentRawPassword, targetUser.getEncryptedPassword())) {
            return Optional.empty();
        }

        targetUser.changeEncryptedPassword(passwordEncoder.encode(newRawPassword));
        return Optional.of(userAccountRepository.save(targetUser));
    }

    /**
     * 新規ユーザーを登録する。
     *
     * @param userAccount 登録するユーザーオブジェクト
     */
    @Transactional
    public void registerUser(UserAccount userAccount) {
        userAccountRepository.save(userAccount);
    }

    /**
     * システムに登録されている有効（アクティブ）な全ユーザー一覧を取得する。
     *
     * <p>退会済みアカウントの個人情報露出を防ぐため、isActive が true のユーザーのみを抽出する。
     * Stream APIによる型エラーを回避し、標準的な ArrayList 走査を採用する。</p>
     *
     * @return アクティブなユーザーアカウントのリスト
     */
    public List<UserAccount> fetchAllActiveUsers() {
        List<UserAccount> allUserAccounts = userAccountRepository.findAll();
        List<UserAccount> activeUserAccounts = new ArrayList<>();

        for (UserAccount userAccount : allUserAccounts) {
            // 肯定形メソッド（isActive）を使用し、条件判定の意図を明瞭にする
            if (userAccount.isActive()) {
                activeUserAccounts.add(userAccount);
            }
        }

        return activeUserAccounts;
    }

    /**
     * 管理者設定画面向けに、表示名・メールアドレスで絞り込んだ全ユーザー（無効化済み含む）をページング取得する。
     */
    public Page<UserAccount> fetchUsersPage(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(Math.max(pageNumber, 0), ADMIN_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (keyword == null || keyword.isBlank()) {
            return userAccountRepository.findAll(pageable);
        }
        return userAccountRepository.findByDisplayNameContainingIgnoreCaseOrEmailAddressContainingIgnoreCase(keyword, keyword, pageable);
    }

    /**
     * ユーザーのロールを変更する。自分自身のロールを変更することはロックアウト防止のため禁止する。
     */
    @Transactional
    public void changeRole(Long targetUserId, UserRole newRole, Long actingUserId) {
        if (targetUserId.equals(actingUserId)) {
            throw new IllegalStateException("自分自身のロールは変更できません。");
        }

        UserAccount targetUser = userAccountRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたユーザーが見つかりません。ID: " + targetUserId));

        targetUser.changeRole(newRole);
        userAccountRepository.save(targetUser);
    }

    /**
     * ユーザーの有効/無効（Ban）状態をトグルする。自分自身は無効化できない。
     */
    @Transactional
    public void toggleActiveStatus(Long targetUserId, Long actingUserId) {
        if (targetUserId.equals(actingUserId)) {
            throw new IllegalStateException("自分自身のアカウントは無効化できません。");
        }

        UserAccount targetUser = userAccountRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたユーザーが見つかりません。ID: " + targetUserId));

        if (targetUser.isActive()) {
            targetUser.deactivate();
        } else {
            targetUser.activate();
        }
        userAccountRepository.save(targetUser);
    }

    /**
     * 新規ユーザーを仮パスワード付きで招待登録し、招待メールを送信する。
     *
     * @return 発行した仮パスワード（平文、呼び出し元の監査ログ等での参照用）
     */
    @Transactional
    public String inviteUser(String emailAddress, String displayName, UserRole role) {
        if (userAccountRepository.existsByEmailAddress(emailAddress)) {
            throw new IllegalStateException("このメールアドレスは既に登録されています。");
        }

        String temporaryRawPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        UserAccount invitedUser = new UserAccount(emailAddress, passwordEncoder.encode(temporaryRawPassword), displayName, role);
        userAccountRepository.save(invitedUser);

        emailNotificationService.sendInviteEmail(emailAddress, displayName, temporaryRawPassword);

        return temporaryRawPassword;
    }
}