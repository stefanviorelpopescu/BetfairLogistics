package com.digitalstack.logistics.entity;

import com.digitalstack.logistics.helpers.OrderStatus;
import jakarta.persistence.*;

@Table(name = "orders")
@Entity
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name="destination_id", nullable=false)
    private Destination destination;

    private Long deliveryDate;

    private Long lastUpdated;
}
