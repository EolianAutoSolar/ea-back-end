package gatherers;

import services.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import datacontainers.DataContainer;

/**
 * Lee las fuentes de datos del sistema y guarda lo leído en su {@link DataContainer} respectivo.
 */

public abstract class Gatherer implements Runnable {
    public HashMap<String, DataContainer> myComponentsMap; // To modify values with cost O(1) inside readingLoop()
    public List<DataContainer> myComponentList ; // List that Services will receive
    protected final List<Service> myServices; // List of services that need to know about myComponents updates
    public String id = "null";
    /**
     * Each channel has predefined AppComponents
     * @param myComponentList List of AppComponent that this Channel update values to
     * @param myServices Services to inform to whenever an AppComponents get updated
     */
    public Gatherer(List<DataContainer> myComponentList, List<Service> myServices) {
        this.myServices = myServices;
        this.myComponentList = myComponentList;
        this.myComponentsMap = new HashMap<>();
        for (DataContainer a: myComponentList
             ) {
            this.myComponentsMap.put(a.ID, a);
        }
    }

    /**
     * Each channel has predefined AppComponents
     * @param myComponentList List of AppComponent that this Channel update values to
     * @param myServices Services to inform to whenever an AppComponents get updated
     */
    public Gatherer(List<DataContainer> myComponentList, List<Service> myServices, String[] componentIds) {
        this.myServices = myServices;
        this.myComponentList = new ArrayList<>();
        this.myComponentsMap = new HashMap<>();
        for (DataContainer a: myComponentList) {
            for(String component : componentIds) {
                // Pertenece a la lista de componentes del Channel
                // Le puse lower case y un contains por flexibilidad, por ejemplo el bms esta, bms_1, bms_2, etc...
                if(a.ID.toLowerCase().contains(component.toLowerCase())) {
                    this.myComponentList.add(a);
                    this.myComponentsMap.put(a.ID, a);
                    break;
                }
            }
        }
    }

    /**
     * Commands executed recurrently. Parsing process.
     * Has a while True loop.
     * At the end of each reading, it executes informServices()
     */
    public abstract void readingLoop();

    /**
     * Any command that only needs to be executed once
     */
    public abstract void setUp();

    /**
     * Method executed once the channel updates all his AppComponents.
     * Called at the end of one reading cycle.
     */
    public void informServices(){
        for (Service s : this.myServices
             ) {
            s.putListOfComponentsInQueue(myComponentList);
        }
    }

    /**
     * Init the channel, and the loop.
     */
    @Override
    public void run() {
        try{
            this.setUp();
            this.readingLoop();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Lee 1 corrida de datos.
     */
    public void singleRead() {}
}
