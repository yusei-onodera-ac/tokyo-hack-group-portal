// --- ExternalLinkService.java ---
package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.link.ExternalLink;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.ExternalLinkRepository;

/**
 * 外部リンクに関するビジネスロジックを統合管理するサービス。
 */
@Service
@Transactional(readOnly = true)
public class ExternalLinkService {

    private final ExternalLinkRepository externalLinkRepository;

    public ExternalLinkService(ExternalLinkRepository externalLinkRepository) {
        this.externalLinkRepository = externalLinkRepository;
    }

    public List<ExternalLink> fetchAllLinks() {
        return externalLinkRepository.findAll();
    }

    @Transactional
    public void registerNewLink(String serviceName, String urlAddress) {
        ExternalLink newLink = new ExternalLink(serviceName, urlAddress);
        externalLinkRepository.save(newLink);
    }
    
    @Transactional
    public void removeLink(Long linkId) {
        externalLinkRepository.deleteById(linkId);
    }
}