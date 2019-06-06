/*-------------------------------------------------------------------------

Android to mqtt example for ESP32 based hardware. TI-1.4 Avans Breda

copyright may, 2019 dm.kroeske@avans.nl

Permission is hereby granted, free of charge, to any person obtaining a copy 
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights 
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is furnished
to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in 
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

-------------------------------------------------------------------------*/
#include "esp_wpa2.h" //wpa2 library for connections to Enterprise networks
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include "WiFi.h"
//testcode
const char *ssid = "eduroam";
const char* username = "jamvries@avans.nl";
const char *password = "Veranderdit123..";

const char* mqtt_broker   = "51.254.217.43";
const char* mqtt_topic    = "TI-14-2019/A3/MascotteKnoppen";
const char* mqtt_username = "emon";
const char* mqtt_password = "uw2ELjAKrEUwqgLT";

const int buttonpin1 = 27;
const int buttonpin2 = 33;

int buttonState1 =0;
int buttonState2 = 0;
boolean button1 = true; 
boolean button2 = true; 

WiFiClient wifiClient;
PubSubClient mqttClient("", 0, wifiClient);

/******************************************************************/
void setup(){
   Serial.begin(115200);
  pinMode(buttonpin1, INPUT);
  pinMode(buttonpin2, INPUT);

/* 
short:      ESP8266 (Arduino) setup
inputs:        
outputs: 
notes:         
Version :   DMK, Initial code
*******************************************************************/



  Serial.begin(115200);
  WiFi.disconnect(true);  //disconnect form wifi to set new wifi connection
  WiFi.mode(WIFI_STA); //init wifi mode
  esp_wifi_sta_wpa2_ent_set_identity((uint8_t *)username, strlen(username)); //provide identity
  esp_wifi_sta_wpa2_ent_set_username((uint8_t *)username, strlen(username)); //provide username --> identity and username is same
  esp_wifi_sta_wpa2_ent_set_password((uint8_t *)password, strlen(password)); //provide password
  esp_wpa2_config_t config = WPA2_CONFIG_INIT_DEFAULT(); //set config settings to default
  esp_wifi_sta_wpa2_ent_enable(&config); //set config settings to enable function
  WiFi.begin(ssid); //connect to wifi
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Connection established");
  Serial.println(WiFi.localIP());
}

/******************************************************************/
void loop(){
  buttonState1 = digitalRead(buttonpin1); 
  buttonState2 = digitalRead(buttonpin2); 

    if( !mqttClient.connected() ) {
      mqtt_connect();
    } else {
      mqttClient.loop();
    }

  if( !mqttClient.connected() ) {
      mqtt_connect();
    } else {
      mqttClient.loop();
    }

  if(buttonState1 == HIGH && button1){
    Serial.println("1"); 
    //buttonState1 = LOW;
    button1 = false;
       
    mqtt_publish("1");
  }else if(buttonState1 == LOW){
    //Serial.println("1w");
    button1 = true;
  }

  if(buttonState2 == HIGH && button2){
    Serial.println("2"); 
    //buttonState2 = LOW;
    button2 = false;
    mqtt_publish("2"); 
  }else if (buttonState2 == LOW){
    //Serial.println("2w"); 
    button2 = true;
  }


  
    // Publish payload is BTN is pressed

       
    

/* 
short:    ESP8266 (Arduino) main loop
inputs:   
outputs: 
notes:    
Version:  DMK, Initial code
*******************************************************************/

  if (WiFi.status() == WL_CONNECTED) {
    
    if( !mqttClient.connected() ) {
      mqtt_connect();
    } else {
      mqttClient.loop();
    }
  
    // Publish payload is BTN is pressed
   
    
      delay(200);
    
    
  } else { 
    Serial.println("Geen WiFi verbinding !");
    delay(1000);
  }
}

/******************************************************************/
void mqtt_connect() 
/* 
short:      Connect to MQTT server UNSECURE
inputs:        
outputs: 
notes:         
Version :   DMK, Initial code
*******************************************************************/
{  
  mqttClient.setClient(wifiClient);
  mqttClient.setServer(mqtt_broker, 1883);

  // Connect with unique id
  String clientId = "TI14-";
  clientId += String(random(0xffff), HEX);
  clientId += '-';
  clientId += String((uint32_t)ESP.getEfuseMac(), HEX);
  
  if(mqttClient.connect( clientId.c_str(), mqtt_username, mqtt_password )){

    // Subscribe to topic
    mqttClient.subscribe(mqtt_topic);

    // Setup callback
    mqttClient.setCallback(mqtt_callback);
    Serial.printf("%s: Connected to %s:%d\n", __FUNCTION__, mqtt_broker, 1883);
  } else {    
    Serial.printf("%s: Connection ERROR (%s:%d)\n", __FUNCTION__, mqtt_broker, 1883);
    delay(2000);
  }
}

/******************************************************************/
void mqtt_publish(String id)
/* 
short:      Pulish on MQTT topic (UNSECURE)
inputs:     mascotte id, latitude from gps, longitude from gps
outputs: 
notes:         
Version :   1.0, Initial code
*******************************************************************/
{
  DynamicJsonDocument jsonDocument(1024);

  JsonObject info = jsonDocument.createNestedObject("Mascotte");
  info["id"] = id;
  

  char json[1024];
  serializeJson(jsonDocument, json);
  Serial.printf("%s\n", json);
  mqttClient.publish(mqtt_topic, json);
}

void mqtt_callback(char* topic, byte* payload, unsigned int length)
/* 
short:    MQTT callback. Elke publish op subscibed topic wordt hier
          afgehandeld
inputs:   'topic' waarop gepublished is
          'payload' bevat de published datablock
          'length' is de lengte van het payload array
outputs: 
notes:    In deze callback wordt gebruikt gemaakt van JSON parser
Version:  DMK, Initial code
*******************************************************************/
{
  if( 0 == strcmp(topic, mqtt_topic) ) {
    // Parse payload
    DynamicJsonDocument jsonDocument(1024);
    DeserializationError error = deserializeJson(jsonDocument, payload);
    if( !error ) {
      JsonVariant msg = jsonDocument["Coordinaat"];
      if(!msg.isNull()) {
        // Flits de blauwe led
        delay(50);
      }
    }
  }
}
