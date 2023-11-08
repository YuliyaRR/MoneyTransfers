package com.project.transfers.converter.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@JsonComponent
public class LocalDateTimeToLongMillisSerializer extends StdSerializer<LocalDateTime> {
    public LocalDateTimeToLongMillisSerializer() {
        this(null);
    }

    public LocalDateTimeToLongMillisSerializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        long epochMilli = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        jsonGenerator.writeNumber(epochMilli);

    }
}




