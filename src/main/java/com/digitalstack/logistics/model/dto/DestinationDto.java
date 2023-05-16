package com.digitalstack.logistics.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DestinationDto
{
    private Long id;

    @NotBlank(message = "Destination name is required!")
    private String name;

    @NotNull(message = "Distance to destination must be set!")
    @Min(value = 1, message = "Distance must be positive integer!")
    private int distance;

//    @AssertTrue(message = "distance invalid")
//    public boolean isDistanceValid() {
//        return distance + name.length() > 15;
//    }
}
