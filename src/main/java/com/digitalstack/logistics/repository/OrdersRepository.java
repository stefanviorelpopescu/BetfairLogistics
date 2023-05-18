package com.digitalstack.logistics.repository;

import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrdersRepository extends CrudRepository<Order, Long>
{
    List<Order> findAllByDestination(Destination destination);

    List<Order> findAllByDeliveryDate(Long deliveryDate);

    List<Order> findAllByDeliveryDateAndDestination(Long deliveryDate, Destination destination);
}
