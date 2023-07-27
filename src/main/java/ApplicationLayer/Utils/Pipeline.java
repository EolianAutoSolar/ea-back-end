package ApplicationLayer.Utils;

import ApplicationLayer.Channel.Channel;
import ApplicationLayer.Channel.ServiceRunner;

public class Pipeline implements Runnable {
    public Channel channel;
    public ServiceRunner serviceRunner;
    public void Pipeline(Channel channel, ServiceRunner serviceRunner){
        channel = channel;
        serviceRunner = serviceRunner;
    }

    //Ejecuta de manera secuencial un singleRead y luego los servicios.
    public void runPipeline(){
        while(true) {
            channel.singleRead();
            serviceRunner.runServices();
        }
    }


    @Override
    public void run(){
        this.runPipeline();
    }
}
