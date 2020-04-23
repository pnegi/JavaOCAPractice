package com.persons.cred.controller;

import com.persons.cred.services.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class DataLoaderController {

    @Autowired
    private DataLoaderService dataLoaderService;

    //Add a controller to upload excel
    @GetMapping("/read")
    public String  readExcel() throws IOException {
        Resource resource = new ClassPathResource("Codes.xlsx");
        InputStream input = resource.getInputStream();
        dataLoaderService.saveDataFromInputToDb(input);
        return "success";
    }




}
