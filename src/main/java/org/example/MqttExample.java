package org.example;

import org.eclipse.paho.client.mqttv3.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MqttExample {

    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883";
    private static final String TOPIC = "/cv17";
    private static final String CLIENT_ID = "java-client-" + System.nanoTime();

    public static void main(String[] args) {
        try {
            MqttClient client = new MqttClient(BROKER_URL, CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            client.connect(options);
            System.out.println("Conectado ao broker MQTT.");

            publishMessage(client);

            subscribeToTopic(client);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void publishMessage(MqttClient client) throws MqttException {
        MqttMessage message = new MqttMessage("Se busca rival en sulamerica".getBytes());
        message.setQos(0);
        message.setRetained(true);
        client.publish(TOPIC, message);
        System.out.println("Mensagem publicada no tópico: " + TOPIC);
    }

    public static void subscribeToTopic(MqttClient client) throws MqttException {
        CountDownLatch latch = new CountDownLatch(1);

        client.subscribe(TOPIC, (topic, msg) -> {
            System.out.println("Mensagem recebida no tópico: " + topic);
            System.out.println("Conteúdo da mensagem: " + new String(msg.getPayload()));
            latch.countDown();
        });

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

