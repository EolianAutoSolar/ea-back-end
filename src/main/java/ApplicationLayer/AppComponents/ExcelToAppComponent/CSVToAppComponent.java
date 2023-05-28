package ApplicationLayer.AppComponents.ExcelToAppComponent;

import ApplicationLayer.AppComponents.AppComponent;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Carga los .csv desde un directorio y los transforma en una lista de AppComponents.
 * En los .csv se debe crear una columna por parámetro que se quiera manejar, cada columna
 * debe tener al menos 3 filas:
 * <ol>
 *     <li>Nombre del parámetro (ej. "voltaje", "corriente")</li>
 *     <li>Valor mínimo del parámetro con los decimales de presición que se quieren manejar (ej 0.1)</li>
 *     <li>Valor mínimo del parámetro con los decimales de presición que se quieren manejar (ej 1.0)</li>
 * </ol>
 * Por ejemplo en la siguiente tabla se específica guardar el valor "voltaje" que va desde 0.1 hasta 1. Como se
 * especifíca 1 decimal, este valor puede ser 0.1, 0.2, 0.3, ..., 1.0.
 * <table border="3">
 *  <tr><th>voltaje</th> <th>nombre parámetro 2</th> <th>...</th></tr>
 *  <tr><td>0.1</td> <td>mínimo parámetro 2</td> <td>...</td></tr>
 *  <tr><td>1</td> <td>máximo parámetro 2</td> <td>...</td></tr>
 * </table>
 *
 * La idea es que esta clase se use desde los métodos de inicialización,
 * para siempre inicializar los AppComponents desde los últimos .csv's.
 */
public class CSVToAppComponent {

    private static final String COMMA_DELIMITER = ",";

    /**
     * Lista todos los archivos .CSV desde un directorio
     * @param directory Directorio base
     * @return Lista de nombre de archivos .csv
     */
    public static List<String> listFilesForFolder(String directory) {
        System.out.println(directory);
        List<String> filenames = new LinkedList<String>();
        File folder = new File(directory);
        System.out.println(folder.getAbsolutePath());

        File[] list = folder.listFiles();
        if (list != null) {
            for (File fileEntry : list) {
                if (fileEntry.isDirectory()) {
                    listFilesForFolder(fileEntry.getAbsolutePath());
                } else {
                    if (fileEntry.getName().contains(".csv"))
                        filenames.add(fileEntry.getName());
                }
            }
        }
        return filenames;
    }

    /**
     * Toma un nombre de archivo CSV y retorna una lista de listas de String, que son las filas del CSV
     * @param file_dir_name Nombre de archivo CSV. EJ: BMS.csv
     * @return Lista de Lista de Strings que son las filas del CSV
     */
    public static List<List<String>> readCSV(String file_dir_name){
        List<List<String>> records = new ArrayList<>();
        try {
            FileReader fd = new FileReader(file_dir_name);
            BufferedReader br = new BufferedReader(fd);
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }


    public static List<AppComponent> CSVs_to_AppComponents(String directory) throws Exception{
        LinkedList<AppComponent> list = new LinkedList<>();
        List<String> components = listFilesForFolder(directory);

        if (components.size() == 0)
            throw new Exception("CSVs_to_AppComponents: No se han leído componentes desde archivos CSV");

        for (String comp : components){
            List<List<String>> values = readCSV(directory + "/" + comp);
            String[] params = Arrays.copyOf(values.get(0).toArray(), values.get(0).toArray().length, String[].class);
            String[] min_str = Arrays.copyOf(values.get(1).toArray(), values.get(1).toArray().length, String[].class);
            String[] max_str = Arrays.copyOf(values.get(2).toArray(), values.get(2).toArray().length, String[].class);
            double[] min = new double[min_str.length];
            double[] max = new double[max_str.length];
            for (int i = 0; i < min_str.length; i++) {
                min[i] = Double.parseDouble(min_str[i]);
                max[i] = Double.parseDouble(max_str[i]);
            }
            list.add(new AppComponent(comp.split("\\.")[0], min, max, params));
        }

        return list;
    }
}
