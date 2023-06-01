package ApplicationLayer.LocalServices;

import ApplicationLayer.AppComponents.AppComponent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * Guarda en tablas de postgresql, el estado actual de todos los {@link ApplicationLayer.AppComponents} presentes en la ejecución del programa.
 * El formato en el que guarda los datos es el mismo en el cual se reciben los .csv para cada AppComponent, más una columna asociada al Timestamp del registro, la cual se crea por defecto en la database.
 */

public class DatabaseService extends Service implements Runnable {

    public String[] components;
    private Connection conn;

    public DatabaseService(List<AppComponent> lac) {
        super(lac);
        this.id = "Database";

        String connectUrl = "jdbc:postgresql:eolianAuriga";
        Properties props = new Properties();
        props.setProperty("user", "backEndTelemetry");
        props.setProperty("password", "");

        try {
            this.conn = DriverManager.getConnection(connectUrl, props);
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Debe tomar el AppComponent y guardar sus valores en su tabla respectiva en la base de datos
     * Debe guardar el timestamp también
     *
     * @param c AppComponent a guardar en la base de datos
     */
    @Override
    protected void serve(AppComponent c) {
        if(c.getID().equals("lcd")) { //no tiene sentido logear los datos de la pantalla
            return;
        }
        try {
            // INSERT double[] en su tabla, sacar el timestamp del momento en que guarda
            writeValues(c.getValoresRealesActuales(), c.getID());

        } catch (Exception e) {
            e.printStackTrace(); // Sólo se hace print, el sistema no se puede caer
        }
    }

    /**
     * Añade una fila a la tabla ID con los valores de "values".
     * @param values Valores a agregar.
     * @param ID Nombre de la tabla
     */
    public void writeValues(double[] values, String ID) {

        try {
            String query = "INSERT INTO " + ID + " VALUES (DEFAULT";
            for(int i = 0; i < values.length; i++) {
                query += ",?";
            }
            query += ");";
            PreparedStatement stat = this.conn.prepareStatement(query);
            for(int i = 0; i < values.length; i++) {
                stat.setDouble(i+1, values[i]);
            }
            stat.executeUpdate();
            stat.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cierra la conneccion creada con la database.
     */
    public void close() {
        try {
            this.conn.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
