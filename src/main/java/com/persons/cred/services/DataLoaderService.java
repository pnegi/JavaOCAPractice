package com.persons.cred.services;

import com.persons.cred.entiities.Person;
import com.persons.cred.entiities.PersonsWebsites;
import com.persons.cred.entiities.WebSite;
import com.persons.cred.repositories.PersonRepository;
import com.persons.cred.repositories.PersonsWebsitesRepository;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.persons.cred.CommonConstants.CHAN_PATTERN_STRING;
import static com.persons.cred.CommonConstants.PREE_PATTERN_STRING;
import static java.util.regex.Pattern.matches;

@Service
@Slf4j
public class DataLoaderService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private WebsiteRepository websiteRepository;
    @Autowired
    PersonsWebsitesRepository personsWebsitesRepository;


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
                if(!webSiteList.isEmpty()) {
                    String userIdValue = getStringValue(row.getCell(1));
                    String pwdValue = getStringValue(row.getCell(2));
                    if (userIdValue!=null && pwdValue!=null)
                        mapPersonWithWebsiteCredentials(webSiteList, personList, userIdValue, pwdValue);
                }
            }
        } catch (Exception e){
            log.error(e.getMessage() + e.getCause());
        }
    }

    private String getStringValue(XSSFCell cell) {

        String value = null;
        switch (cell.getCellType()) {
            case NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case STRING:
                value = cell.getStringCellValue().toLowerCase();
                break;
            case BLANK:
            case BOOLEAN:
            case ERROR:
                break;
            default:
        }
        return value;
    }

    public List<WebSite> storeIfNewElseGetSitesLikeRowWebsite(XSSFRow row) {
        List<WebSite> webSiteList = new ArrayList<>();
        if(row.getCell(0) != null) {
            String websiteName = row.getCell(0).getStringCellValue();
            webSiteList = websiteRepository.findByNameContainingIgnoreCase(websiteName);
            if (webSiteList.isEmpty()){
            WebSite webSite = WebSite.builder().name(websiteName).url(websiteName).alias(websiteName).build();
            websiteRepository.save(webSite);
            webSiteList.add(webSite);
        }
        }
        return webSiteList;
    }
    private void mapPersonWithWebsiteCredentials(List<WebSite> webSiteList, List<Person> personList, String userIdValue, String pwdValue) {
        Optional<Person> person = getPerson(personList, userIdValue);
        if(person.isPresent()){
            PersonsWebsites personsWebsites = PersonsWebsites.builder()
                    .webSite(webSiteList.stream().findFirst().orElse(null))
                    .person(person.get())
                    .userId(userIdValue)
                    .password(pwdValue)
                    .build();
            personsWebsitesRepository.save(personsWebsites);
        }else{
        log.info("Person not found for userId? {}",userIdValue);
        }
    }

    private Optional<Person> getPerson(List<Person> personList, String userIdValue) {
        Optional<Person> person = Optional.empty();
        if (matches(PREE_PATTERN_STRING, userIdValue))
            person = personList.stream().filter(p -> p.getFirstName().toLowerCase().matches(PREE_PATTERN_STRING)).findFirst();
        if (matches(CHAN_PATTERN_STRING, userIdValue))
            person = personList.stream().filter(p -> p.getFirstName().toLowerCase().matches(CHAN_PATTERN_STRING)).findFirst();
        return person;
    }
}
