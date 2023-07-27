package ApplicationLayer.Channel;

import ApplicationLayer.AppComponents.AppComponent;
import ApplicationLayer.LocalServices.Service;
import ApplicationLayer.Utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Channel encargado de leer los datos entregados por el CANBUS de los inversores Kelly.
 * Esta es una carácteristica opcional ofrecida al comprar los inversores, por lo que 
 * la documentación sobre los mensajes es bastante dificil de encontrar.
 * Está en el siguiente link <a target="_blank" 
 * href="https://drive.google.com/file/d/1Fepr-ZYgVn0TuLsoN_UW5ZaNASNzT34t/view">
 * https://drive.google.com/file/d/1Fepr-ZYgVn0TuLsoN_UW5ZaNASNzT34t/view</a>
 */

public class Canbus0 extends Channel {
    private int[] data = new int[8]; // Memory efficient buffer

    private AppComponent kellyIzq;
    private AppComponent kellyDer;
    private int msg;
    private ProcessBuilder processBuilder = new ProcessBuilder("candump", "can0,0cd:7FF,069:7FF", "-n 1");

    /**
     * Each channel has predefined AppComponents
     *
     * @param myComponentList List of AppComponent that this Channel update values to
     * @param myServices Services to inform to whenever an AppComponents get updated
     */
    public Canbus0(List<AppComponent> myComponentList, List<Service> myServices) {
        super(myComponentList, myServices);
        this.id = "Canbus0";
    
        for(AppComponent ac : myComponentList) {
            if(ac.getID().equals("kellyIzq")) {
                kellyIzq = ac;
            }
            else if(ac.getID().equals("kellyDer")) {
                kellyDer = ac;
            }
        }
    }

