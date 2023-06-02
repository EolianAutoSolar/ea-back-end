package ToReview.Refactor3Tests;
//package Test.Refactor3Tests;
//import AppComponents.AppComponent;
//import AppComponents.AppReceiver;
//import AppComponents.AppSender;
//import LocalServices.PrintService;
//import LocalServices.WirelessService.WirelessReceiver;
//import LocalServices.WirelessService.WirelessSender;
//import LocalServices.WirelessService.ZigBeeLayer.XbeeReceiver;
//import SensorReading.RandomReaders.RandomReader;
//import SensorReading.SensorsReader;
//import SensorReading.SequentialReaderExecutor;
//import AppComponents.ExcelToAppComponent.CSVToAppComponent;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//
//public class XbeeRandomTest {
//    XbeeReceiver xbeeReceiver;
//
//    void receiverSetup() throws Exception {
//        String dir = "src/AppComponents.ExcelToAppComponent/Eolian_fenix";
//        List<AppReceiver> appReceivers = CSVToAppComponent.CSVs_to_AppReceivers(dir);
//
//        // High Level Services
//        PrintService printService = new PrintService();
//        WirelessReceiver wirelessReceiver = new WirelessReceiver(appReceivers, "COM6");
//
//        //this.xbeeReceiver = wirelessReceiver.getXbeeReceiver(); // Save globally to pass to xbeeSender
//
//        for (AppComponent ac: appReceivers) {ac.subscribeToService(printService); }
//
//        // Execute threads
//        ExecutorService mainExecutor = Executors.newFixedThreadPool(4);
//
//        // Init threads
//        //mainExecutor.submit(wirelessReceiver); // Crea 2 threads más
//        mainExecutor.submit(wirelessReceiver.getXbeeReceiver());
//        mainExecutor.submit(wirelessReceiver.getReceiverAdmin());
//        mainExecutor.submit(printService);
//
//        mainExecutor.shutdown();
//    }
//
//    /**
//     * Execute afet recevierSetup due to xbeeReceiver to pass to xbeeSender (testing only)
//     * @throws Exception
//     */
//    void senderSetup() throws Exception {
//        String dir = "src/AppComponents.ExcelToAppComponent/Eolian_fenix";
//        List<AppSender> appSenders = CSVToAppComponent.CSVs_to_AppSenders(dir);
//        LinkedList<RandomReader> randomReaders = new LinkedList<>();
//
//        // Random reader
//        for (AppSender as: appSenders) {
//            randomReaders.add(new RandomReader(as, 1000));
//        }
//
//        // Sequential executor of readers
//        SequentialReaderExecutor sequentialReaderExecutor = new SequentialReaderExecutor();
//        for (SensorsReader sr : randomReaders) { sequentialReaderExecutor.addReader(sr); }
//
//        // High Level Services
//        PrintService printService = new PrintService();
//        WirelessSender wirelessSender = new WirelessSender(appSenders, "/dev/ttyUSB0");
//
//        for (AppComponent ac: appSenders) {
//            ac.subscribeToService(printService);
//            ac.subscribeToService(wirelessSender);
//        }
//
//        // Execute threads
//        ExecutorService mainExecutor = Executors.newFixedThreadPool(4);
//
//        // Init threads
//        mainExecutor.submit((wirelessSender.getXbeeSender()));  // XbeeSender thred
//        mainExecutor.submit(wirelessSender);                    // serve(AppComponent) to putInXbeeQueue() thread
//
//        mainExecutor.submit(sequentialReaderExecutor);
//        mainExecutor.submit(printService);
//
//        mainExecutor.shutdown();
//    }
//
//
//    public static void main(String[] args) throws Exception{
//        XbeeRandomTest xbeeRandomTest = new XbeeRandomTest();
//        String device = "PC"; // Use Raspberry otherwise
//
//        if (device.equals("PC")){
//            xbeeRandomTest.receiverSetup();
//        }else if (device.equals("RASPBERRY")){
//            xbeeRandomTest.senderSetup();
//        }
//
//    }
//
//}
