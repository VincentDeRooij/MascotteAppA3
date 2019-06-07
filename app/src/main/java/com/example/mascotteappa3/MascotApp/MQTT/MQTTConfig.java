package com.example.mascotteappa3.MascotApp.MQTT;

import java.util.UUID;

public class MQTTConfig {

    // Onze broker: test.mosquitto.org is een broker voor exprimenten, niet voor
    // productie of hoge volume
    //private String MQTT_BROKER_URL = "tcp://test.mosquitto.org:1883";
    private String MQTT_BROKER_URL = "tcp://51.254.217.43:1883";


    // De topic welke in de app gebruikt wordt, aanpassen naar wens maar wel uniek
    private String MQTT_TOPIC = "TI-14-2019/A3/GPSCoordinaten";

    // Elke client connect met een unieke client_id. Aanpassen voor elk device! (auto
    // generate in constructor?
    private String CLIENT_ID;

    // private constructor
    private MQTTConfig() {
        CLIENT_ID = "MIJN_UNIEKE_ID_" + UUID.randomUUID().toString();
    }

    private static MQTTConfig instance = null;

    // Singleton
    public static MQTTConfig getInstance() {
        if( instance == null ) {
            instance = new MQTTConfig();
        }
        return instance;
    }

    public String MQTT_BROKER_URL() {
        return MQTT_BROKER_URL;
    }

    public String PUBLISH_TOPIC() {
        return MQTT_TOPIC;
    }

    public String CLIENT_ID() {
        return CLIENT_ID;
    }
}