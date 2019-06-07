package com.example.mascotteappa3.MascotApp.MQTT;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.mascotteappa3.MascotApp.MapView.MapActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttMessageService extends IntentService {

    private static final String TAG = "TI14-MQTT";

    private PahoMqttClient pahoMqttClient;
    private MqttAndroidClient mqttAndroidClient;

    public MqttMessageService() {
        super(MqttMessageService.class.getSimpleName());
        Log.d(TAG, "MqttMessageService()");
        //this.listener = listener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        this.pahoMqttClient = new PahoMqttClient();
        this.mqttAndroidClient = pahoMqttClient.getMqttClient(
                getApplicationContext(),
                MQTTConfig.getInstance().MQTT_BROKER_URL(),
                MQTTConfig.getInstance().CLIENT_ID());

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.i(TAG, "connectComplete()");
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.i(TAG, "connectionLost()");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {
                String payload = new String( mqttMessage.getPayload() );
                //Log.i(TAG, "messageArrived() with payload : " + payload );
                try
                {
                    Intent broadCastIntent = new Intent();
                    broadCastIntent.setAction(MapActivity.BROADCAST_ACTION);
                    broadCastIntent.putExtra("payload", payload);
                    sendBroadcast(broadCastIntent);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.i(TAG, "deliveryComplete()");
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

}

