package ToReview.Refactor4Tests;
//package Test.Refactor4Tests;
//
//import AppComponents.AppComponent;
//import AppComponents.AppReceiver;
//import AppComponents.AppSender;
//import LocalServices.WirelessService.WirelessReceiver;
//import LocalServices.WirelessService.WirelessSender;
//import LocalServices.WirelessService.ZigBeeLayer.XbeeReceiver;
//import SensorReading.DelayControl;
//import SensorReading.RandomReaders.RandomReader;
//import SensorReading.SensorsReader;
//import AppComponents.ExcelToAppComponent.CSVToAppComponent;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//
//public class NoXbeeTest {
//    XbeeReceiver xbeeReceiver;
//
//    void receiverSetup() throws Exception {
//        String dir = "src/main/java/ApplicationLayer/AppComponents/ExcelToAppComponent/Eolian_fenix";
//        List<AppReceiver> appReceivers = CSVToAppComponent.CSVs_to_AppReceivers(dir);
//
//        // High Level Services
//        //PrintService printService = new PrintService();
//        WirelessReceiver wirelessReceiver = new WirelessReceiver(appReceivers);
//
//        this.xbeeReceiver = wirelessReceiver.getXbeeReceiver(); // Save globally to pass to xbeeSender
//
//        //for (AppComponent ac: appReceivers) {ac.subscribeToService(printService); }
//
//        // Execute threads
//        ExecutorService mainExecutor = Executors.newFixedThreadPool(2);
//
//        // Init threads
//        //mainExecutor.submit(wirelessReceiver); // Crea 2 threads más
//        mainExecutor.submit(wirelessReceiver.getXbeeReceiver());
//        mainExecutor.submit(wirelessReceiver.getReceiverAdmin());
//        //mainExecutor.submit(printService);
//
//        mainExecutor.shutdown();
//    }
//
//    /**
//     * Execute afet recevierSetup due to xbeeReceiver to pass to xbeeSender (testing only)
//     * @throws Exception
//     */
//    void senderSetup() throws Exception {
//        String dir = "src/main/java/ApplicationLayer/AppComponents/ExcelToAppComponent/Eolian_fenix";
//        List<AppSender> appSenders = CSVToAppComponent.CSVs_to_AppSenders(dir);
//        LinkedList<RandomReader> randomReaders = new LinkedList<>();
//        LinkedList<SensorsReader> readers = new LinkedList<>();
//
//        // Random reader
//        for (AppSender as: appSenders) {
//            RandomReader rd = new RandomReader(as, 1000);
//            randomReaders.add(rd);
//            readers.add((SensorsReader) rd);
//        }
//
//        // High Level Services
//        //PrintService printService = new PrintService();
//        WirelessSender wirelessSender = new WirelessSender(appSenders, xbeeReceiver);
//        DelayControl delayControl = new DelayControl(wirelessSender.getXbeeSender(), readers);
//
//        for (AppComponent ac: appSenders) {
//            //ac.subscribeToService(printService);
//            ac.subscribeToService(wirelessSender);
//        }
//
//        // Execute threads
//        ExecutorService mainExecutor = Executors.newFixedThreadPool(200);
//
//        // Init threads
//        mainExecutor.submit((wirelessSender.getXbeeSender()));  // XbeeSender thred
//        mainExecutor.submit(wirelessSender);                    // serve(AppComponent) to putInXbeeQueue() thread
//        //mainExecutor.submit(printService);
//        mainExecutor.submit(delayControl);
//
//        // Sensor readers
//        for (SensorsReader sr : randomReaders){
//            mainExecutor.submit(sr);
//        }
//
//        mainExecutor.shutdown();
//    }
//
//
//    void basicTest() throws Exception {
//        receiverSetup();
//        senderSetup();
//    }
//
//    public static void main(String[] args) throws Exception{
//        Test.Refactor4Tests.NoXbeeTest noXbeeTest = new Test.Refactor4Tests.NoXbeeTest();
//        noXbeeTest.basicTest();
//    }
//
//}
