float Ki, Kd, Kp;
int setX, setY;


void setup() {
  Serial.begin(9600);
}

void loop() {
}


void serialEvent() {
  while (Serial.available()) {
    char in = (char)Serial.read();
    if (in == 'w') {
      Serial.print('w');
      Serial.print(",");
      Serial.print(Ki);
      Serial.print(",");
      Serial.print(Kd);
      Serial.print(",");
      Serial.print(Kp);
      Serial.print(",");
      Serial.print(setX);
      Serial.print(",");
      Serial.print(setY);
      Serial.print('\n');
      
    } else if ( in == 'r') {
      int address=0;
      Ki = Serial.parseFloat();
      Kd = Serial.parseFloat();
      Kp = Serial.parseFloat();
      setX = Serial.parseInt();
      setY = Serial.parseInt();
      Serial.print('a');
      Serial.print(",");
      Serial.print(Ki);
      Serial.print(",");
      Serial.print(Kd);
      Serial.print(",");
      Serial.print(Kp);
      Serial.print(",");
      Serial.print(setX);
      Serial.print(",");
      Serial.print(setY);
      Serial.print('\n');
    }
  }
}

