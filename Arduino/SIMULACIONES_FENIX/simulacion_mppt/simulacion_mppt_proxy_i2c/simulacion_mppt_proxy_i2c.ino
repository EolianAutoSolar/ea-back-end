// DEBUG
//#define serial_print // Usar para ver que los mppt se están leyendo bien
//#define debug_i2c
// CANBUS
#include <Serial_CAN_Module.h>
#include <SoftwareSerial.h>
Serial_CAN can;
#define can_tx  2           // tx of serial can module connect to D2
#define can_rx  3           // rx of serial can module connect to D3
unsigned long id = 0;
unsigned long id_aux = 0;// Just to fix a bug with print
unsigned char buff[7];

// Data
int switch_mppt = 0;
unsigned char MPPT1[7];
unsigned char MPPT2[7];
unsigned char MPPT3[7];
unsigned char MPPT4[7];
unsigned char MPPT5[7];

// I2C
#include <Wire.h>
byte I2C_RequestCommand = 0;

// When 0x08 is called, we store the request command
// Executed when a slave device receives a transmission from a master. (a.k.a Master wirtes on slave)
void processWriteEvent(int numBytes){
  
  while (Wire.available ()){
    byte I2C_command = Wire.read(); // receive byte as a character
    if (I2C_command > 0) { // Check that its a valid command
      I2C_RequestCommand = I2C_command; // Store globally
      #ifdef debug_i2c
        Serial.print("Write from master: ");Serial.println(I2C_RequestCommand, HEX);
      #endif
    }
  }
}

// Handler given the stored command
// Executed when a master requests data from this slave device. (a.k.a Master request from slave)
void processRequestEvent(void){
  #ifdef debug_i2c
    Serial.print("Request command: ");Serial.println(I2C_RequestCommand, HEX);
  #endif
  switch (I2C_RequestCommand){
    case 0x01:
      Wire.write( MPPT1, 7);
      #ifdef debug_i2c
        Serial.println("0x01 -> Sending MPPT1: ");for(int i = 0; i<7; i++){Serial.println(MPPT1[i]);};Serial.println("");
      #endif
      break;
    case 0x02:
      Wire.write( MPPT2, 7);
      #ifdef debug_i2c
        Serial.println("0x02 -> Sending MPPT2: ");for(int i = 0; i<7; i++){Serial.println(MPPT2[i]);};Serial.println("");
      #endif
      break;
    case 0x03:
      Wire.write( MPPT3, 7);
      #ifdef debug_i2c
        Serial.println("0x03 -> Sending MPPT3: ");for(int i = 0; i<7; i++){Serial.println(MPPT3[i]);};Serial.println("");
      #endif
      break;
    case 0x04:
      Wire.write( MPPT4, 7);
      #ifdef debug_i2c
        Serial.println("0x04 -> Sending MPPT4: ");for(int i = 0; i<7; i++){Serial.println(MPPT4[i]);};Serial.println("");
      #endif
      break;
    case 0x05:
      Wire.write( MPPT5, 7);
      #ifdef debug_i2c
        Serial.println("0x05 -> Sending MPPT5: ");for(int i = 0; i<7; i++){Serial.println(MPPT5[i]);};Serial.println("");
      #endif
      break;
    default:
      break;
  }
}


/*///////////////////// SET UP /////////////////////*/
void setup() {
  can.begin(can_tx, can_rx, 57600); // CANBUS baudrate is set to 125 KB
  Wire.begin(0x8); // Join I2C bus as slave with address 8
  Wire.setClock(400000); // Set Hz to max RP4 Hz allowed
  Wire.onReceive(processWriteEvent);  
  Wire.onRequest(processRequestEvent);
  
  #ifdef serial_print
    Serial.begin(9600);
    Serial.println("Comienza programa de lectura directa de MPPT.");
  #endif
  #ifdef debug_i2c
    Serial.begin(9600);
    Serial.println("Comienza programa de debug I2C.");
  #endif
}

