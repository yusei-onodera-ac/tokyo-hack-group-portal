package com.tokyohackgroup.tokyohackgroup_portal.presentation.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.SystemSettingService;

/**
 * 全ページ共通のヘッダー（サイドバー）で使用する属性をモデルへ注入するアドバイス。
 *
 * <p>header.jsp は各画面から {@code <%@ include %>} で静的インクルードされるため、
 * ここで注入した属性はリクエストスコープ属性として header.jsp からも参照できる。</p>
 */
@ControllerAdvice
public class GlobalModelAttributeAdvice {

    private final SystemSettingService systemSettingService;

    public GlobalModelAttributeAdvice(SystemSettingService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @ModelAttribute("appIconStoredFileName")
    public String appIconStoredFileName() {
        return systemSettingService.getAppIconStoredFileName();
    }
}
