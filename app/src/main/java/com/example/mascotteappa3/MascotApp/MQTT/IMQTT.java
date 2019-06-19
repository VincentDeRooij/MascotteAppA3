package com.example.mascotteappa3.MascotApp.MQTT;

//Interface for observer model
public interface IMQTT {

    void onMessageArrived(String message);

}
