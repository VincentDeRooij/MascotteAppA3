#include "WiFi.h"
#include "esp_wpa2.h" //wpa2 library for connections to Enterprise networks
#include "PubSubClient.h"

//Enter user name and password here
const char *ssid = "eduroam";
const char* username = "@avans.nl";
const char *password = "";


void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  WiFi.disconnect(true);  //disconnect form wifi to set new wifi connection
  WiFi.mode(WIFI_STA); //init wifi mode
  esp_wifi_sta_wpa2_ent_set_identity((uint8_t *)username, strlen(username)); //provide identity
  esp_wifi_sta_wpa2_ent_set_username((uint8_t *)username, strlen(username)); //provide username --> identity and username is same
  esp_wifi_sta_wpa2_ent_set_password((uint8_t *)password, strlen(password)); //provide password
  esp_wpa2_config_t config = WPA2_CONFIG_INIT_DEFAULT(); //set config settings to default
  esp_wifi_sta_wpa2_ent_enable(&config); //set config settings to enable function
  WiFi.begin(ssid); //connect to wifi
  
}

void loop() {
  // put your main code here, to run repeatedly:
while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Connection established");
}
