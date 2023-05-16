package com.digitalstack.logistics.controller;

import com.digitalstack.logistics.helpers.InvalidDestinationDtoException;
import com.digitalstack.logistics.model.dto.DestinationDto;
import com.digitalstack.logistics.service.DestinationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/destinations")
public class DestinationController
{

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService)
    {
        this.destinationService = destinationService;
    }

    @GetMapping
    public List<DestinationDto> getAllDestinations() {
        return destinationService.getAllDestinations();
    }

    @GetMapping("/{id}")
    public DestinationDto getDestinationById(@PathVariable(name = "id") Long destinationId) {
        return destinationService.getDestinationById(destinationId);
    }

    @PostMapping("/add")
    public ResponseEntity<DestinationDto> addDestination(@Valid @RequestBody DestinationDto body) throws InvalidDestinationDtoException
    {
        DestinationDto response = destinationService.addDestination(body);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<DestinationDto> updateDestination(@Valid @RequestBody DestinationDto body) throws InvalidDestinationDtoException
    {
        DestinationDto response = destinationService.updateDestination(body);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteDestinationById(@PathVariable Long id) {
        destinationService.deleteDestinationById(id);
    }

}
