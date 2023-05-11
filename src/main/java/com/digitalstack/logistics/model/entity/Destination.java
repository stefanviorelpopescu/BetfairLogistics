package com.digitalstack.logistics.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Destination
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private Integer distance;

    public Destination(String name, Integer distance)
    {
        this.name = name;
        this.distance = distance;
    }

    public Destination()
    {

    }
}
