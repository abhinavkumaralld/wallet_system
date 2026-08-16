package com.abhi.auth.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class MaskAttribute extends JsonSerializer<String> {
    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(mask(s));
    }

    public String mask(String value){
        if (value == null || value.length() < 4) {
            return "****";
        }
        int visible = 4;
        String masked = "*".repeat(value.length() - visible) + value.substring(value.length() - visible);
        return masked;
    }
}
