package executables;

import java.util.ArrayList;
import java.util.List;

import com.pi4j.system.SystemInfo;

import datacontainers.DataContainer;
import datacontainers.ExcelToAppComponent.CSVToAppComponent;
import gatherers.GathererRunner;
import gatherers.Gatherer;
import gatherers.Kelly;
import gatherers.Lithiumate;
import services.DatabaseService;
import services.Service;
import services.ServiceRunner;
import services.WebSocketServer;
import services.WirelessService.WirelessSender;

public class Sender {

    public static void main(String[] args) throws Exception {
        boolean encrypt = false;
        String xbeePort = "/dev/ttyUSB0";
        // String componentsPath = "/home/pi/Desktop/components/auriga/";
        String componentsPath = "/home/jayki/Desktop/eolian/components/";
        // String databasePath = "/home/pi/Desktop/";
        String databasePath = "/home/jayki/Desktop/eolian/data/";
        for(int i = 0; i < args.length; i++) {
            try {
                if(args[i].equals("--xbee")) {
                    xbeePort = args[i+1];
                }
                else if(args[i].equals("--out")) {
                    databasePath = args[i+1];
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
                System.out.println("         --out <path>");
                System.out.println("         --in <path>");
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
        WirelessSender ws = new WirelessSender(components, xbeePort, encrypt);
        WebSocketServer wss = new WebSocketServer();
        //DatabaseService dbs = new DatabaseService(components, databasePath);

        services.add(ws);
        services.add(wss);
        //services.add(dbs);

        List<Gatherer> channels = new ArrayList<>();
        // Channels
        Lithiumate can1 = new Lithiumate(components);
        Kelly can0 = new Kelly(components);

        channels.add(can1);
        channels.add(can0);

        GathererRunner cr = new GathererRunner(channels, 1000);
        Thread channelsThread = new Thread(cr);
        channelsThread.start();
        
        ServiceRunner sr = new ServiceRunner(services, 1000);
        sr.run();
    }
}
