package com.persons.cred;

import com.persons.cred.entiities.Person;
import com.persons.cred.entiities.WebSite;
import com.persons.cred.repositories.PersonRepository;
import com.persons.cred.repositories.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
		Set<WebSite> preeWebSites = new HashSet<WebSite>();
		Set<WebSite> chanWebSites = new HashSet<WebSite>();
		Set<WebSite> aahuWebSites = new HashSet<WebSite>();
		WebSite amazonUKSite = WebSite.builder()
				.id(1)
				.name("Amazon UK")
				.url("https://www.amazon.co.uk/")
				.alias("for shopping")
				.build();
		WebSite tescoSite = WebSite.builder()
				.id(2)
				.name("Tesco")
				.url("https://www.tesco.com/")
				.alias("for groceries and shopping")
				.build();
		WebSite wishSite = WebSite.builder()
				.id(3)
				.name("Wish")
				.url("https://www.wish.com/")
				.alias("shopping made fun")
				.build();

		websiteRepository.save(amazonUKSite);
		websiteRepository.save(tescoSite);
		websiteRepository.save(wishSite);

		preeWebSites.add(amazonUKSite);
		preeWebSites.add(tescoSite);
		chanWebSites.add(tescoSite);
		Person pree = Person.builder().id(1).firstName("Pree")
				.lastName("negirastogi")
				.email("pnr@pp.com")
                .dob(LocalDate.of(1991, 8, 2))
				.relatedWebsites(preeWebSites)
				.build();
		Person chan = Person.builder().id(2).firstName("Chan")
				.lastName("nrastogi")
				.email("cbnr@pp.com")
                .dob(LocalDate.of(1990, 2, 28))
				.relatedWebsites(preeWebSites)
				.build();

		aahuWebSites.add(wishSite);
		Person aahu = Person.builder().id(3).firstName("Aahu")
				.lastName("nrastogi")
				.email("anr@pp.com")
                .dob(LocalDate.of(2019, 11, 11))
				.relatedWebsites(aahuWebSites)
				.build();
		personRepository.save(pree);
		personRepository.save(chan);
		personRepository.save(aahu);
	}
}
