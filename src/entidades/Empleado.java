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
        asignado = !asignado; // alterna entre true y false
    }

    public void registrarRetraso(){
        cantRetrasos++;
    }

    public abstract double calcularCosto(double cantDias);

    public int getLegajo(){
        return this.legajo;
    }
}
