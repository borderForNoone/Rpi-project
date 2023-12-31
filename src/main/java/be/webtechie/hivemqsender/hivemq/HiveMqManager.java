package be.webtechie.hivemqsender.hivemq;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.pi4j.util.Console;

import java.util.UUID;

public class HiveMqManager {
    private static final String HIVEMQ_SERVER = "0d771434a61e40989564352d53beb88e.s2.eu.hivemq.cloud";
    private static final String HIVEMQ_USER = "denis";
    private static final String HIVEMQ_PASSWORD = "P@ssw0rd";
    private static final int HIVEMQ_PORT = 8883;

    private static Console console;
    private static Mqtt5AsyncClient client;

    public HiveMqManager(Console console) {
        this.console = console;

        client = MqttClient.builder()
                .useMqttVersion5()
                .identifier("Java_" + UUID.randomUUID())
                .serverHost(HIVEMQ_SERVER)
                .serverPort(HIVEMQ_PORT)
                .sslWithDefaultConfig()
                .buildAsync();

        client.connectWith()
                .simpleAuth()
                .username(HIVEMQ_USER)
                .password(HIVEMQ_PASSWORD.getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        console.println("Could not connect to HiveMQ: " + throwable.getMessage());
                    } else {
                        console.println("Connected to HiveMQ: " + connAck.getReasonCode());
                    }
                });
    }

    public void sendMessage(String topic, String message) {
        client.publishWith()
                .topic(topic)
                .payload(message.getBytes())
                .qos(MqttQos.EXACTLY_ONCE)
                .send()
                .whenComplete((mqtt5Publish, throwable) -> {
                    if (throwable != null) {
                        console.println("Error while sending message: " + throwable.getMessage());
                    } else {
                        console.println("Message sent to '" + topic + "': " + message);
                    }
                });
    }
}
