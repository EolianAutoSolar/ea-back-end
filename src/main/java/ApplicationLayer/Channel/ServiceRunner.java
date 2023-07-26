package ApplicationLayer.Channel;

import java.util.List;
import ApplicationLayer.LocalServices.Service;

public class ServiceRunner implements Runnable {

    public List<Service> services;
    public int delay;
    
    public ServiceRunner(List<Service> services, int delay) {
        this.services = services;
        this.delay = delay;
    }

    public void runServices() {
        for(Service s : services) {
            System.out.println("Executing service ");
            System.out.println(s.id);
            s.consumeComponents();
        }
    }

    @Override
    public void run() {
        while(true){
            runServices();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}
