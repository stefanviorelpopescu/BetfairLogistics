package com.digitalstack.logistics.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Destination
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
