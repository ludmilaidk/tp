package entidades;

public abstract class Empleado {
    private String nombre;
    private int legajo;
    private boolean asignado;
    private int cantRetrasos;

    public Empleado(String nombre, int legajo, boolean asignado, int cantRetrasos){
        this.nombre= nombre;
        this.legajo= legajo;
        this.asignado=asignado;
        this.cantRetrasos= cantRetrasos;
    }

    public void asignar(){
        asignado = true;
    }
    public void desasignar(){
        asignado = false;
    }

    public void registrarRetraso(){
        cantRetrasos++;
    }

    public abstract double calcularCosto(double cantDias);

    public int getLegajo(){return legajo;}

    public String getNombre() {return nombre;}

    public int getCantRetrasos(){return cantRetrasos;}

    public boolean getAsignado(){return asignado;}

    @Override
    public String toString() {return"";}


}
