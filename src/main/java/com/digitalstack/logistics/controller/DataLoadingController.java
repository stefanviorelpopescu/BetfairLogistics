package com.digitalstack.logistics.controller;

import com.digitalstack.logistics.service.DataLoadingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/load")
public class DataLoadingController
{
    public final DataLoadingService dataLoadingService;

    public DataLoadingController(DataLoadingService dataLoadingService)
    {
        this.dataLoadingService = dataLoadingService;
    }

    @GetMapping
    public ResponseEntity<String> loadData() throws IOException
    {
        return new ResponseEntity<>(dataLoadingService.loadCsvData(), HttpStatus.OK);
    }

}
