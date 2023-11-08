package com.project.transfers.converter.date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class StringMillisToLocalDateTimeDeserializer implements Converter<String, LocalDateTime> {
    @Override
    public LocalDateTime convert(String source) {
        long decode = Long.parseLong(source);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(decode), ZoneOffset.UTC);
    }
}
