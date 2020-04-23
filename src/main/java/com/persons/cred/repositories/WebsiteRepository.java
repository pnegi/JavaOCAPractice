package com.persons.cred.repositories;

import com.persons.cred.entiities.WebSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebsiteRepository extends JpaRepository<WebSite, Integer> {
    List<WebSite> findByNameContainingIgnoreCase(String websiteNameLike);
}
