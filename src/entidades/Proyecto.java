package entidades;
import java.time.LocalDate;
import java.util.HashMap;

public class Proyecto {
    private int codigo;
    private Estado estado;
    private String vivienda;
    private HashMap <String, Tarea> tareas;
    private LocalDate fechaInicio;
    private LocalDate fechaEstimada;
    private LocalDate fechaFinalizacion;
    private double valorFinal;
    private int clienteAsociado;

    public Proyecto(int codigo, Estado estado, String vivienda, HashMap<String, Tarea> tareas,
    LocalDate fechaInicio, LocalDate fechaEstimada, LocalDate fechaFinalizacion, double valorFinal, int clienteAsociado){
        this.codigo=codigo;
        this.estado=estado;
        this.vivienda=vivienda;
        this.tareas= tareas;
        this.fechaInicio=fechaInicio;
        this.fechaEstimada=fechaEstimada;
        this.fechaFinalizacion=fechaFinalizacion;
        this.valorFinal=valorFinal;
        this.clienteAsociado=clienteAsociado;
    }

    public double calcularCosto(){
        return 0;
    }
    public void asignarEmpleado(int legajo){

    }
    public void cambiarEstado(){

    }
    public void agregarTarea(String nombreTarea, String descriocion, int cantDias){

    }
    public boolean finalizado(){
        return true;
    }
}
