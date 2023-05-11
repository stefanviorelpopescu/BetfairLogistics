package com.digitalstack.logistics.model.entity;

import com.digitalstack.logistics.helpers.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "orders")
@Entity
@Getter
@Setter
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name="destination_id")
    private Destination destination;

    private Long deliveryDate;

    private Long lastUpdated;
}
