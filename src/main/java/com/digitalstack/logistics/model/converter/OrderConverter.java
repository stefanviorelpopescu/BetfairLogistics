package com.digitalstack.logistics.model.converter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class OrderConverter
{
    private static final String dateFormat = "dd-MM-yyyy";
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
}
