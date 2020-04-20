package com.persons.cred.repositories;

import com.persons.cred.entiities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
}
