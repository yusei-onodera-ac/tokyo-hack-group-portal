// --- ExternalLinkRepository.java ---
package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.link.ExternalLink;

@Repository
public interface ExternalLinkRepository extends JpaRepository<ExternalLink, Long> {
}