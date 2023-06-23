package gatherers;

import java.util.List;

/*
 * Recibe una lista de {@link Gatherer} y los ejecuta secuencialmente.
 
 */
public class GathererRunner implements Runnable {

    public List<Gatherer> channels;
    public int delay;

    public GathererRunner(List<Gatherer> channels, int delay) {
        this.channels = channels;
        this.delay = delay;
        for(Gatherer c : channels) {
            c.setUp();
        }
    }

    public void runChannels() {
        while(true) {
            for(Gatherer c : channels) {
                System.out.println("Executing channel ");
                System.out.println(c.id);
                c.singleRead();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        runChannels();
    }

    
}
