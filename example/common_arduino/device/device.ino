#include <Javino.h>

#define RESOURCE1 12
#define RESOURCE2 13

Javino javino;
String msg;

void setup() {
  pinMode(RESOURCE1, OUTPUT);
  pinMode(RESOURCE2, OUTPUT);
  Serial.begin(9600);
}

void loop() {
  if (javino.availablemsg()) {
    msg = javino.getmsg();
    if (msg == "getPercepts") { // fffe0bgetPercepts
      String perceptions = getPercepts();
      javino.sendmsg(perceptions);
      delay(300);
    } else if (msg == "command1") { // fffe08command1
      digitalWrite(RESOURCE1, HIGH);
    } else if (msg == "command2") { // fffe08command2
      digitalWrite(RESOURCE1, LOW);
    } else if (msg == "command3") { // fffe08command3
      digitalWrite(RESOURCE2, HIGH);
    } else if (msg == "command4") { // fffe08command4
      digitalWrite(RESOURCE2, LOW);
    }
  }
}

String getPercepts() {
  return 
    "resource1(" + String(digitalRead(RESOURCE1)) + ");" +
    "resource2(" + String(digitalRead(RESOURCE2)) + ");";
}
