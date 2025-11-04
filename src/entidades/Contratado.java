package entidades;

public class Contratado extends Empleado {
    private double valorHora;
    public Contratado(String nombre, int legajo, boolean asignado,
                      int cantRetrasos, double valorHora){
        super(nombre, legajo, asignado, cantRetrasos);
        this.valorHora= valorHora;

    }
    @Override
    public double calcularCosto(double cantDias){
        return cantDias* valorHora * 8; //consideramos que trabaja al menos 8hs por dia

    }
    @Override
    public String toString() {
        return String.valueOf(getLegajo());
    }


}
