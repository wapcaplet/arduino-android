
#define soundPin 18
#define zeroDurationFrom 10000 
#define zeroDurationTo 19999
#define oneDurationFrom 20000
#define oneDurationTo 29999
#define headDurationFrom 30000
#define headDurationTo 99999
#define resetTimeout 3000

void setup()
{
  pinMode(soundPin, INPUT);
  Serial.begin(9600);
  Serial.println("Ready");
}

unsigned int result;
int bitNo = 0;
long lastPulseTime = 0;

void loop()
{
  //int analogValue = analogRead(soundPin);
  //Serial.print("Analog: "); Serial.println(analogValue);
  long pulseLength = pulseIn(soundPin, HIGH, oneDurationTo * 8);
  //long now = millis();
  long timeSinceLastPulse = millis() - lastPulseTime;
  lastPulseTime = millis();
  if (pulseLength == 0)
  {
    //Serial.print("timeSinceLastPulse = "); Serial.println(timeSinceLastPulse);
    bitNo = 0; result = 0;
  }
  else if (timeSinceLastPulse > resetTimeout) {
    //Serial.print(timeSinceLastPulse); Serial.print(" > "); Serial.println(resetTimeout);
    bitNo = 0; result = 0;
  }
  else 
  {
    if (bitNo == 0) { Serial.println("------------"); }

    if (pulseLength >= headDurationFrom && pulseLength <= headDurationTo)
    {
      //Serial.println("---- HEAD ----");
      bitNo = 0; result = 0;
    }
    else if (pulseLength >= zeroDurationFrom && pulseLength <= zeroDurationTo)
    {
      Serial.print("Pulse 0: "); Serial.println(pulseLength);
      result = result << 1;
      bitNo ++;
    }
    else if (pulseLength >= oneDurationFrom && pulseLength <= oneDurationTo)
    {
      Serial.print("Pulse 1: "); Serial.println(pulseLength);
      result = (result << 1) + 1;
      bitNo ++;
    }
    else
    {
       Serial.print("Error pulseLength="); Serial.println(pulseLength);
    }
    if (bitNo == 16)
    {
      Serial.print("Arduino recieved: ");
      Serial.println(result, 16);
      bitNo = 0; result = 0;
    }
  }
}



