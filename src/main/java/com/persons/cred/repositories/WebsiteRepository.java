package com.persons.cred.repositories;

import com.persons.cred.entiities.WebSite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteRepository extends JpaRepository<WebSite, Integer> {
}
