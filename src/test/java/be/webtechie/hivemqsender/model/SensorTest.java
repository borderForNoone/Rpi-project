package be.webtechie.hivemqsender.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SensorTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private final String json = "{\"temperature\":20.45,\"humidity\":30.56}";

    @Test
    void toJson() throws JsonProcessingException {
        var sensor = new Sensor();
        sensor.setTemperature(20.45);
        sensor.setHumidity(30.56);
        assertEquals(json, mapper.writeValueAsString(sensor));
    }

    @Test
    void toObject() throws JsonProcessingException {
        var sensor = mapper.readValue(json, Sensor.class);
        assertAll(
                () -> assertEquals(20.45, sensor.getTemperature()),
                () -> assertEquals(30.56, sensor.getHumidity())
        );
    }
}