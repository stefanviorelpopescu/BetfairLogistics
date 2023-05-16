package com.digitalstack.logistics.service;

import com.digitalstack.logistics.helpers.OrderStatus;
import com.digitalstack.logistics.model.dto.CancelOrdersResponse;
import com.digitalstack.logistics.model.dto.OrderDto;
import com.digitalstack.logistics.model.entity.Order;
import com.digitalstack.logistics.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.digitalstack.logistics.helpers.OrderStatus.*;

@Service
public class OrdersService
{
    private final List<OrderStatus> statusesValidForCancel = Arrays.asList(NEW, DELIVERING);

    private final OrdersRepository ordersRepository;

    public OrdersService(OrdersRepository ordersRepository)
    {
        this.ordersRepository = ordersRepository;
    }

    public ResponseEntity<String> addOrders(List<OrderDto> requestBody)
    {
        return null;
    }

    @Transactional
    public CancelOrdersResponse cancelOrders(List<Long> idsToDelete)
    {
        CancelOrdersResponse response = new CancelOrdersResponse();
        List<Long> notFound = new ArrayList<>(idsToDelete);

        Iterable<Order> foundOrders = ordersRepository.findAllById(idsToDelete);
        for (Order foundOrder : foundOrders)
        {
            if (statusesValidForCancel.contains(foundOrder.getStatus())) {
                foundOrder.setStatus(CANCELED);
                foundOrder.setLastUpdated(System.currentTimeMillis());
                response.getSuccess().add(foundOrder.getId());
            } else {
                response.getFailed().add(foundOrder.getId());
            }
            notFound.remove(foundOrder.getId());
        }
        ordersRepository.saveAll(foundOrders);

        response.getFailed().addAll(notFound);

        return response;
    }
}
