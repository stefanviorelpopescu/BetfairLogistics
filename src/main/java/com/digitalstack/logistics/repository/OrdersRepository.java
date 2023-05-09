package com.digitalstack.logistics.repository;

import com.digitalstack.logistics.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrdersRepository extends CrudRepository<Order, Long>
{
}
