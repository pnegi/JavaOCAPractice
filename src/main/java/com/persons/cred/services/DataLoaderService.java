package com.persons.cred.services;

import com.persons.cred.entiities.Person;
import com.persons.cred.entiities.WebSite;
import com.persons.cred.repositories.PersonRepository;
import com.persons.cred.repositories.WebsiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

            List<Person> personList = personRepository.findAll();

            while (rowIterator.hasNext()) {
                XSSFRow row = (XSSFRow) rowIterator.next();
                //Website = storeWebsite(website cell)
                //mapWebsiteToUser(website, usercell, passwordcell);

                Iterator<Cell> cellIterator = row.cellIterator();
                while ( cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    int cellColumnIndex= cell.getColumnIndex();
                    if(cell.getColumnIndex()<3){
                        double numCellValue;
                        String stringCellValue = null;
                        switch (cell.getCellType()) {
                            case NUMERIC:
                                numCellValue = cell.getNumericCellValue();
                                break;
                            case STRING:
                                stringCellValue = cell.getStringCellValue();
                                break;
                            default:
                        }

                        addWebsiteToPerson(personList, row, cellColumnIndex, stringCellValue);

                    }
                }
                log.info("\n");
            }

            personRepository.saveAll(personList);
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public void addWebsiteToPerson(List<Person> personList, XSSFRow row, int cellColumnIndex, String stringCellValue) {
        //TODO: try to use patter matches from this link http://tutorials.jenkov.com/java-regex/pattern.html
        List<WebSite> webSiteList = getWebSites(row);
        //cellColumnIndex%2!=0 means only for usernames(they are at odd columns)
        if (cellColumnIndex%2!=0 && stringCellValue!=null && !stringCellValue.isEmpty() && stringCellValue.contains("negi")){
            //map website against respective person
            Optional<Person> person = personList.stream().filter(p -> p.getLastName().toLowerCase().contains("negi")).findFirst();
            person.ifPresent(prsn -> prsn.getRelatedWebsites().add(webSiteList.stream().findFirst().orElse(null)));
        }
    }

    public List<WebSite> getWebSites(XSSFRow row) {
        //get cell at index 0 to get website name,
        String websiteName = row.getCell(0).getStringCellValue();
        // get above found website from db
        List<WebSite> webSiteList = websiteRepository.findByNameContainingIgnoreCase(websiteName);
        //if empty, create new website
        if (webSiteList.isEmpty()){
            WebSite webSite = WebSite.builder().name(websiteName).url(websiteName).alias(websiteName).build();
            websiteRepository.save(webSite);
            webSiteList.add(webSite);
        }
        return webSiteList;
    }
}
