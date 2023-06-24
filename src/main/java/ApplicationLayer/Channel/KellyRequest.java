package ApplicationLayer.Channel;

public class KellyRequest implements Runnable {

    public String channel;
    public String kellyId;

    public KellyRequest(String channel, String kellyId) {
        this.channel = channel;
        this.kellyId = kellyId;
    }

    public void run() {
        ProcessBuilder CCP_A2D_BATCH_READ2 = new ProcessBuilder("cansend", channel, (kellyId + "#1A"));
        ProcessBuilder CCP_MONITOR1 = new ProcessBuilder("cansend", channel, (kellyId + "#33"));
        ProcessBuilder CCP_MONITOR2 = new ProcessBuilder("cansend", channel, (kellyId + "#37"));
        ProcessBuilder COM_SW_ACC = new ProcessBuilder("cansend", channel, (kellyId + "#42"));
        ProcessBuilder COM_SW_REV = new ProcessBuilder("cansend", channel, (kellyId + "#44"));
        
        try {
            CCP_A2D_BATCH_READ2.start();
            CCP_MONITOR1.start();
            CCP_MONITOR2.start();
            COM_SW_ACC.start();
            COM_SW_REV.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
        // read output 
    }
}
