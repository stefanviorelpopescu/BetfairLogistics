package com.digitalstack.logistics.service;

import com.digitalstack.logistics.company_manager.CompanyManager;
import com.digitalstack.logistics.helpers.InvalidOrderDtoException;
import com.digitalstack.logistics.helpers.OrderStatus;
import com.digitalstack.logistics.model.converter.OrderConverter;
import com.digitalstack.logistics.model.dto.CancelOrdersResponse;
import com.digitalstack.logistics.model.dto.OrderDto;
import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;
import com.digitalstack.logistics.repository.DestinationRepository;
import com.digitalstack.logistics.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.digitalstack.logistics.helpers.OrderStatus.*;

@Service
public class OrdersService
{
    private final List<OrderStatus> statusesValidForCancel = Arrays.asList(NEW, DELIVERING);

    private final OrdersRepository ordersRepository;
    private final DestinationRepository destinationRepository;
    private final CompanyManager companyManager;

    public OrdersService(OrdersRepository ordersRepository, DestinationRepository destinationRepository, CompanyManager companyManager)
    {
        this.ordersRepository = ordersRepository;
        this.destinationRepository = destinationRepository;
        this.companyManager = companyManager;
    }

    @Transactional
    public List<OrderDto> addOrders(List<OrderDto> requestBody) throws InvalidOrderDtoException
    {
        List<Order> orders = new ArrayList<>();

        for (OrderDto orderDto : requestBody)
        {
            orders.add(addOrder(orderDto));
        }
        ordersRepository.saveAll(orders);

        return OrderConverter.fromOderModels(orders);
    }

    private Order addOrder(OrderDto orderDto) throws InvalidOrderDtoException
    {
        long dateLong = OrderConverter.deliveryDateFromStringToLong(orderDto.getDeliveryDate());
        long applicationDate = companyManager.getCurrentDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();

        if (dateLong <= applicationDate) {
            throw new InvalidOrderDtoException("Order Date should be greater than current date!" + orderDto.getDeliveryDate());
        }

        Destination destination = destinationRepository.findById(orderDto.getDestinationId())
                .orElseThrow(() -> new InvalidOrderDtoException("Invalid destination ID: " + orderDto.getDestinationId()));

        Order newOrder = new Order();
        newOrder.setStatus(NEW);
        newOrder.setDestination(destination);
        newOrder.setDeliveryDate(OrderConverter.deliveryDateFromStringToLong(orderDto.getDeliveryDate()));
        newOrder.setLastUpdated(System.currentTimeMillis());

        return newOrder;
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
