package com.digitalstack.logistics.model.converter;

import com.digitalstack.logistics.model.dto.OrderDto;
import com.digitalstack.logistics.model.entity.Order;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrderConverter
{
    private static final String dateFormat = "dd-MM-yyyy";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

    public static long deliveryDateFromStringToLong(String dateString) {
        return LocalDate.parse(dateString, formatter).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static String deliveryDateFromLongToString(long deliveryDate) {
        return Instant.ofEpochMilli(deliveryDate).atZone(ZoneOffset.UTC).toLocalDate().format(formatter);
    }

    public static OrderDto fromOrderModel(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setOrderStatus(order.getStatus());
        orderDto.setDestinationId(order.getDestination().getId());
        orderDto.setDeliveryDate(deliveryDateFromLongToString(order.getDeliveryDate()));
        orderDto.setLastUpdated(deliveryDateFromLongToString(order.getLastUpdated()));
        return orderDto;
    }

    public static List<OrderDto> fromOderModels(List<Order> orders) {
        return orders.stream()
                .map(OrderConverter::fromOrderModel)
                .collect(Collectors.toList());
    }
}
