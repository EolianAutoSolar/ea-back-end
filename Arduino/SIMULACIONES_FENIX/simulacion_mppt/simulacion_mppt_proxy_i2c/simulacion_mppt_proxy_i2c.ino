#include <Serial_CAN_Module.h>
#include <SoftwareSerial.h>
Serial_CAN can;
#define can_tx  2           // tx of serial can module connect to D2
#define can_rx  3           // rx of serial can module connect to D3
unsigned long id = 0;
unsigned long id_aux = 0;// Just to fix a bug with print
unsigned char buff[7];

int switch_mppt = 0;
byte MPPT1[7];
byte MPPT2[7];
byte MPPT3[7];
byte MPPT4[7];
byte MPPT5[7];

#define serial_print // Usar para ver que los mppt se están leyendo bien

/*///////////////////// SET UP /////////////////////*/
void setup() {
  can.begin(can_tx, can_rx, 57600); // CANBUS baudrate is set to 125 KB
  
  #ifdef serial_print
    Serial.begin(9600);
    Serial.println("Comienza programa de lectura directa de MPPT.");
  #endif
}

/*///////////////////// LOOP /////////////////////*/
void loop() {

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

    if (id == 0x771){
      for(int i=0;i<7;i++){buff[i] = MPPT1[i];};
    }else if (id == 0x772){
      for(int i=0;i<7;i++){buff[i] = MPPT2[i];};
    } else if (id == 0x773){
      for(int i=0;i<7;i++){buff[i] = MPPT3[i];};
    } else if (id == 0x774){
      for(int i=0;i<7;i++){buff[i] = MPPT4[i];};
    }else if (id == 0x775){
      for(int i=0;i<7;i++){buff[i] = MPPT5[i];};
    }
    
  }// FIn Check Receive()

  #ifdef serial_print
    Serial.flush();
  #endif
}
