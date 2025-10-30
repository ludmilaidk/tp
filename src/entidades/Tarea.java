package entidades;

public class Tarea {
    private String nombre;
    private String descripcion;
    private double cantDias;
    private double cantDiasRetrasos;
    private boolean finalizado;
    private Empleado empleadoAsociado;
    private double costoTarea;
    private double costoEstimado;

    public Tarea( String nombre, String descripcion, int cantDias, int cantDiasRetrasos,
                  boolean finalizado, Empleado empleadoAsociado, double costoTarea, double costoEstimado){
        this.nombre=nombre;
        this.descripcion=descripcion;
        this.cantDias=cantDias;
        this.cantDiasRetrasos=cantDiasRetrasos;
        this.finalizado=finalizado;
        this.empleadoAsociado=empleadoAsociado;
        this.costoTarea=costoTarea;
        this.costoEstimado=costoEstimado;
    }

    // Método para asignar un empleado a una tarea
    public void asignarEmpleado(Empleado empleado) {
        this.empleadoAsociado = empleado;

        // Calcular el costo estimado solo cuando se asigna el empleado
        calcularCostoEstimado();
    }

    // Método para calcular el costo estimado (solo se debe calcular cuando se asigna el empleado)
    private void calcularCostoEstimado() {
        if (empleadoAsociado != null) {
            // Calcular el costo estimado basado en los días
            double costoEstimadoBase = empleadoAsociado.calcularCosto(cantDias);

            // Si es PlantaPermanente, aplicamos el 2% adicional
            if (empleadoAsociado instanceof PlantaPermanente) {
                costoEstimadoBase *= 1.02; // Aplicamos el aumento del 2%
            }

            // Guardamos el costo estimado
            this.costoEstimado = costoEstimadoBase;
        }
    }

    public void registrarFinalizado(){
        if (!finalizado) {
            calcularCostoTarea(); //nuestro metodo ya sobreescribe a costoTareaFinalizada
            finalizado = true;
            if (empleadoAsociado != null) {
                empleadoAsociado.asignar(); // libera al empleado

            }
        }
    }
    //actualiza nuestra variable costoTarea al finalizarse una tarea
    public void calcularCostoTarea(){
        if (empleadoAsociado!=null){
            double costo = empleadoAsociado.calcularCosto(duracionTotal());
            costo= costoTarea;
        }
    }

    //solo cuando la tarea este finalizada se llamara a este metodo
    //y entonces evaluara si se debe sumar el extra o no
    public double calcularCostoFinal() {
        if (empleadoAsociado == null) return 0;

        // Solo si NO hubo retrasos se abona el 2%
        if (cantDiasRetrasos == 0 && empleadoAsociado instanceof PlantaPermanente) {
            costoTarea *= 1.02;
        }
        return costoTarea;
    }

    public void registrarRetraso(double dias){
        if (dias > 0) {    //le pasamos los dias de retraso que vamos a registrar y lo valida
            cantDiasRetrasos += dias;
            if (empleadoAsociado != null) {
                empleadoAsociado.registrarRetraso();
            }
        } //esto aun no incrementa la fecha real
    }

    public double duracionTotal(){
        return cantDias + cantDiasRetrasos;
    }



    // Getters
    public String getNombre() { return nombre; }
    public boolean isFinalizado() { return finalizado; }
    public double getCostoTareaFinalizada() { return costoTarea; }
    public Empleado getEmpleadoAsociado() { return empleadoAsociado; }
    public double getCantDiasRetrasos(){ return cantDiasRetrasos;}
    public double getCostoEstimado(){return costoEstimado;}
    // Setters
    public void setEmpleadoAsociado(Empleado empleado) {
        this.empleadoAsociado = empleado;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "nombre='" + nombre + '\'' +
                ", duracionTotal=" + duracionTotal() +
                ", retrasos=" + cantDiasRetrasos +
                ", finalizado=" + finalizado +
                ", costoFinal=" + costoTarea +
                ", costoEstimado=" + costoEstimado +
                '}';
    }


}
