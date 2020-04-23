package com.persons.cred;

import com.persons.cred.entiities.Person;
import com.persons.cred.repositories.PersonRepository;
import com.persons.cred.repositories.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class CredApplication implements CommandLineRunner {

	@Autowired
	PersonRepository personRepository;
	@Autowired
	WebsiteRepository websiteRepository;

	public static void main(String[] args){
		SpringApplication.run(CredApplication.class, args);
	}

	@Override
	public void run(String... args){
		Person preeti = Person.builder().firstName("Preeti")
				.lastName("Negi")
				.email("2pnegi@gmail.com")
                .dob(LocalDate.of(1992, 3, 2))
				.build();
		Person chandra = Person.builder().firstName("Chandra Bhanu")
				.lastName("Rastogi")
				.email("rastogi.chandrabhanu@gmail.com")
                .dob(LocalDate.of(1990, 7, 4))
				.build();

		personRepository.save(preeti);
		personRepository.save(chandra);
	}
}
