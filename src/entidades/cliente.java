package entidades;

public class Cliente {
    private String nombre;
    private String mail;
    private String telefono;

    public Cliente(String nombre, String mail, String telefono) {
        if (nombre == null || nombre.trim().isEmpty()) { // trim elimina los espacios en blanco si es que los hay
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        this.nombre = nombre;
        this.mail = mail;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMail() {
        return mail;
    }

    public String getTelefono() {
        return telefono;
    }

    @Override
    public String toString() { // toString devuelve una cadena de texto 
        return "Cliente: " + nombre + " (Mail: " + mail + ", Teléfono: " + telefono + ")";
    }
}