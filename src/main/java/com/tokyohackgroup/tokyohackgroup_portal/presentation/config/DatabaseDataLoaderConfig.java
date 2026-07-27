package com.tokyohackgroup.tokyohackgroup_portal.presentation.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.GroupService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserRole;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.group.Group;

/**
 * 開発テスト用シードデータを暗号化パスワードで投入する初期化クラス。
 */
@Configuration
public class DatabaseDataLoaderConfig {

    private static final String INITIAL_ADMIN_EMAIL = "system.admin@tokyohackgroup.com";
    private static final String INITIAL_ADMIN_RAW_PASSWORD = "password123";
    private static final String INITIAL_ADMIN_NAME = "東京ハック太郎";

    @Bean
    public CommandLineRunner initializeDatabase(UserService userService, GroupService groupService, PasswordEncoder passwordEncoder) {
        return args -> {
            UserAccount initialAdminUser = userService.findActiveUserByEmail(INITIAL_ADMIN_EMAIL).orElse(null);

            if (initialAdminUser == null) {
                // 生パスワードをBCryptでハッシュ化してからDBに登録する
                String hashedPassword = passwordEncoder.encode(INITIAL_ADMIN_RAW_PASSWORD);

                initialAdminUser = new UserAccount(
                        INITIAL_ADMIN_EMAIL,
                        hashedPassword,
                        INITIAL_ADMIN_NAME,
                        UserRole.ADMINISTRATOR
                );
                userService.registerUser(initialAdminUser);
            }

            if (groupService.fetchGroupsForUser(initialAdminUser).isEmpty()) {
                Group portalDevGroup = groupService.registerGroup("ポータル開発チーム", "Tokyo Hack Group Portalの開発・運用を行うチーム");
                groupService.addMember(portalDevGroup.getId(), initialAdminUser);

                Group hackathonGroup = groupService.registerGroup("ハッカソン運営チーム", "定期開催ハッカソンの企画・運営を行うチーム");
                groupService.addMember(hackathonGroup.getId(), initialAdminUser);
            }
        };
    }
}