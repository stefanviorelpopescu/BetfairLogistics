package com.digitalstack.logistics.model.dto;

import com.digitalstack.logistics.helpers.OrderStatus;
import com.digitalstack.logistics.model.converter.OrderConverter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Data
@NoArgsConstructor
public class OrderDto
{
    private Long id;

    private OrderStatus orderStatus;

    @NotNull(message = "Destination Id must not be null")
    @Min(value = 1, message = "destination ID must be > 0")
    private Long destinationId;

    @NotBlank(message = "DeliveryDate is required")
    private String deliveryDate;

    private String lastUpdated;

    @AssertTrue(message = "Delivery date format must be dd-MM-yyyy !!!")
    public boolean isDateFormatCorrect() {
        if (deliveryDate == null) {
            return false;
        }
        try {
            LocalDate.parse(deliveryDate, OrderConverter.formatter);
        } catch (DateTimeParseException exception) {
            return false;
        }
        return true;
    }
}
