package com.persons.cred.entiities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @Column
    private int id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private Date dob;
    @Column
    private String email;
    @ManyToMany
    @JoinTable(
            name = "persons_related_websites",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "website_id"))
    Set<WebSite> relatedWebsites;
}
