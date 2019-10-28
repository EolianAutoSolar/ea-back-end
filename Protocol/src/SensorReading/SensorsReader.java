package SensorReading;

import Components.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SensorsReader {
    private HashMap<String, Component> allComponents;

    public SensorsReader(HashMap<String, Component> allComponents){
        this.allComponents = allComponents;
    }

    /**
     * Actualiza el array de enteros de un componente por el array de 'newValues'
     * @param componentName : Componente a actualizar
     * @param newValues : Nuevos valores a poner en array de componente
     */
    public void updateDirectly(String componentName, int[] newValues){
        allComponents.get(componentName).replaceMyValues(newValues);
    }

    /**
     * Método para testing
     * Lectura directa del BMS, luego reemplaza valores int[] en Component BMS.
     */
    public void readBMSTest(String bmsName, int sizeOfValues){
        Random r = new Random();
        int[] up = new int[sizeOfValues]; // Debe ser el orden exacto que hay en el Componente
        System.out.print("Array random generado : [ ");
        for (int i = 0; i < up.length; i++){
            up[i] = r.nextInt(255);
            System.out.print(up[i] + " ");
        }
        System.out.println("]");
        updateDirectly(bmsName, up);
    }

}
