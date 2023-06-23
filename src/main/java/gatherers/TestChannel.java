package gatherers;

import services.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import datacontainers.DataContainer;

public class TestChannel extends Gatherer{
    private HashMap<DataContainer, double[]> valuesMap;
    private final Random r;
    private List<DataContainer> myComponentList;

    /**
     * Each channel has predefined AppComponents
     *  @param myComponentList List of AppComponent that this Channel update values to
     * @param myServices Services to inform to whenever an AppComponents get updated
     */
    public TestChannel(List<DataContainer> myComponentList) {
        this.r = new Random();
        this.valuesMap = new HashMap<>();
        for (DataContainer a : myComponentList
             ) {
            valuesMap.put(a, new double[a.len]);
        }
    }

    @Override
    public void singleRead() {
        for (DataContainer a : myComponentList) {
            // Update values directly. Without the call of updateValues() inside AppComponent
            for (int i = 0; i < a.len; i++) {
                a.valoresRealesActuales[i] = a.minimosConDecimal[i] + (a.maximosConDecimal[i] - a.minimosConDecimal[i]) * this.r.nextDouble(); // Random value in adequate range
            }
        }
    }

    /**
     * No commands needed for Test random channel.
     */
    @Override
    public void setUp() {}
}
