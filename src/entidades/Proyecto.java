package entidades;
import java.time.LocalDate;
import java.util.HashMap;

public class Proyecto {
    private int codigo;
    private String estado;
    private String vivienda;
    private HashMap <String, Tarea> tareas;
    private LocalDate fechaInicio;
    private LocalDate fechaEstimada;
    private LocalDate fechaFinalizacion;
    private double costoEstimado;
    private double valorFinal;
    private String clienteAsociado;
    private HashMap<Integer, Tarea> historial;
    private boolean huboRetrasos;

    public Proyecto(int codigo, String estado, String vivienda, HashMap<String, Tarea> tareas,
    LocalDate fechaInicio, LocalDate fechaEstimada, LocalDate fechaFinalizacion, double costoFinal, double valorFinal, String clienteAsociado){
        this.codigo=codigo;
        this.estado = Estado.pendiente; //por defecto
        this.vivienda=vivienda;
        this.tareas = (tareas != null) ? tareas : new HashMap<>(); //// Usamos el operador ternario para garantizar que nunca queden atributos nulos. Si el parámetro pasado es distinto de null, se asigna tal cual; Si es null, se asigna un valor por defecto (nuevo HashMap para tareas). Esto evita NullPointerException al manipular estos atributos más adelante.
        this.fechaInicio=fechaInicio;
        this.fechaEstimada=fechaEstimada;
        this.fechaFinalizacion=fechaFinalizacion;
        this.valorFinal=0.0;
        this.costoEstimado=0.0;
        this.clienteAsociado=clienteAsociado;
        this.historial=new HashMap<>();
        this.huboRetrasos=false;
    }

    public void registrarRetrasoTarea(double dias, String nombre){
        Tarea tarea = tareas.get(nombre);

        if (tarea.getEmpleadoAsociado() != null && tarea != null) {
            tarea.registrarRetraso(dias);
            this.huboRetrasos=true;

        } else{
            System.out.println("No existe una tarea con el nombre: " + nombre);
        }
    }

    public boolean asignarEmpleado(String nombreTarea, Empleado empleado){
        //asigna un empleado a una tarea disponible. recordar que el empleado
        // puede ser null si no se consigue un empleado con el criterio requerido
        for (Tarea tarea : tareas.values()) {
            if (tarea.getEmpleadoAsociado() == null && tarea.getNombre().equals(nombreTarea)) {
                tarea.asignarEmpleado(empleado);
                costoEstimado+=tarea.getCostoEstimado();
                empleado.asignar();
                actualizarEstado();
                return true; // empleado asignado exitosamente
            }
        }
        return false; // no había tareas libres

    }

    public void agregarTarea(String nombreTarea, String descripcion, int cantDias){
        if (!estado.equals(Estado.finalizado)) {
            Tarea nueva = new Tarea(nombreTarea, descripcion, cantDias,0, false, null, 0, 0);
            tareas.put(nombreTarea, nueva);

            //al agregar una tarea debemos incrementar la fecha estimada, la fecha real se incrementa al finalizar la tarea
            // 🔹 Actualizar la fecha estimada de finalización
            if (fechaEstimada != null) {
                fechaEstimada = fechaEstimada.plusDays((long)cantDias);
            }

            //cada vez q agregamos una tarea, revisamos si todas tienen empleado
            actualizarEstado();
        }

    }

    // Actualiza el estado del proyecto según las tareas asignadas.
    // ACTIVO → si TODAS las tareas tienen un empleado asignado.
    // PENDIENTE → si hay tareas sin empleado o no hay tareas.
    //este lo usamos al agregar una tarea
    private void actualizarEstado() {
        if (tareas.isEmpty()) {
            estado = Estado.pendiente;
            return;
        } else {
            boolean todasConEmpleado = true;
            for (Tarea t : tareas.values()) {
                if (t.getEmpleadoAsociado() == null) {
                    todasConEmpleado = false;
                    break;
                }
            }
            if (todasConEmpleado) {
                estado = Estado.activo;
            } else {
                estado = Estado.pendiente;
            }
        }
        // Actualizamos la variable huboRetrasos al revisar las tareas
        this.huboRetrasos = tareas.values().stream().anyMatch(t -> t.getCantDiasRetrasos() > 0);
    }
    //este lo usamos al terminar una tarea
    // Actualiza el estado cuando se finaliza una tarea.
    // Si TODAS las tareas están finalizadas → cambia a FINALIZADO.
    private void actualizarEstadoFinalizacion() {
        boolean todasFinalizadas = tareas.values().stream().allMatch(Tarea::isFinalizado);

        if (todasFinalizadas && !tareas.isEmpty()) {
            estado = Estado.finalizado;
            fechaFinalizacion = LocalDate.now();
        }
    }

    //este metodo modifica el estado de una tarea especifica
    //tambien calcula el costo de las tareas finalizadas y las suma continuamente
    //a medida que se finalizan. por lo tanto tenemos el costoFinal en O(1)
    public void establecerTareaFinalizada(String nombreTarea) {
        Tarea tarea = tareas.get(nombreTarea);
        if (tarea != null && !tarea.isFinalizado()) {
            tarea.registrarFinalizado(); //este de aca se encarga de desasignar el empleado
            valorFinal += tarea.calcularCostoFinal(); // O(1)
            historial.put(tarea.getEmpleadoAsociado().getLegajo(), tarea); //guardamos en historial

            //si aun no tengo fechaFinalizacion entonces sumo los dias de la tarea a la fecha de inicio
            //sino lo sumo a la fecha de finalizacion
            if (fechaFinalizacion == null) {
                fechaFinalizacion = fechaInicio.plusDays((long)tarea.duracionTotal());
            } else {
                fechaFinalizacion = fechaFinalizacion.plusDays((long)tarea.duracionTotal());
            }
            actualizarEstadoFinalizacion(); //reevalua si ya se puede poner como finalizado al terminar esta tarea
        }
    }
    public double calcularCostoProyecto() {
        double costoProyecto;

        // Si el proyecto está finalizado, usamos el valor final acumulado
        if (estaFinalizado()) {
            costoProyecto = valorFinal;
        } else {
            // Si no está finalizado, usamos el costo estimado
            costoProyecto = costoEstimado;
        }

        // Aplicamos el porcentaje según si hubo retrasos o no
        if (huboRetrasos) {
            costoProyecto *= 1.25;  // +25% si hubo retrasos
        } else {
            costoProyecto *= 1.35;  // +35% si no hubo retrasos
        }

        return costoProyecto;
    }

    public boolean estaFinalizado(){
        return estado.equals(Estado.finalizado);
    }

    // -------------------- GETTERS Y SETTERS --------------------
    public int getCodigo() { return codigo; }
    public String getEstado() { return estado; }
    public String getVivienda() { return vivienda; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaEstimada() { return fechaEstimada; }
    public LocalDate getFechaFinalizacion() { return fechaFinalizacion; }
    public HashMap<String, Tarea> getTareas() { return tareas; }
    public double getValorFinal() { return valorFinal; }
    public String getClienteAsociado() { return clienteAsociado; }
    public void setFechaFinalizacion(LocalDate fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion;}

}
