// Test code for Ultimate GPS Using Hardware Serial (e.g. GPS Flora or FeatherWing)
//
// This code shows how to listen to the GPS module via polling. Best used with
// Feathers or Flora where you have hardware Serial and no interrupt
//
// Tested and works great with the Adafruit GPS FeatherWing
// ------> https://www.adafruit.com/products/3133
// or Flora GPS
// ------> https://www.adafruit.com/products/1059
// but also works with the shield, breakout
// ------> https://www.adafruit.com/products/1272
// ------> https://www.adafruit.com/products/746
//
// Pick one up today at the Adafruit electronics shop
// and help support open source hardware & software! -ada

#include <Adafruit_GPS.h>

// what's the name of the hardware serial port?
#define GPSSerial Serial1

// Connect to the GPS on the hardware port
Adafruit_GPS GPS(&GPSSerial);

// Set GPSECHO to 'false' to turn off echoing the GPS data to the Serial console
// Set to 'true' if you want to debug and listen to the raw GPS sentences
#define GPSECHO false
#include "esp_wpa2.h" //wpa2 library for connections to Enterprise networks
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include "WiFi.h"
//testcode
const char *ssid = "eduroam";
const char* username = "cpsteven@avans.nl";
const char *password = "AvengersAssemble-1";

const char* mqtt_broker   = "51.254.217.43";
const char* mqtt_topic    = "TI-14-2019/A3/GPSCoordinaten";
const char* mqtt_username = "emon";
const char* mqtt_password = "uw2ELjAKrEUwqgLT";

WiFiClient wifiClient;
PubSubClient mqttClient("", 0, wifiClient);

uint32_t timer = millis();


void setup()
{
  //while (!Serial);  // uncomment to have the sketch wait until Serial is ready

  // connect at 115200 so we can read the GPS fast enough and echo without dropping chars
  // also spit it out
  Serial.begin(115200);
  Serial.println("Adafruit GPS library basic test!");

  // 9600 NMEA is the default baud rate for Adafruit MTK GPS's- some use 4800
  GPS.begin(9600);
  // uncomment this line to turn on RMC (recommended minimum) and GGA (fix data) including altitude
  GPS.sendCommand(PMTK_SET_NMEA_OUTPUT_RMCGGA);
  // uncomment this line to turn on only the "minimum recommended" data
  //GPS.sendCommand(PMTK_SET_NMEA_OUTPUT_RMCONLY);
  // For parsing data, we don't suggest using anything but either RMC only or RMC+GGA since
  // the parser doesn't care about other sentences at this time
  // Set the update rate
  GPS.sendCommand(PMTK_SET_NMEA_UPDATE_1HZ); // 1 Hz update rate
  // For the parsing code to work nicely and have time to sort thru the data, and
  // print it out we don't suggest using anything higher than 1 Hz

  // Request updates on antenna status, comment out to keep quiet
  GPS.sendCommand(PGCMD_ANTENNA);

  delay(1000);

  // Ask for firmware version
  GPSSerial.println(PMTK_Q_RELEASE);

  Serial.println(" Init wifi connection" );
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


  void types(String a){Serial.println("it's a String");}
  void types(int a)   {Serial.println("it's an int");}
  void types(char* a) {Serial.println("it's a char*");}
  void types(float a) {Serial.println("it's a float");} 
  
void loop() // run over and over again
{
  // read data from the GPS in the 'main loop'
  char c = GPS.read();
  // if you want to debug, this is a good time to do it!
  if (GPSECHO)
    if (c) Serial.print(c);
  // if a sentence is received, we can check the checksum, parse it...
  if (GPS.newNMEAreceived()) {
    // a tricky thing here is if we print the NMEA sentence, or data
    // we end up not listening and catching other sentences!
    // so be very wary if using OUTPUT_ALLDATA and trytng to print out data
    if (!GPS.parse(GPS.lastNMEA())) // this also sets the newNMEAreceived() flag to false
      return; // we can fail to parse a sentence in which case we should just wait for another
  }

  // if millis() or timer wraps around, we'll just reset it
  if (timer > millis()) timer = millis();

  // approximately every 2 seconds or so, print out the current stats
  if (millis() - timer > 10000) {
    timer = millis(); // reset the timer
    Serial.print("\nTime: ");
    Serial.print(GPS.hour, DEC); Serial.print(':');
    Serial.print(GPS.minute, DEC); Serial.print(':');
    Serial.print(GPS.seconds, DEC); Serial.print('.');
    Serial.println(GPS.milliseconds);
    Serial.print("Date: ");
    Serial.print(GPS.day, DEC); Serial.print('/');
    Serial.print(GPS.month, DEC); Serial.print("/20");
    Serial.println(GPS.year, DEC);
    Serial.print("Fix: "); Serial.print((int)GPS.fix);
    Serial.print(" quality: "); Serial.println((int)GPS.fixquality);
    if (GPS.fix) {
      Serial.print("Location: ");
      Serial.print(GPS.latitude, 4); Serial.print(GPS.lat);
      Serial.print(", ");
      Serial.print(GPS.longitude, 4); Serial.println(GPS.lon);
      Serial.print("Satellites: "); Serial.println((int)GPS.satellites);
        if (WiFi.status() == WL_CONNECTED) {
    
    if( !mqttClient.connected() ) {
      mqtt_connect();
    } else {
      mqttClient.loop();
    }
  
    // Publish payload is BTN is pressed
   
      mqtt_publish(
        "2", // id String
       GPS.latitude, // latitude value
        GPS.longitude  // longitude value
      );
        
  } else { 
    Serial.println("Geen WiFi verbinding !");
    delay(1000);
  }
    }

    
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
void mqtt_publish(String id, double latitude, double longitude)
/* 
short:      Pulish on MQTT topic (UNSECURE)
inputs:     mascotte id, latitude from gps, longitude from gps
outputs: 
notes:         
Version :   1.0, Initial code
*******************************************************************/
{
  DynamicJsonDocument jsonDocument(1024);

  JsonObject info = jsonDocument.createNestedObject("Coordinaat");
  info["id"] = id;
  info["latitude"] = latitude;
  info["longitude"] = longitude;

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
