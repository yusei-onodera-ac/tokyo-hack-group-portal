package com.tokyohackgroup.tokyohackgroup_portal.presentation.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserRole;

/**
 * 開発テスト用シードデータを暗号化パスワードで投入する初期化クラス。
 */
@Configuration
public class DatabaseDataLoaderConfig {

    private static final String INITIAL_ADMIN_EMAIL = "system.admin@tokyohackgroup.com";
    private static final String INITIAL_ADMIN_RAW_PASSWORD = "password123";
    private static final String INITIAL_ADMIN_NAME = "東京ハック太郎";

    @Bean
    public CommandLineRunner initializeDatabase(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userService.findActiveUserByEmail(INITIAL_ADMIN_EMAIL).isEmpty()) {
                // 生パスワードをBCryptでハッシュ化してからDBに登録する
                String hashedPassword = passwordEncoder.encode(INITIAL_ADMIN_RAW_PASSWORD);

                UserAccount initialAdminUser = new UserAccount(
                        INITIAL_ADMIN_EMAIL,
                        hashedPassword,
                        INITIAL_ADMIN_NAME,
                        UserRole.ADMINISTRATOR
                );
                userService.registerUser(initialAdminUser);
            }
        };
    }
}