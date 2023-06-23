package gatherers;

import datacontainers.DataContainer;

/**
 * Lee las fuentes de datos del sistema y guarda lo leído en su {@link DataContainer} respectivo.
 */

public abstract class Gatherer {
    public String id = "null";

    /**
     * Each channel has predefined {@link DataContainer}s
     */
    public Gatherer() {}

    /**
     * Any command that only needs to be executed once
     */
    public abstract void setUp();

    /*
     * Lee 1 corrida de datos.
     */
    public void singleRead() {}
}
