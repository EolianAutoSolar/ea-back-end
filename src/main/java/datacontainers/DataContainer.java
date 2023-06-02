package datacontainers;

import java.util.LinkedList;

import services.Service;

/**
 * Representa como se van a guardar los datos dentro de la ejecución del backend.
 * En general se asocia un componente a una fuente de datos dentro del auto ("bms", "gps") pero
 * su uso es bastante flexible, por ejemplo se podrían mezclar 2 fuentes de datos en un mismo AppComponent.
 * La estructura de cada AppComponent esta fuertemente relacionada con el {@link gatherers.Gatherer}
 * que va a leer sus datos asociados.
 * Cada AppComponent tiene un State del protocolo asociado (necesario para la comunicación inalámbrica).
 */
public class DataContainer {
    /** ID del Componente. */
    public String ID;
    /** Valor mínimo posible de cada parámetro (usado para envío inalámbrico) */
    public double[] minimosConDecimal;
    /** Valor máximo posible de cada parámetro (usado para envío inalámbrico) */
    public double[] maximosConDecimal;
    /** Nombre de cada parámetro. */
    public String[] nombreParametros;
    /** Valor de cada parámetro. */
    public double[] valoresRealesActuales;  // Valores reales provenientes de lecturas reales. Se actualizan cada vez
    /** Cantidad de parámetro que guarda el AppComponent */
    public int len;                         // Deducido. Se calcula una vez. Número de valores en componente. Se usa en varios for()
    /** @deprecated  */
    LinkedList<Service> mySubscriptions;    // Servicios a los que les comunico mis updates

    /**
     * Inicia los objetos con constructores default
     */
    private DataContainer(){
        this.mySubscriptions = new LinkedList<>();
    }

    /**
     * AppComponent sólo se caracteriza por sus valores mínimos, máximos, y su ID que se usará para muchas cosas (eventos de socket.io por ejemplo).
     * Debe tener asociado un State de la capa inferior.
     * @param id Nombre del AppComponent
     * @param minimosConDecimal Valores mínimos de cada valor del componente
     * @param maximosConDecimal Valores máximos de cada valor del componente
     * @param nombreParametros Nombre de los parámetros del componente actual
     */
    public DataContainer(String id, double[] minimosConDecimal, double[] maximosConDecimal, String[] nombreParametros) {
        this();
        this.ID = id;
        this.minimosConDecimal = minimosConDecimal;
        this.maximosConDecimal = maximosConDecimal;
        this.nombreParametros = nombreParametros;
        this.len = minimosConDecimal.length;
        this.valoresRealesActuales = new double[len];
    }

    /**
     * DIRECT UPDATE
     * Reemplaza valores double en capa de Aplicación.
     * Lo ejecutan los Sensors Readers. También llamadas en AppReceiver al hacer update con nuevos valores
     * @param newValoresReales Valores nuevos reales provienentes de lecturas/arrivo de mensajes
     * @throws Exception Si es el array de nuevos valores proporcionados no tiene el mismo largo que pre-configuraciones
     */
    public synchronized void updateValues(double[] newValoresReales) throws Exception{
        if(newValoresReales.length != len){
            throw new Exception("updateFromReading: Array de valores nuevo no tiene el mismo largo que preconfiguraciones");
        }
        this.valoresRealesActuales = newValoresReales; // TODO: Analizar eficiencia: si reemplaza puntero, o valor a valor, o si se crean objetos nuevos
    }

    /**
     * AppComponent se suscribe al servicio indicado
     * @param service Servicio a suscribirse
     */
    public void subscribeToService(Service service){
        this.mySubscriptions.add(service);
    }

    /**
     * Manda a todos las suscripciones, un "heads-up" que este componente tiene nuevos valores
     */
    public synchronized void informToServices(){
        for (Service s: mySubscriptions
             ) {
            s.putComponentInQueue(this); // Me pongo en cola del servicio
        }
    }

    /**
     * Hace un for secuencial para cada servicio, haciendo que usen los datos del AppComponente en forma secuencial.
     */
    public void sequentialInformToServices(){
        for (Service s: mySubscriptions
        ) {
            s.sequentialRun(this); // Me pongo en cola del servicio
        }
    }

    /**
     * Retorna el ID del AppComponent (= ID del State de capa inferior)
     * @return ID tipo String del AppComponent
     */
    public String getID() {
        return this.ID;
    }


    public double[] getValoresRealesActuales() {
        return this.valoresRealesActuales;
    }

    public double[] getMinimosConDecimal() {
        return minimosConDecimal;
    }

    public double[] getMaximosConDecimal() {
        return maximosConDecimal;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.ID);
        sb.append("\n");
        for(int i = 0; i < len; i++){
            sb.append(this.nombreParametros[i]);
            sb.append(" : [");sb.append(this.minimosConDecimal[i]);sb.append(" | ");sb.append(this.maximosConDecimal[i]);sb.append("] ");
            sb.append(this.valoresRealesActuales[i]);
            sb.append("\n");
        }
        return sb.toString();
    }
}
