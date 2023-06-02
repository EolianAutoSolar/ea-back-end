package services;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import datacontainers.DataContainer;

/**
 * Un servicio representa una acción que procesa los datos almacenados en un {@link DataContainer}.
 * Por ejemplo: Visualización de datos o Almacenamiento de datos.
 */
public abstract class Service implements Runnable{
    
    BlockingQueue<DataContainer> componentsToBeChecked; // Cola de AppComponents
    public List<DataContainer> components;
    public String id = "Service";
    /**
     * Constructor de objetos default
     */
    public Service(){
        this.componentsToBeChecked = new LinkedBlockingDeque<>();
    }

    public Service(List<DataContainer> components) {
        this.components = components;
    } 

    /**
     * Puts a list of AppComponents un the list
     * @param c List of AppComponents to be reviewed
     */
    public synchronized void putListOfComponentsInQueue(List<DataContainer> c){
        for (DataContainer a: c
             ) {
            this.putComponentInQueue(a);
        }
    }

    /**
     * Añade un AppComponent en la queue por ser revisados
     * Varios AppComponent pueden llamar a este método a la vez, por eso el synchronized
     * @param c : AppComponent a poner en la Queue
     */
    public synchronized void putComponentInQueue(DataContainer c){
        if(!this.componentsToBeChecked.contains(c)){ // Sólo si no estoy, me agrego, esto toma a lo más O(largo lista) = O(número de Componentes) = O(5) ? = cte.
            this.componentsToBeChecked.add(c);
        }
    }

    /**
     * Consume y procesa todos los AppComponent de la Queue.
     * Procesan el componente de acuerdo al servicio que brindan.
     */
    private void consumeComponent(){
        while(!componentsToBeChecked.isEmpty()){
            //System.out.println("COMPONENTS TO BE CHECKED: " + this.componentsToBeChecked.size());
            DataContainer c = this.componentsToBeChecked.poll();
            this.serve(c); // Método para hacer algo con el AppComponent
        }
    }

    /**
     * Procesa el AppComponent recibido.
     * @param c AppComponent a consumir
     */
    protected abstract void serve(DataContainer c);


    public void consumeComponents() {
        for(DataContainer c : components) {
            System.out.println(c.getID());
            serve(c);
        }
    }

    /**
     * Debe sacar los componentes pendientes de su lista, si esta vacía no entra
     */
    @Override
    public void run() {
        while(true){
            try {
                consumeComponent(); // Siempre consume componentes y ejecuta el servicio
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Ejecución secuencial pasando AppComponent como argumento
     * @param c Componente a ser revisado por servicio
     */
    public void sequentialRun(DataContainer c){
        this.serve(c);
    }
}
