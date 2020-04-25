package com.persons.cred.entiities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private String url;

    @Column
    private String alias;

    @OneToMany(mappedBy = "webSite")
    List<PersonsWebsites> personsWebsitesList;
}
