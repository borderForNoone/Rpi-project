package be.webtechie.hivemqsender;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Sensor {
    public static void main(String[] args) throws InterruptedException {
        final String host = "0d771434a61e40989564352d53beb88e.s2.eu.hivemq.cloud";
        final String username = "denis";
        final String password = "";

        // 1. create the client
        final Mqtt5Client client = Mqtt5Client.builder()
                .identifier("sensor-" + 1)
                .serverHost(host)
                .automaticReconnectWithDefaultConfig()
                .serverPort(8883)
                .sslWithDefaultConfig()
                .build();

        // 2. connect the client
        client.toBlocking().connectWith()
                .simpleAuth()
                .username(username)
                .password(password.getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .willPublish()
                .topic("home/will")
                .payload("sensor gone".getBytes())
                .applyWillPublish()
                .send();

        // 3. publishing of sensor data
        while (true) {
            client.toBlocking().publishWith()
                    .topic("home/temperature")
                    .payload(getTemperature())
                    .send();

            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    private static byte[] getTemperature() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "/home/denis/Desktop/dht.py");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // Parse the JSON string and get values
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(output.toString());
                double temperature = jsonNode.get("temperature").asDouble();
                double humidity = jsonNode.get("humidity").asDouble();

                System.out.println("Temperature: " + temperature + "°C");
                System.out.println("Humidity: " + humidity);

                // Return temperature value as MQTT payload
                return (temperature + "°C").getBytes(StandardCharsets.UTF_8);
            } catch (IOException e) {
                // Handle exception if there are issues with JSON parsing
                e.printStackTrace();
            }

            reader.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            // Handle exception if there are issues with running the Python script
            e.printStackTrace();
        }

        // In case of an error, return an empty payload
        return new byte[0];
    }
}
