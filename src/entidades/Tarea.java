package entidades;

public class Tarea {
    private String nombre;
    private String descripcion;
    private int cantDias;
    private int cantDiasRetrasos;
    private boolean finalizado;
    private Empleado empleadoAsociado;
    private double costoTareaFinalizada;

    public Tarea( String nombre, String descripcion, int cantDias, int cantDiasRetrasos,boolean finalizado, Empleado empleadoAsociado, double costoTareaFinalizada){
        this.nombre=nombre;
        this.descripcion=descripcion;
        this.cantDias=cantDias;
        this.cantDiasRetrasos=cantDiasRetrasos;
        this.finalizado=finalizado;
        this.empleadoAsociado=empleadoAsociado;
        this.costoTareaFinalizada=costoTareaFinalizada;
    }
    public void registrarFinalizado(){
    }

    public void registrarRetraso(int cantDias){
    }

    public double duracionTotal(){
        return cantDias + cantDiasRetrasos;
    }

    public double calcularCosto(){
        return empleadoAsociado.calcularCosto(duracionTotal());
    }
}
