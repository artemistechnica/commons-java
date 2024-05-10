package com.artemistechnica.commons.datatypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class EnvelopeTests {

    @Test
    public void testSimpleSuccessEnvelope() throws JsonProcessingException {
        String value = "SOME_VALUE";
        Envelope<String> envelope = Envelope.mkSuccess(value);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(envelope);
        System.out.println(json);
    }

    @Test
    public void testSimpleFailureEnvelope() throws JsonProcessingException {
        Envelope<String> envelope = Envelope.mkFailure("Exception thrown");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(envelope);
        System.out.println(json);
    }
}
