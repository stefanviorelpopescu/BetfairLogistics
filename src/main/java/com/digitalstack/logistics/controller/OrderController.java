package com.digitalstack.logistics.controller;

import com.digitalstack.logistics.model.dto.CancelOrdersResponse;
import com.digitalstack.logistics.model.dto.OrderDto;
import com.digitalstack.logistics.service.OrdersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController
{
    private final OrdersService ordersService;

    public OrderController(OrdersService ordersService)
    {
        this.ordersService = ordersService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addOrders(@RequestBody List<@Valid OrderDto> requestBody) {
        return ordersService.addOrders(requestBody);
    }

    @PostMapping("/cancel")
    public CancelOrdersResponse cancelOrders(@RequestBody List<Long> idsToDelete) {
        return ordersService.cancelOrders(idsToDelete);
    }
}
