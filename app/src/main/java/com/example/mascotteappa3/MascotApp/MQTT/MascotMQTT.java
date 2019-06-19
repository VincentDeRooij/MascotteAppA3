package com.example.mascotteappa3.MascotApp.MQTT;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.mascotteappa3.MascotApp.MapView.MapActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MascotMQTT {
    private final String MQTTHOST = "tcp://51.254.217.43:1883";
    private final String USERNAME = "emon";
    private final String PASSWORD = "uw2ELjAKrEUwqgLT";
    private final String TOPIC = "TI-14-2019/A3/GPSCoordinaten";

    private boolean isConnected = false;
    private AppCompatActivity activity;

    private MqttConnectOptions options;
    private MqttAndroidClient client;
    private IMQTT listener;

    public MascotMQTT(AppCompatActivity activity, Context context, String clientId, IMQTT listener) {
        this.activity = activity;
        client = new MqttAndroidClient(context, MQTTHOST, clientId);

        options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        this.listener = listener;
    }

    public void connect() {
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(activity, "Connected", Toast.LENGTH_LONG).show();
                    setSubscription();
                    isConnected = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(activity, "Connection Failed", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                isConnected = false;
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String text = new String(message.getPayload());
                listener.onMessageArrived(text);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void setSubscription() {
        try {
            client.subscribe(TOPIC, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
