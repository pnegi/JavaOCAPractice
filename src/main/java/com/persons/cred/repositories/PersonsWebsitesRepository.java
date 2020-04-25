package com.persons.cred.repositories;

import com.persons.cred.entiities.PersonsWebsites;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonsWebsitesRepository extends JpaRepository<PersonsWebsites, Integer> {
}
