package com.persons.cred.entiities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSite {
    @Id
    @Column
    private int id;
    @Column
    private String name;
    @Column
    private String url;
    @Column
    private String alias;
    @ManyToMany(mappedBy = "relatedWebsites")
    Set<Person> relatedPersons;
}
