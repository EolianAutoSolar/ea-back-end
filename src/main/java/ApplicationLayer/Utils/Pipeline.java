package ApplicationLayer.Utils;

import ApplicationLayer.Channel.Channel;
import ApplicationLayer.Channel.ServiceRunner;

public class Pipeline implements Runnable {
    public void Pipeline(Channel channel, ServiceRunner serviceRunner){
        this.channel = channel;
        this.serviceRunner = serviceRunner;
    }

    public void runPipeline(){
        while(true) {
            this.channel.singleRead();
            this.serviceRunner.runServices();
        }
    }


    @Override
    public void run(){
        this.runPipeline();
    }
}
