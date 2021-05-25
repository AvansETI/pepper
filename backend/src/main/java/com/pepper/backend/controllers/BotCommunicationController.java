package com.pepper.backend.controllers;

import com.pepper.backend.services.messaging.bot.BotMessageHandlerService;
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

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    private IMqttClient client;
    private final BotMessageHandlerService messageHandler;

    public BotCommunicationController(@Lazy BotMessageHandlerService messageHandler) {
        this.messageHandler = messageHandler;
    }

    @PostConstruct
    public void connect() {
        try {
            this.client = new MqttClient(this.host, "client-1");
            this.client.setCallback(this);
            this.client.connect(this.getOptions());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public boolean publish(String message) {
        if (!this.client.isConnected()) {
            return false;
        }

        try {
            this.client.publish(this.topic, this.createMqttMessage(message));
        } catch (MqttException e) {
            return false;
        }

        return true;
    }

    private MqttConnectOptions getOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(this.username);
        options.setPassword(this.password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        return options;
    }

    private MqttMessage createMqttMessage(String message) {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(0);
        mqttMessage.setRetained(false);

        return mqttMessage;
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
    public void messageArrived(String s, MqttMessage mqttMessage) {
        this.messageHandler.handle(mqttMessage.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
