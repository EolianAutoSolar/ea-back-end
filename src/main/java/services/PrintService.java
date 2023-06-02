package services;

import datacontainers.DataContainer;

/**
 * Simple print AppComponent's real values
 */
public class PrintService extends Service{
    private String printPrefix;

    public PrintService(){
        this("NN");
    }

    public PrintService(String printPrefix){
        this.printPrefix = printPrefix;
    }
    @Override
    protected void serve(DataContainer c) {
        System.out.print(printPrefix+"==>");
        for(int i = 0; i < c.valoresRealesActuales.length; i++) {
            System.out.print(c.nombreParametros[i]+":"+c.valoresRealesActuales[i]+", ");
        }
        System.out.println("\n");
    }
}
