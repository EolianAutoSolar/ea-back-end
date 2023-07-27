package executables;

import java.util.ArrayList;
import java.util.List;

import com.pi4j.system.SystemInfo;

import datacontainers.DataContainer;
import datacontainers.ExcelToAppComponent.CSVToAppComponent;
import gatherers.GathererRunner;
import gatherers.Kelly;
import gatherers.Lithiumate;
import gatherers.Gatherer;
import services.Service;
import services.ServiceRunner;
import services.WebSocketServer;
import services.WirelessService.WirelessReceiver;
import services.DatabaseService;

public class Receiver {

    public static void main(String[] args) throws Exception {
        boolean encrypt = false;
        String xbeePort = "/dev/ttyUSB0";
        String componentsPath = "/home/pi/Desktop/components/auriga/";
        String databasePort = "5432";
        for(int i = 0; i < args.length; i++) {
            try {
                if(args[i].equals("--xbee")) {
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
        List<DataContainer> components = CSVToAppComponent.CSVs_to_AppComponents(componentsPath);

        // Services
        List<Service> services = new ArrayList<>();
        WirelessReceiver receiver = new WirelessReceiver(components, encrypt, services);
        WebSocketServer wss = new WebSocketServer();
        DatabaseService dbs = new DatabaseService(components, databasePort);
        //DatabaseService dbs = new DatabaseService(components, databasePath);

        services.add(receiver);
        services.add(wss);
        services.add(dbs);

        List<Gatherer> channels = new ArrayList<>();
        // Channels
        Lithiumate can1 = new Lithiumate(components);
        Kelly can0 = new Kelly(components);
        channels.add(can1);
        channels.add(can0);

        GathererRunner cr = new GathererRunner(channels, 300);
        Thread channelsThread = new Thread(cr);
        channelsThread.start();
        
        ServiceRunner sr = new ServiceRunner(services, 100);
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
