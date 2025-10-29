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

    }

    public void registrarRetraso(){

    }

    public abstract double calcularCosto(double cantDias);

}
