package ApplicationLayer.Channel;


public class KellyRequest implements Runnable {

    public String channel;
    public String[] kellyIds = {"068", "0C8"};

    //Recibe una strig con el channel y un arreglo de strings con los ids de todos los kellys disponibles en el canal.
    public KellyRequest(String channel) {
        channel = channel;
    }

    public void run() {
        // init process builder
        // start 4 commands to ask for data
        // Bucle for que ejecuta las 4 consultas para cada kelly.
        for(String kellyId : kellyIds){

            // launch command CCP_A2D_BATCH_READ1
            ProcessBuilder CCP_A2D_BATCH_READ1 = new ProcessBuilder("cansend", channel, (kellyId + "#1B"));
            // read output 

            // launch command CCP_A2D_BATCH_READ2
            ProcessBuilder CCP_A2D_BATCH_READ2 = new ProcessBuilder("cansend", channel, (kellyId + "#1A"));
            
            // read output 

            // launch command CCP_MONITOR1
            ProcessBuilder CCP_MONITOR1 = new ProcessBuilder("cansend", channel, (kellyId + "#33"));
            
            // read output 

            // launch command CCP_MONITOR2
            ProcessBuilder CCP_MONITOR2 = new ProcessBuilder("cansend", channel, (kellyId + "#37"));
            
            try {
                CCP_A2D_BATCH_READ1.start();
                CCP_A2D_BATCH_READ2.start();
                CCP_MONITOR1.start();
                CCP_MONITOR2.start();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        // read output 
    }
}
