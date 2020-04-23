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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class DataLoaderController {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private WebsiteRepository websiteRepository;
    //Add a controller to upload excel
    @GetMapping("/read")
    public void readExcel() throws IOException {
        FileInputStream fis = new FileInputStream(new File("/Users/preetinegi/IdeaProjects/cred/src/main/java/com/persons/cred/controller/Codes.xlsx"));
        try (XSSFWorkbook workbook = new XSSFWorkbook(fis)){
            XSSFSheet spreadsheet = workbook.getSheetAt(2);
            Iterator<Row> rowIterator = spreadsheet.iterator();
            List<Person> personList = personRepository.findAll();
            while (rowIterator.hasNext()) {
                XSSFRow row = (XSSFRow) rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while ( cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int cellColumnIndex= cell.getColumnIndex();
                    if(cellColumnIndex<3){
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

                        if (cellColumnIndex%2!=0 && stringCellValue!=null && !stringCellValue.isEmpty() && stringCellValue.contains("negi")){
                            //get cell at index 0 to get website name,
                            String websiteName = row.getCell(0).getStringCellValue();
                            //get person preeti object
                            Optional<Person> person = personList.stream().filter(p -> p.getLastName().toLowerCase().contains("negi")).findFirst();
                            // match with website db if name exists in db
                            List<WebSite> webSiteList = websiteRepository.findByNameContainingIgnoreCase(websiteName);
                            //if empty, create new website
                            if (webSiteList.isEmpty()){
                                WebSite webSite = WebSite.builder().name(websiteName).url(websiteName).alias(websiteName).build();
                                websiteRepository.save(webSite);
                                webSiteList.add(webSite);
                            }
                            //map website against person
                            person.ifPresent(prsn -> prsn.getRelatedWebsites().add(webSiteList.stream().findFirst().orElse(null)));
                        }

                    }
                }
                log.info("\n");
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
  fis.close();
    }
}
