package gatherers;

import java.util.List;

import datacontainers.DataContainer;

public class Sevcon extends Gatherer {
    private int[] data = new int[8]; // Memory efficient buffer

    private DataContainer sevcon;

    private final int message_100_index = 3; // 3 data
    private final int message_200_index = 0; // 3 data
    private final int message_300_index = 6; // 4 data
    private final int message_400_index = 10; // 4 data
    private final int message_500_index = 14; // 2 data
    private final int message_102_index = 16; // 3
    private final int message_202_index = 19; // 3
    private final int message_302_index = 22; // 4
    private final int message_402_index = 26; // 4
    private final int message_502_index = 30; // 2 data

    /**
     * Each channel has predefined AppComponents
     *
     * @param myComponentList List of AppComponent that this Channel update values to
     * @param myServices Services to inform to whenever an AppComponents get updated
     */
    public Sevcon(List<DataContainer> myComponentList) {
        for(DataContainer dc : myComponentList) {
            if(dc.getID() == "sevcon") this.sevcon = dc;
        }
    }

    // msb indexado desde 0 a 31
    public int twoComp(int val, int msb) {
        if(msb == 31) {
            return val;
        }
        else {
            if(((val >> msb) & 1) == 1) {
                return -(1<<(msb+1))+val;
            }
            else {
                return val;
            }
        }
    }