    @Override
    public void readingLoop() {}
    /**
     * Main reading and parsing loop
     */
    @Override
    public void singleRead() {
        // Init sphere.py
        try {
            //Ejecuta una sola lectura y espera hasta que esta llegue
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            //traduce el mensaje
            String line = reader.readLine();
            parseMessage(line, msg);
            //Luego del parseado cambia el puntero del mensaje
            msg++;
            msg = msg % 8;
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Commands executed once
     */
    @Override
    public void setUp() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        // NOTA: primero hay que iniciar el can com en comando 'stty -F /dev/serial0 raw 9600 cs8 clocal -cstopb'
        // (9600 es el baud rate)
        //stringBuilder.append("cd ./src/main/java/ApplicationLayer/SensorReading/CANReaders/linux-can-utils;");
        //stringBuilder.append("gcc candump.c lib.c -o candump;"); // Comment this on second execution, no need to recompile
        processBuilder.command("sudo", "/sbin/ip", "link" , "set", "can0", "up", "type", "can", "bitrate", "1000000");
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parsing function. Transforms CANBUS message from console to double,
     * into AppComponent bms's double[] valoresRealesActuales, directly.
     * @param message
     */
    public void parseMessage(String message, int msgNumber) {
        //String[] msg = Utils.split(message, " "); // Better performance split than String.split()
        String[] msg = message.split("\\s+"); // etter performance split than String.split()

        // Parse HEX strings to byte data type, into local buffer
        int L = Character.getNumericValue(msg[3].charAt(1));
        for(int i=0 ; i<L; i++){ //asume mensaje can de 8 bytes fijo, todo: hacer mas flexible en el futuro.
            // atento a esto en la prueba, puede estar alrevez
            data[i] = Integer.parseInt(msg[4+i], 16);
        }

        switch (msgNumber){
            case 0:
                // Kelly izq CCP_A2D_BATCH_READ1
                System.out.println("Kelly izq CCP_A2D_BATCH_READ1");
                kellyIzq.valoresRealesActuales[0] = data[0]; // Brake A/D
                kellyIzq.valoresRealesActuales[1] = data[1]; // TPS A/D
                kellyIzq.valoresRealesActuales[2] = data[2]; // Operation voltage A/D
                kellyIzq.valoresRealesActuales[3] = data[3]; // Vs A/D
                kellyIzq.valoresRealesActuales[4] = data[4]; // B+ A/D
                break;
            case 1:
                // Kelly izq CCP_A2D_BATCH_READ2
                System.out.println("Kelly izq CCP_A2D_BATCH_READ2");
                kellyIzq.valoresRealesActuales[5] = data[0];  // Ia A/D
                kellyIzq.valoresRealesActuales[6] = data[1];  // Ib A/D
                kellyIzq.valoresRealesActuales[7] = data[2];  // Ic A/D
                kellyIzq.valoresRealesActuales[8] = data[3];  // Va A/D
                kellyIzq.valoresRealesActuales[9] = data[4];  // Vb A/D
                kellyIzq.valoresRealesActuales[10] = data[5]; // Vc A/D
                break;
            case 2:
                // Kelly izq CCP_MONITOR1
                System.out.println("Kelly izq CCP_MONITOR1");
                kellyIzq.valoresRealesActuales[11] = data[0]; // PWM
                kellyIzq.valoresRealesActuales[12] = data[1]; // enable motor rotation
                kellyIzq.valoresRealesActuales[13] = data[2]; // motor temperature
                kellyIzq.valoresRealesActuales[14] = data[3]; // Controller's temperature
                kellyIzq.valoresRealesActuales[15] = data[4]; // temperature of high side FETMOS heat sink
                kellyIzq.valoresRealesActuales[16] = data[5]; // temperature of low side FETMOS heat sink
                break;
            case 3:
                // Kelly izq CCP_MONITOR2
                System.out.println("Kelly izq CCP_MONITOR2");
                // RPM: data[0] -> MSB of mechanical speed in RPM,      
                //      data[1] -> LSB of mechanical speed in RPM
                kellyIzq.valoresRealesActuales[17] = (data[0] << 8) | data[1];
                kellyIzq.valoresRealesActuales[18] = data[2]; // present current accounts for percent of the rated current of controller
                // Error code: data[3] -> MSB of error code,      
                //             data[4] -> LSB of error code
                kellyIzq.valoresRealesActuales[19] = (data[3] << 8) | data[4];
                break;
            case 4:
                // Kelly der CCP_A2D_BATCH_READ1
                System.out.println("Kelly der CCP_A2D_BATCH_READ1");
                kellyDer.valoresRealesActuales[0] = data[0]; // Brake A/D
                kellyDer.valoresRealesActuales[1] = data[1]; // TPS A/D
                kellyDer.valoresRealesActuales[2] = data[2]; // Operation voltage A/D
                kellyDer.valoresRealesActuales[3] = data[3]; // Vs A/D
                kellyDer.valoresRealesActuales[4] = data[4]; // B+ A/D
                break;
            case 5:
                // Kelly der CCP_A2D_BATCH_READ2
                System.out.println("Kelly der CCP_A2D_BATCH_READ2");
                kellyDer.valoresRealesActuales[5] = data[0];  // Ia A/D
                kellyDer.valoresRealesActuales[6] = data[1];  // Ib A/D
                kellyDer.valoresRealesActuales[7] = data[2];  // Ic A/D
                kellyDer.valoresRealesActuales[8] = data[3];  // Va A/D
                kellyDer.valoresRealesActuales[9] = data[4];  // Vb A/D
                kellyDer.valoresRealesActuales[10] = data[5]; // Vc A/D
                break;
            case 6:
                // Kelly der CCP_MONITOR1
                System.out.println("Kelly der CCP_MONITOR1");
                kellyDer.valoresRealesActuales[11] = data[0]; // PWM
                kellyDer.valoresRealesActuales[12] = data[1]; // enable motor rotation
                kellyDer.valoresRealesActuales[13] = data[2]; // motor temperature
                kellyDer.valoresRealesActuales[14] = data[3]; // Controller's temperature
                kellyDer.valoresRealesActuales[15] = data[4]; // temperature of high side FETMOS heat sink
                kellyDer.valoresRealesActuales[16] = data[5]; // temperature of low side FETMOS heat sink
                break;
            case 7:
                // Kelly der CCP_MONITOR2
                System.out.println("Kelly der CCP_MONITOR2");
                // RPM: data[0] -> MSB of mechanical speed in RPM,      
                //      data[1] -> LSB of mechanical speed in RPM
                kellyDer.valoresRealesActuales[17] = (data[0] << 8) | data[1];
                kellyDer.valoresRealesActuales[18] = data[2]; // present current accounts for percent of the rated current of controller
                // Error code: data[3] -> MSB of error code,      
                //             data[4] -> LSB of error code
                kellyDer.valoresRealesActuales[19] = (data[3] << 8) | data[4];
                break;
            default:
                System.out.println("Trama "+msg[0]+" no procesada");
        } // switch
    } // parseMessage()
}
