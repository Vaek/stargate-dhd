/*
   Copyright (c) 2015, Majenko Technologies
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification,
   are permitted provided that the following conditions are met:

 * * Redistributions of source code must retain the above copyright notice, this
     list of conditions and the following disclaimer.

 * * Redistributions in binary form must reproduce the above copyright notice, this
     list of conditions and the following disclaimer in the documentation and/or
     other materials provided with the distribution.

 * * Neither the name of Majenko Technologies nor the names of its
     contributors may be used to endorse or promote products derived from
     this software without specific prior written permission.

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
   ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
   ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/* Create a WiFi access point and provide a web server on it. */

#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>

#include <uri/UriBraces.h>
#include <uri/UriRegex.h>

#include <FastLED.h>
#define NUM_LEDS 90
#define DATA_PIN D1
#define LED_TYPE    WS2812
#define COLOR_ORDER GRB
CRGB leds[NUM_LEDS];

#ifndef APSSID
#define APSSID "ESPap"
#define APPSK  "thereisnospoon"
#endif

/* Set these to your desired credentials. */
const char *ssid = APSSID;
const char *password = APPSK;

const String onState = "1";
const String offState = "0";
String settedPattern = "00000000";
int waveIndex = 0;

ESP8266WebServer server(80);

/* Just a little test message.  Go to http://192.168.4.1 in a web browser
   connected to this access point to see it.
*/
void light(String pattern) {
  settedPattern = pattern;
  Serial.println("Lighting: '" + pattern + "'");
}

void setup() {
  // limit my draw to 1A at 5v of power draw
  FastLED.setMaxPowerInVoltsAndMilliamps(5,1000);
  FastLED.addLeds<LED_TYPE, DATA_PIN, COLOR_ORDER>(leds, NUM_LEDS);
  
  delay(1000);
  Serial.begin(115200);
  Serial.println();
  Serial.print("Configuring access point...");
  /* You can remove the password parameter if you want the AP to be open. */
  WiFi.softAP(ssid, password);

  IPAddress myIP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(myIP);

  server.on(F("/"), []() {
    server.send(200, "text/plain", "hello from esp8266!");
  });

  server.on(UriBraces("/light/{}"), []() {
    String pattern = server.pathArg(0);
    light(pattern);
    server.send(200, "text/plain", "Lighting: '" + pattern + "'");
  });
  
  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
  server.handleClient();

  if (settedPattern.substring(0, 1) == onState) {
    leds[0] = CRGB::White;
  } else {
    leds[0] = CRGB::Black;
  }
  
  if (settedPattern.substring(1, 2) == onState) {
    leds[1] = CRGB::White;
  } else {
    leds[1] = CRGB::Black;
  }
  
  if (settedPattern.substring(2, 3) == onState) {
    leds[2] = CRGB::White;
  } else {
    leds[2] = CRGB::Black;
  }
  
  if (settedPattern.substring(3, 4) == onState) {
    leds[3] = CRGB::White;
  } else {
    leds[3] = CRGB::Black;
  }
  
  if (settedPattern.substring(4, 5) == onState) {
    leds[4] = CRGB::White;
  } else {
    leds[4] = CRGB::Black;
  }
  
  if (settedPattern.substring(5, 6) == onState) {
    leds[5] = CRGB::White;
  } else {
    leds[5] = CRGB::Black;
  }
  
  if (settedPattern.substring(6, 7) == onState) {
    leds[6] = CRGB::White;
  } else {
    leds[6] = CRGB::Black;
  }
  
  if (settedPattern.substring(7, 8) == onState) {
    for (int index = 7; index < NUM_LEDS; index++) {
      if (index == waveIndex) {
        leds[index] = CRGB::Blue;
      } else {
        leds[index] = CRGB::Black;
      }
    }
    waveIndex = (waveIndex + 1) % NUM_LEDS;
  } else {
    for (int index = 7; index < NUM_LEDS; index++) {
      leds[index] = CRGB::Black;
    }
  }

  FastLED.show();
  delay(50);
}
