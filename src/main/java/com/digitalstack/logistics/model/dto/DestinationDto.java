package com.digitalstack.logistics.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
public class DestinationDto
{
    private Long id;

    @NotBlank
    private String name;

    @NonNull
    @Min(1)
    private int distance;

//    @AssertTrue(message = "distance invalid")
//    public boolean isDistanceValid() {
//        return distance + name.length() > 15;
//    }
}