    /**
     * Commands executed once
     */
    @Override
    public void setUp() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        //processBuilder.redirectErrorStream(true);
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sudo /sbin/ip link set can0 up type can bitrate 1000000");
        processBuilder.command("bash", "-c", stringBuilder.toString());
        try {
            processBuilder.start();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parsing function. Transforms CANBUS message from console to double,
     * into AppComponent bms's double[] valoresRealesActuales, directly.
     * @param message
     */
    public void parseMessage(String message) {
        //String[] msg = Utils.split(message, " "); // Better performance split than String.split()
        System.out.println(message);
        String[] msg = message.split("\\s+"); // Better performance split than String.split()

        // if (msg.length != 16){ // If it isn't CAN-type message
        //     System.out.println("Message is not CAN-type. Split length is not 16.");
        //     System.out.println(message);
        //     return;
        // }

        // Parse HEX strings to byte data type, into local buffer
        int L = Character.getNumericValue(msg[3].charAt(1));
        for(int i=0 ; i<L; i++){ //asume mensaje can de 8 bytes fijo, todo: hacer mas flexible en el futuro.
            // atento a esto en la prueba, puede estar alrevez
            data[i] = Integer.parseInt(msg[4+i], 16);
        }

        switch (msg[2]){
            case "100":
                this.sevcon.valoresRealesActuales[message_100_index    ] = 0.0625 * twoComp((((data[0] << 8) | data[1]) & 0x00FFFF),15); // der_battery_V
                this.sevcon.valoresRealesActuales[message_100_index + 1] = -1 * 0.0625 * twoComp(((data[2] << 8) | data[3]),15) ; // der_battery_I SIGNED
                this.sevcon.valoresRealesActuales[message_100_index + 2] = twoComp((data[4] & 0x00FF), 7); // der_inverter_temp
                break;
            case "200":
                this.sevcon.valoresRealesActuales[message_200_index    ] = -1 * twoComp(((data[0] << 8) | data[1]),15) ; // der_motor_I SIGNED [A]
                this.sevcon.valoresRealesActuales[message_200_index + 1] = -1 * 0.1 * twoComp(((data[2] << 8) | data[3]),15) ; // der_motor_torque_demand SIGNED
                this.sevcon.valoresRealesActuales[message_200_index + 2] = -1 * twoComp(((data[7] << 24) | (data[6] << 16) | (data[5] << 8) | data[4]),31) ; // der_motor_RPM SIGNED [rad/s]
                System.out.println("RPM: "+this.sevcon.valoresRealesActuales[message_200_index+2]);
                break;
            case "300":
                this.sevcon.valoresRealesActuales[message_300_index    ] = -1 * 0.0625 * twoComp(((data[0] << 8) | data[1]), 15); // der_target_I_quadrature SIGNED
                this.sevcon.valoresRealesActuales[message_300_index + 1] = -1 * 0.0625 * twoComp(((data[2] << 8) | data[3]),15); // der_I_quadrature SIGNED
                this.sevcon.valoresRealesActuales[message_300_index + 2] = -1 * 0.1 * twoComp(((data[4] << 8) | data[5]),15); // der_torque_actual SIGNED
                this.sevcon.valoresRealesActuales[message_300_index + 3] = -1 * 0.0625 * twoComp(((data[6] << 8) | data[7]),15); // der_V_quadrature SIGNED
                break;
            case "400":
                this.sevcon.valoresRealesActuales[message_400_index    ] = 0.00390625 * twoComp((((data[0] << 8) | data[1]) & 0x00FF),15) ; // der_throttle_V [V]
                this.sevcon.valoresRealesActuales[message_400_index + 1] = -1 * 0.0625 * twoComp(((data[2] << 8) | data[3]),15); // der_target_I_direct SIGNED
                this.sevcon.valoresRealesActuales[message_400_index + 2] = -1 * 0.0625 * twoComp(((data[4] << 8) | data[5]),15); // der_I_direct SIGNED
                this.sevcon.valoresRealesActuales[message_400_index + 3] = -1 * 0.0625 * twoComp(((data[6] << 8) | data[7]),15); // der_V_direct SIGNED
                break;
            case "500":
                this.sevcon.valoresRealesActuales[message_500_index    ] = 0.1 * twoComp((((data[0] << 8) | data[1]) & 0x00FF),15) ; // der_target_torque_percentaje
                this.sevcon.valoresRealesActuales[message_500_index + 1] = 0.00390625 * twoComp((((data[2] << 8) | data[3]) & 0x00FF),15) ; // der_footbrake_V [V]
                break;
            case "102":
                this.sevcon.valoresRealesActuales[message_102_index    ] = 0.0625 * twoComp((((data[0] << 8) | data[1]) & 0x00FFFF),15); // izq_battery_V
                this.sevcon.valoresRealesActuales[message_102_index + 1] = -1 * 0.0625 * twoComp(((data[2] << 8) | data[3]),15) ; // izq_battery_I SIGNED
                this.sevcon.valoresRealesActuales[message_102_index + 2] = twoComp((data[4] & 0x00FF),5); // izq_inverter_temp
                break;
            case "202":
                this.sevcon.valoresRealesActuales[message_202_index    ] = -1 * twoComp(((data[0] << 8) | data[1]), 15) ; // izq_motor_I SIGNED
                this.sevcon.valoresRealesActuales[message_202_index + 1] = -1 * 0.1 * twoComp(((data[2] << 8) | data[3]), 15) ; // izq_motor_torque_demand SIGNED
                this.sevcon.valoresRealesActuales[message_202_index + 2] = -1 * twoComp(((data[4] << 24) | (data[5] << 16) | (data[6] << 8) | data[7]),31) ; // izq_motor_RPM SIGNED
                break;
            case "302":
                this.sevcon.valoresRealesActuales[message_302_index    ] = -1 * 0.0625 * twoComp(((data[0] << 8) | data[1]), 15) ; // izq_target_I_quadrature SIGNED
                this.sevcon.valoresRealesActuales[message_302_index + 1] = -1 * 0.0625 * twoComp(((data[2] << 8) | data[3]), 15) ; // izq_I_quadrature SIGNED
                this.sevcon.valoresRealesActuales[message_302_index + 2] = -1 * 0.1    * twoComp(((data[4] << 8) | data[5]), 15) ; // izq_torque_actual SIGNED
                this.sevcon.valoresRealesActuales[message_302_index + 3] = -1 * 0.0625 * twoComp(((data[6] << 8) | data[7]), 15) ; // izq_V_quadrature SIGNED
                break;
            case "402":
                this.sevcon.valoresRealesActuales[message_402_index    ] = 0.00390625 *  twoComp(((data[0] << 8) | (data[1] & 0x00FF)),15) ; // izq_throttle_V [V]
                this.sevcon.valoresRealesActuales[message_402_index + 1] = -1 * 0.0625 * twoComp(((data[2] << 8) | data[3]),15) ; // izq_target_I_direct SIGNED
                this.sevcon.valoresRealesActuales[message_402_index + 2] = -1 * 0.0625 * twoComp(((data[4] << 8) | data[5]),15) ; // izq_I_direct SIGNED
                this.sevcon.valoresRealesActuales[message_402_index + 3] = -1 * 0.0625 * twoComp(((data[6] << 8) | data[7]),15) ; // izq_V_direct SIGNED
                break;
            case "502":
                this.sevcon.valoresRealesActuales[message_502_index    ] = 0.1 * twoComp((((data[0] << 8) | data[1]) & 0x00FF),15); // izq_target_torque_percentaje
                this.sevcon.valoresRealesActuales[message_502_index + 1] = 0.00390625 * twoComp((((data[2] << 8) | data[3]) & 0x00FF),15) ; // izq_footbrake_V [V]
                break;
            default:
                System.out.print("ID: " + msg[2] + " MSG: ");
                for(int i=0 ; i< L; i++){
                    System.out.print(" " + msg[i+4]);
                }System.out.println("");

        } // switch
    } // parseMessage()
}
