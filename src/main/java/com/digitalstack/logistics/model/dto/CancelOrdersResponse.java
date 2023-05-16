package com.digitalstack.logistics.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CancelOrdersResponse
{
    private List<Long> success = new ArrayList<>();
    private List<Long> failed = new ArrayList<>();
}
