package Main;

import java.util.ArrayList;
import java.util.List;

import com.pi4j.system.SystemInfo;

import ApplicationLayer.AppComponents.AppComponent;
import ApplicationLayer.AppComponents.ExcelToAppComponent.CSVToAppComponent;
import ApplicationLayer.Channel.Canbus0;
import ApplicationLayer.Channel.Canbus1;
import ApplicationLayer.Channel.CanbusKelly;
import ApplicationLayer.Channel.Channel;
import ApplicationLayer.Channel.ChannelRunner;
import ApplicationLayer.Channel.ServiceRunner;
import ApplicationLayer.Channel.SphereChannel;
import ApplicationLayer.Channel.TestChannel;
import ApplicationLayer.LocalServices.DatabaseService;
import ApplicationLayer.LocalServices.Service;
import ApplicationLayer.LocalServices.WebSocketService;
import ApplicationLayer.LocalServices.WirelessService.WirelessSender;

public class MainRefactor {

    public static void main(String[] args) throws Exception {
        boolean dev = false;
        boolean encrypt = false;
        String xbeePort = "/dev/ttyUSB0";
        // String componentsPath = "/home/pi/Desktop/components/auriga/";
        String componentsPath = "/home/jayki/Desktop/eolian/components/";
        String databasePort = "5432";
        for(int i = 0; i < args.length; i++) {
            try {
                if(args[i].equals("--dev")) {
                    dev = true;
                }
                else if(args[i].equals("--xbee")) {
                    xbeePort = args[i+1];
                }
                else if(args[i].equals("--database")) {
                    databasePort = args[i+1];
                }
                else if(args[i].equals("--in")) {
                    componentsPath = args[i+1];
                }
                else if(args[i].equals("--encrypt")) {
                    encrypt = true;
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Usage: java -jar Main.jar [OPTIONS]");
                System.out.println("Options: --xbee <port>");
                System.out.println("         --database <port>");
                System.out.println("         --in <path>");
                System.out.println("         --dev");
                System.out.println("         --encrypt");
            }
        }

        System.out.println("Java Version      :  " + SystemInfo.getJavaVersion());
        if(!SystemInfo.getJavaVersion().equals("1.8.0_212")) {
            System.out.println("WARNING: Java version should be 1.8.0_212, the current version is "+SystemInfo.getJavaVersion());
        }
        System.out.println("Java VM           :  " + SystemInfo.getJavaVirtualMachine());
        System.out.println("Java Runtime      :  " + SystemInfo.getJavaRuntime());
        System.out.println("Main Sender");

        // Components
        List<AppComponent> components = CSVToAppComponent.CSVs_to_AppComponents(componentsPath);

        // Services
        List<Service> services = new ArrayList<>();
        //WirelessSender ws = new WirelessSender(components, xbeePort, encrypt);
        //PrintService ps = new PrintService("TX: ");
        WebSocketService wss = new WebSocketService(components);
        //DatabaseService dbs = new DatabaseService(components, databasePort);

        //services.add(ws);
        //ls.add(ps);
        services.add(wss);
        //services.add(dbs);

        List<Channel> channels = new ArrayList<>();
        // Channels
        Canbus1 can1 = new Canbus1(components, services);
        Canbus0 can0 = new Canbus0(components, services);
        TestChannel tc = new TestChannel(components, services);
        //channels.add(can1);
        //channels.add(can0);
        channels.add(tc);

        ChannelRunner cr = new ChannelRunner(channels, 1000);
        Thread channelsThread = new Thread(cr);
        channelsThread.start();
        
        ServiceRunner sr = new ServiceRunner(services, 1000);
        sr.run();
        // //Canbus0 can0 = new Canbus0(lac, ls, dev);
        // // Main loops
        // Thread t1 = new Thread(can1);
        // // Thread t5 = new Thread(can0);
        // // //Thread t2 = new Thread(ps);
        // Thread t3 = new Thread(ws);
        // Thread t4 = new Thread(wss);
        // Thread t9 = new Thread(dbs);
        // // t1.start();
        // // t5.start();
        // // //t7.start();
        // // //t2.start();
        // t3.start();
        // t4.start();
        // t9.start();
    }
}
