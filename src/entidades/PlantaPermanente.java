package entidades;

public class PlantaPermanente extends Empleado {
    private double valorDia;
    private String categoria;

    public PlantaPermanente(String nombre, int legajo, boolean asignado,
                            int cantRetrasos, double valorDia, String categoria) {
        super(nombre, legajo, asignado, cantRetrasos);
        this.valorDia = valorDia;
        this.categoria = categoria;
    }

    @Override
    public double calcularCosto(double cantDias){
        return cantDias*valorDia;
    }
}
