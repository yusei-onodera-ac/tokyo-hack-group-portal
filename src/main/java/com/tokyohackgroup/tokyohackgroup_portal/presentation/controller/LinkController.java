package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.ExternalLinkService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.link.ExternalLink;

@Controller
@RequestMapping("/links")
public class LinkController {

    private static final String VIEW_LINK_LIST = "link/list";
    private static final String REDIRECT_LINK_LIST = "redirect:/links";
    private static final String MODEL_KEY_LINK_LIST = "externalLinkList";

    private final ExternalLinkService externalLinkService;

    public LinkController(ExternalLinkService externalLinkService) {
        this.externalLinkService = externalLinkService;
    }

    @GetMapping
    public String showLinkList(Model model) {
        List<ExternalLink> links = externalLinkService.fetchAllLinks();
        model.addAttribute(MODEL_KEY_LINK_LIST, links);
        return VIEW_LINK_LIST;
    }

    @PostMapping("/new")
    public String processRegisterLink(@RequestParam("serviceName") String serviceName, @RequestParam("urlAddress") String urlAddress) {
        externalLinkService.registerNewLink(serviceName, urlAddress);
        // 二重送信を防止するため、更新後は必ずリダイレクトさせる
        return REDIRECT_LINK_LIST;
    }

    @PostMapping("/delete")
    public String processDeleteLink(@RequestParam("linkId") Long linkId) {
        externalLinkService.removeLink(linkId);
        return REDIRECT_LINK_LIST;
    }
}