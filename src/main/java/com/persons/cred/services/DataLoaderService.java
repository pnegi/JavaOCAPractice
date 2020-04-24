package com.persons.cred.services;

import com.persons.cred.entiities.Person;
import com.persons.cred.entiities.WebSite;
import com.persons.cred.repositories.PersonRepository;
import com.persons.cred.repositories.WebsiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DataLoaderService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private WebsiteRepository websiteRepository;

    public void saveDataFromInputToDb(InputStream input) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(input)){
            XSSFSheet spreadsheet = workbook.getSheetAt(2);
            Iterator<Row> rowIterator = spreadsheet.iterator();
            //get all persons from db at once and use it to map web details(in multiple rows) and then save persons(with mapped web details) at the end of iterations.
            // if we add web details from multiple rows to the same person by calling and saving person(from/in db) in every row iteration
            // we'll end up saving same person in db multiple times.
            List<Person> personList = personRepository.findAll();
            while (rowIterator.hasNext()) {
                XSSFRow row = (XSSFRow) rowIterator.next();
                List<WebSite> webSiteList = storeIfNewElseGetSitesLikeRowWebsite(row);
                mapWebsiteToPerson(webSiteList ,personList, row.getCell(1), row.getCell(2));
            }
            personRepository.saveAll(personList);
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public void mapWebsiteToPerson(List<WebSite> webSiteList , List<Person> personList, XSSFCell userId, XSSFCell pwd) {
        //TODO: try to use pattern matches from this link http://tutorials.jenkov.com/java-regex/pattern.html
        switch (userId.getCellType()){
            case NUMERIC:
            case BLANK:
            case BOOLEAN:
            case ERROR:
                break;
            case STRING:
                if (!userId.getStringCellValue().isEmpty() && userId.getStringCellValue().contains("negi")){
                    Optional<Person> person = personList.stream().filter(p -> p.getLastName().toLowerCase().contains("negi")).findFirst();
                    person.ifPresent(prsn -> prsn.getRelatedWebsites().add(webSiteList.stream().findFirst().orElse(null)));
                }
                break;
            default:
        }

    }

    public List<WebSite> storeIfNewElseGetSitesLikeRowWebsite(XSSFRow row) {
        String websiteName = row.getCell(0).getStringCellValue();
        List<WebSite> webSiteList = websiteRepository.findByNameContainingIgnoreCase(websiteName);
        if (webSiteList.isEmpty()){
            WebSite webSite = WebSite.builder().name(websiteName).url(websiteName).alias(websiteName).build();
            websiteRepository.save(webSite);
            webSiteList.add(webSite);
        }
        return webSiteList;
    }
}
