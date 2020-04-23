package com.persons.cred.controller;

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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class DataLoaderController {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private WebsiteRepository websiteRepository;
    //Add a controller to upload excel
    @GetMapping("/read")
    public String  readExcel() throws IOException {

        Resource resource = new ClassPathResource("Codes.xlsx");

        InputStream input = resource.getInputStream();

        try (XSSFWorkbook workbook = new XSSFWorkbook(input)){
            XSSFSheet spreadsheet = workbook.getSheetAt(2);
            Iterator<Row> rowIterator = spreadsheet.iterator();

            List<Person> personList = personRepository.findAll();

            while (rowIterator.hasNext()) {
                XSSFRow row = (XSSFRow) rowIterator.next();
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
                                break;
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
        return "success";
    }

    private void addWebsiteToPerson(List<Person> personList, XSSFRow row, int cellColumnIndex, String stringCellValue) {
        //TODO: try to use patter matches from this link http://tutorials.jenkov.com/java-regex/pattern.html

        if (cellColumnIndex%2!=0 && stringCellValue!=null && !stringCellValue.isEmpty() && stringCellValue.contains("negi")){
            //get cell at index 0 to get website name,
            String websiteName = row.getCell(0).getStringCellValue();
            // match with website db if name exists in db
            List<WebSite> webSiteList = getWebSites(websiteName);
            //map website against person
            Optional<Person> person = personList.stream().filter(p -> p.getLastName().toLowerCase().contains("negi")).findFirst();
            person.ifPresent(prsn -> prsn.getRelatedWebsites().add(webSiteList.stream().findFirst().orElse(null)));
        }
    }

    private List<WebSite> getWebSites(String websiteName) {
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
