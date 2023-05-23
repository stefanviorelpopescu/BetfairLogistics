package com.digitalstack.logistics.controller;

import com.digitalstack.logistics.service.ShippingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipping")
public class ShippingController
{
    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService)
    {
        this.shippingService = shippingService;
    }

    @PostMapping("/new-day")
    public String startShipping() {
        return shippingService.newDay();
    }

}
