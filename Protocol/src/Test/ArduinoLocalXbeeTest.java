package Test;

import Components.Component;
import LocalSystems.DatabaseAdmin.DatabaseAdmin;
import LocalSystems.LocalMasterAdmin;
import LocalSystems.ServerAdmin.ServerAdmin;
import Protocol.Initializer.Initializer;
import Protocol.Messages.Message;
import Protocol.Receiving.ReceiverAdmin;
import Protocol.Receiving.XbeeReceiver;
import Protocol.Sending.SenderAdmin;
import Protocol.Sending.XbeeSender;
import SensorReading.ArduinoReader;
import SensorReading.RandomReader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArduinoLocalXbeeTest {

    @org.junit.jupiter.api.Test
    public static void main(String ... args) throws Exception{
        ///////////////////////// Main Reciver /////////////////////////
        /*--------------------- Constantes ---------------------*/
        boolean serverON = true; // True si servidor NodeJS está corriendo para probar el proceso completo
        char baseHeader = 'A';
        int msgLimitSize = 8 + 16 + 8; // 8 bit header, 16 bit contenido, 8 bit CRC8
        int BAUD_RATE = 9600;
        String PORT_RECEIVER = "COM6";
        //String REMOTE_IDENTIFIER = "EOLIAN FENIX"; // Yo tengo este nombre en mi Xbee, no necesario, ahora se hace broadcast


        /*------------------ Clases de recibir ------------------*/

        XbeeReceiver xbeeReceiver = new XbeeReceiver(BAUD_RATE, PORT_RECEIVER);
        ServerAdmin serverAdmin = new ServerAdmin("http://localhost:3000/update");
        DatabaseAdmin databaseAdmin = new DatabaseAdmin();
        LocalMasterAdmin localMasterAdmin = new LocalMasterAdmin(serverAdmin, databaseAdmin, serverON); // Todos los componentes lo deben conocer para ponerse en su Queue y que el LocalMasterAdmin los revise

        /*--------------------- Componentes ---------------------*/
        int[] valores_0 = {0, 0, 0, 0, 0};
        int[] bitSig_0 = {16, 8, 8, 8, 1};
        Component BMS_destino = new Component(localMasterAdmin, valores_0, bitSig_0, "BMS_DESTINO");

        LinkedList<Component> listAllComponents_destino = new LinkedList<>();
        listAllComponents_destino.add(BMS_destino);

        /*--------------------- Inicializador ---------------------*/

        Initializer initializer_detino = new Initializer(listAllComponents_destino, msgLimitSize, baseHeader);
        HashMap<Character, Message> messages_destino = initializer_detino.genMessages();

        /*----------------- Generador de mensajes -----------------*/

        ReceiverAdmin receiverAdmin = new ReceiverAdmin(1, xbeeReceiver, messages_destino);

        /*---------------- Threads de cada clase -----------------*/

        ExecutorService xbeeReceiverExecutor = Executors.newFixedThreadPool(1);
        ExecutorService receiverAdminExecutor = Executors.newFixedThreadPool(1);
        ExecutorService localMasterAdminExecutor = Executors.newFixedThreadPool(1);

        xbeeReceiverExecutor.submit(xbeeReceiver);
        receiverAdminExecutor.submit(receiverAdmin);
        localMasterAdminExecutor.submit(localMasterAdmin);

        ///////////////////////// Main Sender /////////////////////////

        /*--------------------- Constantes ---------------------*/

        String PORT_SENDER = "COM4";

        /*------------------- Clases de envío -------------------*/

        XbeeSender xbeeSender = new XbeeSender(BAUD_RATE, PORT_SENDER);
        SenderAdmin senderAdmin = new SenderAdmin(xbeeSender);

        /*--------------------- Componentes ---------------------*/

        // 2 4 8 16 32 64 128 256 512 1024 2048
        // 1 2 3 4  5  6  7   8   9   10   11
        int[] valores = {0, 0, 0, 0, 0};
        int[] bitSig =  {16, 8, 8, 8, 1};
        Component BMS_origen = new Component(senderAdmin, valores, bitSig, "BMS_ORIGEN");

        HashMap<String, Component> allComponents = new HashMap<>();
        allComponents.put(BMS_origen.getID(), BMS_origen);

        LinkedList<Component> listAllComponents_origen = new LinkedList<>();
        listAllComponents_origen.add(BMS_origen);

        /*--------------------- Inicializador ---------------------*/

        Initializer initializer_origen = new Initializer(listAllComponents_origen, msgLimitSize, baseHeader);
        initializer_origen.genMessages(); // Para enviar no se necesita HashMap de Messages

        System.out.println("Initializaer origen: ");
        System.out.println(BMS_origen.printMessagesWithIndexes());


        /*------------------ Lectura de datos ------------------*/

        System.out.println("BMS DESTINO ANTES DE ENVÍO DE MENSAJE : \n" + BMS_destino.toString());

        String ARDUINO_PORT = "COM7";
        int ARDUINO_BAUD_RATE = 9600;
        int ARDUINO_TIMEOUT = 2000;

        ArduinoReader sensorsReader = new ArduinoReader(allComponents, listAllComponents_origen, ARDUINO_PORT, ARDUINO_BAUD_RATE, ARDUINO_TIMEOUT);

        /*---------------- Threads de cada clase -----------------*/

        ExecutorService xbeeSenderExecutor = Executors.newFixedThreadPool(1);
        ExecutorService senderAdminExecutor = Executors.newFixedThreadPool(1);
        ExecutorService sensorsReaderExecutor = Executors.newFixedThreadPool(1);

        xbeeSenderExecutor.submit(xbeeSender);
        senderAdminExecutor.submit(senderAdmin);
        sensorsReaderExecutor.submit(sensorsReader);


        /////////////////////////// ShutDown //////////////////////////

        // Al finalizar, finalizar también los executors
        xbeeReceiverExecutor.shutdown();
        receiverAdminExecutor.shutdown();
        localMasterAdminExecutor.shutdown();
        // Sender
        xbeeSenderExecutor.shutdown();
        senderAdminExecutor.shutdown();
        sensorsReaderExecutor.shutdown();
    }
}
