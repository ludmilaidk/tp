package entidades;

import java.time.LocalDate; //esto lo vamos a usar para las fechas

    private static int nextId = 1; //esto es para que cada tarea tenga un id unico
    private final int idTarea;
    private String titulo;
    private String descripcion;
    private int diasEstimados;
    private String estado;
    private Empleado responsable;
    private int retrasos;

    public Tarea(String titulo, String descripcion, int diasEstimados) {
        if (titulo == null || titulo.trim().isEmpty() || diasEstimados <= 0) {
            throw new IllegalArgumentException("Datos de tarea inválidos.");
        }
        this.idTarea = nextId++;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.diasEstimados = diasEstimados;
        this.estado = Estado.pendiente;
        this.responsable = null;
        this.retrasos = 0;
    }

    // Métodos de Tarea

    public void asignarResponsable(Empleado empleado) {
        if (this.estado.equals(Estado.finalizado)) {
            throw new IllegalStateException("No se puede asignar una tarea finalizada.");
        }
        this.responsable = empleado;
        if (this.estado.equals(Estado.pendiente)) {
             this.estado = Estado.activo; // Una tarea asignada pasa a estar activa
        }
    }

    public void registrarRetraso() {
        if (this.estado.equals(Estado.finalizado)) {
            throw new IllegalStateException("No se puede registrar retraso en tarea finalizada.");
        }
        this.retrasos++;
        if (responsable != null) {
            responsable.incrementarRetrasos();
        }
    }

    public void finalizar() {
        if (!this.estado.equals(Estado.activo)) {
            // Asumo que solo se finaliza una tarea que está ACTIVA.
            // Si está PENDIENTE (no asignada), se lanza excepción.
            throw new IllegalStateException("La tarea no puede finalizarse, su estado es " + this.estado + ".");
        }
        this.estado = Estado.finalizado;
    }

    // Getters
    public int getIdTarea() {
        return idTarea;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getDiasEstimados() {
        return diasEstimados;
    }

    public String getEstado() {
        return estado;
    }

    public Empleado getResponsable() {
        return responsable;
    }

    public int getRetrasos() {
        return retrasos;
    }

    @Override
    public String toString() {
        String resp = responsable != null ? responsable.getNombre() : "Sin asignar";
        return idTarea + " - " + titulo + " (" + diasEstimados + " días, Estado: " + estado + ", Resp: " + resp + ", Retrasos: " + retrasos + ")";
    }
}