/*///////////////////// LOOP /////////////////////*/
void loop() {
  delay(4); // Delay empírico. Si no está, algo le pasa al canbus y se traba, no actualiza ningún MPPT.
  #ifdef serial_print
    delay(1000);
    Serial.print("Sending Request to 0x71");Serial.println(switch_mppt+1);
  #endif
  
  switch (switch_mppt) {
    case 0:
      can.send(0x711, 0, 0, 0, 0);
      break;
    case 1:
      can.send(0x712, 0, 0, 0, 0);
      break;
    case 2:
      can.send(0x713, 0, 0, 0, 0);
      break;
    case 3:
      can.send(0x714, 0, 0, 0, 0);
      break;
    case 4:
      can.send(0x715, 0, 0, 0, 0);
      break;
    default:
      switch_mppt = 0;
      break;
  }
  switch_mppt = (switch_mppt + 1) % 5; 

  if(can.recv(&id, buff)){
    id_aux = id; // Just to fix a bug with print
    #ifdef serial_print
      Serial.print("ID________________________________________________");Serial.println(id_aux, HEX);
      Serial.print("BVLR (1: Uout = Umax, 0: Uout < Umax)_____________");Serial.println(bitRead(buff[0],7));
      Serial.print("OVT  (1: T>95°C, 0:T<95°C)________________________");Serial.println(bitRead(buff[0],6));
      Serial.print("NOC  (1: Batt. desconectada, 0: Batt. conectada)__");Serial.println(bitRead(buff[0],5));
      Serial.print("UNDV (1: Uin <= 26V, 0: Uin > 26V_________________");Serial.println(bitRead(buff[0],4));
      Serial.print("Uin  (Voltage IN)_________________________________");Serial.print((((bitRead(buff[0],1)<<1|bitRead(buff[0],0))<<8)|buff[1])*150.49/1000.0);Serial.println("\t[V]");
      Serial.print("Iin  (Current IN)_________________________________");Serial.print((((bitRead(buff[2],1)<<1|bitRead(buff[2],0))<<8)|buff[3])*8.72/1000.0);Serial.println("\t[A]");
      Serial.print("Potencia generada_________________________________");Serial.print(((((bitRead(buff[0],1)<<1|bitRead(buff[0],0))<<8)|buff[1])*150.49/1000.0)*((((bitRead(buff[2],1)<<1|bitRead(buff[2],0))<<8)|buff[3])*8.72/1000.0));Serial.println("\t[W]");
      Serial.print("Uout (Voltage OUT)________________________________");Serial.print((((bitRead(buff[4],1)<<1|bitRead(buff[4],0))<<8)|buff[5])*208.79/1000.0);Serial.println("[V]");
      Serial.print("Temperature_______________________________________");Serial.print(buff[6]);Serial.println("\t[°C]");
      Serial.println("");
    #endif

    if (id_aux == 0x771){
      for(int i=0;i<7;i++){MPPT1[i] = buff[i];};
      #ifdef serial_print
        Serial.println("MPPT1 updated");
      #endif
    }else if (id_aux == 0x772){
      for(int i=0;i<7;i++){MPPT2[i] = buff[i];};
      #ifdef serial_print
        Serial.println("MPPT2 updated");
      #endif
    } else if (id_aux == 0x773){
      for(int i=0;i<7;i++){MPPT3[i] = buff[i];};
      #ifdef serial_print
        Serial.println("MPPT3 updated");
      #endif
    } else if (id_aux == 0x774){
      for(int i=0;i<7;i++){MPPT4[i] = buff[i];};
      #ifdef serial_print
        Serial.println("MPPT4 updated");
      #endif
    }else if (id_aux == 0x775){
      for(int i=0;i<7;i++){MPPT5[i] = buff[i];};
      #ifdef serial_print
        Serial.println("MPPT5 updated");
      #endif
    }
    
  }// FIn Check Receive()

  #ifdef serial_print
    Serial.flush();
  #endif
  #ifdef debug_i2c
    Serial.flush();
  #endif
}
