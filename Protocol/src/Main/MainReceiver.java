package Main;

import Components.Component;
import LocalSystems.DatabaseAdmin.DatabaseAdmin;
import LocalSystems.LocalMasterAdmin;
import LocalSystems.ServerAdmin.ServerAdmin;
import Protocol.Initializer.Initializer;
import Protocol.Messages.Message;
import Protocol.Receiving.ReceiverAdmin;
import Protocol.Receiving.XbeeReceiver;
//import com.sun.security.ntlm.Server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MainReceiver {
    public static void main(String ... args) throws Exception {
        /*--------------------- Constantes ---------------------*/
        char baseHeader = 'A';
        int msgLimitSize = 8 + 16 + 8; // 8 bit header, 16 bit contenido, 8 bit CRC8
        int BAUD_RATE = 9600;
        String PORT_RECEIVER = "COM6";
        String REMOTE_IDENTIFIER = "EOLIAN FENIX"; // Yo tengo este nombre en mi Xbee

        /*------------------ Clases de recibir ------------------*/

        XbeeReceiver xbeeReceiver = new XbeeReceiver(BAUD_RATE, PORT_RECEIVER);
        ServerAdmin serverAdmin = new ServerAdmin("http://localhost:3000/update");
        DatabaseAdmin databaseAdmin = new DatabaseAdmin();
        LocalMasterAdmin localMasterAdmin = new LocalMasterAdmin(serverAdmin, databaseAdmin, false); // Todos los componentes lo deben conocer para ponerse en su Queue y que el LocalMasterAdmin los revise

        /*--------------------- Componentes ---------------------*/
        int[] valores_0 = {0, 0, 0, 0, 0};
        int[] bitSig_0 = {8, 8, 2, 8, 5};
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


        // Al finalizar, finalizar también los executors
        xbeeReceiverExecutor.shutdown();
        receiverAdminExecutor.shutdown();
        localMasterAdminExecutor.shutdown();

    }
}
