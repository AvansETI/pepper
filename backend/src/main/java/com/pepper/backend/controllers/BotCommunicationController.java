package com.pepper.backend.controllers;

import com.pepper.backend.services.BotMessageHandlerService;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Controller
public class BotCommunicationController implements MqttCallbackExtended {

    @Value("${mqtt.host}")
    private String host;

    @Value("${mqtt.topic}")
    private String topic;

    private IMqttClient client;
    private final BotMessageHandlerService messageHandler;

    public BotCommunicationController(@Lazy BotMessageHandlerService messageHandler) {
        this.messageHandler = messageHandler;
    }

    @PostConstruct
    public void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        try {
            this.client = new MqttClient(this.host, "client-1");
            this.client.setCallback(this);
            this.client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String message) {
        if (!this.client.isConnected()) {
            return;
        }

        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(0);
        mqttMessage.setRetained(false);

        try {
            this.client.publish(this.topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectComplete(boolean b, String s) {
        try {
            this.client.subscribe(this.topic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        try {
            this.client.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        this.messageHandler.handle(mqttMessage.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
