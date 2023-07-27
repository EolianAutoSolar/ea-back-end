package gatherers;

import utils.Utils;
import java.io.IOException;
import java.util.List;
import datacontainers.DataContainer;

public class GPS extends Gatherer {
    private double[] data = new double[6];

    public DataContainer gps;
    
    /**
     * Each channel has predefined AppComponents
     *
     * @param myComponentList List of AppComponent that this Channel update values to
     * @param myServices Services to inform to whenever an AppComponents get updated
     */
    public GPS(List<DataContainer> myComponentList) {
        for(DataContainer dc : myComponentList) {
            if(dc.getID() == "gps") this.gps = dc;
        }
    }

    @Override
    public void setUp() {
        // NOTA: primero hay que iniciar el serial com en comando 'stty -F /dev/serial0 raw 9600 cs8 clocal -cstopb'
        // (9600 es el baud rate)
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "stty -F /dev/serial0 raw 9600 cs8 clocal -cstopb");
        try {
            processBuilder.start();
        }catch (IOException  e) {
            e.printStackTrace();
        }
    }

    public void parseMessage(String message){
        // Revisar el checksum aca
            // leer el mensaje
            String[] msg = Utils.split(message, ",");
            switch (msg[0]) {
                // esto se podria hacer con una clase 'NMEAsentenceReader' implementada para cada tipo de mensaje, pero no se
                // que tanto valga la pena, primero se planea implementar asi, luego evaluar si es mejor abstraerlo mas.
                case "$GPRMC":
                    RMCReader(msg);
                    break;
                case "$GPGGA":
                    GGAReader(msg);
                    break;
                case "$GPGSV":
                    GSVReader(msg);
                    break;
                case "$GPVTG":
                    VTGReader(msg);
                    break;
                case "$GPGSA":
                    GSAReader(msg);
                    break;
                default:
                    //System.out.println("Mensaje, "+msg[0]+" no soportado para lectura");
                    break;
            }
        // si falla el checksum avisar (pero no terminar el programa)
        // else {
        //     System.out.println("Checksum requirements were not met, message ignored.");
        // }
    }

    /**
     * Simple checksum calculation for a NMEA message.
     * @param msg The message to check.
     * @return true if the coded message equals the checksum, false otherwise.
     */

    public boolean checkSum(String msg) {
        // se ignoran estos 2 caracteres
        String newMsg = msg.replace("I", "");
        newMsg = newMsg.replace("$", "");
        // el 0 es el mensaje y el 1 es el codigo checksum
        String[] msg_cs = Utils.split(newMsg, "*");

        int result = 0;
        for(int i = 0; i < msg_cs[0].length(); i++) {
            result ^= msg_cs[0].charAt(i);
        }

        int checksum = Integer.parseInt(msg_cs[1], 16);

        return checksum == result;
    }

    public void RMCReader(String[] msg) {
        //data[0] = Double.parseDouble(msg[3].substring(0, 2));
        data[0] = Double.parseDouble(msg[3].substring(0, 2));
        this.gps.valoresRealesActuales[1] = data[0];
        data[1] = Double.parseDouble(msg[3].substring(2));
        this.gps.valoresRealesActuales[2] = data[1];
        switch (msg[4]) {
            case "N":
                this.gps.valoresRealesActuales[0] = 1; // Latitude_direction
                break;
            case "S":
                this.gps.valoresRealesActuales[0] = -1;
                break;
            default:
                System.out.println("ERROR: Valor "+msg[4]+" no identificado como direccion de latitud.");
        }
        data[3] = Double.parseDouble(msg[5].substring(0, 3));
        this.gps.valoresRealesActuales[4] = data[3];
        data[4] = Double.parseDouble(msg[5].substring(3));
        this.gps.valoresRealesActuales[5] = data[4];
        switch (msg[6]) {
            case "E":
                this.gps.valoresRealesActuales[3] = 1; // Longitude_direction
                break;
            case "W":
                this.gps.valoresRealesActuales[3] = -1;
                break;
            default:
                System.out.println("ERROR: Valor "+msg[6]+" no identificado como direccion de longitud.");
                // todo: no se si un mensaje de aviso basta o es mejor tirar un error.
        }
    }

    public void GGAReader(String[] msg) {
        data[0] = Double.parseDouble(msg[2].substring(0, 2));
        this.gps.valoresRealesActuales[1] = data[0];
        data[1] = Double.parseDouble(msg[2].substring(2));
        this.gps.valoresRealesActuales[2] = data[1];
        switch (msg[3]) {
            case "N":
                this.gps.valoresRealesActuales[0] = 1; // Latitude_direction
                break;
            case "S":
                this.gps.valoresRealesActuales[0] = -1;
                break;
            default:
                System.out.println("ERROR: Valor "+msg[4]+" no identificado como direccion de latitud.");
        }
        data[3] = Double.parseDouble(msg[4].substring(0, 3));
        this.gps.valoresRealesActuales[4] = data[3];
        data[4] = Double.parseDouble(msg[4].substring(3));
        this.gps.valoresRealesActuales[5] = data[4];
        switch (msg[5]) {
            case "E":
                this.gps.valoresRealesActuales[3] = 1; // Longitude_direction
                break;
            case "W":
                this.gps.valoresRealesActuales[3] = -1;
                break;
            default:
                System.out.println("ERROR: Valor "+msg[5]+" no identificado como direccion de longitud.");
                // todo: no se si un mensaje de aviso basta o es mejor tirar un error.
        }
    }

    public void VTGReader(String[] msg) {
        //System.out.println("Mensaje VTG no requerido.");
    }

    public void GSVReader(String[] msg) {
        //System.out.println("Mensaje GSV no requerido.");
    }

    public void GSAReader(String[] msg) {
        //System.out.println("Mensaje GSA no requerido.");
    }
}
