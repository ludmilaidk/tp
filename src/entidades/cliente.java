package entidades;

public class Cliente {
    private String nombre;
    private String telefono;
    private String email;

    public Cliente(String nombre, String telefono, String email){
        this.nombre=nombre;
        this.telefono=telefono;
        this.email=email;
    }
    @Override
    public String toString() {
        return "Cliente{" +
                "nombre=" + nombre +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
