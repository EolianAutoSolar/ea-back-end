package gatherers;

import java.util.List;

import datacontainers.DataContainer;
import services.Service;

public class NullChannel extends Gatherer {

    public NullChannel(List<DataContainer> myComponentList, List<Service> myServices) {
        super(myComponentList, myServices);
    }
    
    @Override
    public void readingLoop() {
        // TODO Auto-generated method stub
        while(true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            super.informServices();
        }
        
    }

    @Override
    public void setUp() {
        // TODO Auto-generated method stub
        
    }
    
}
