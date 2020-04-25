package com.persons.cred.entiities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonsWebsites implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "website_id")
    WebSite webSite;

    @Column
    String userId;

    @Column
    String password;
}
