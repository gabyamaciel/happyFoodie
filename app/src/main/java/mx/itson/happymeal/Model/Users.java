package mx.itson.happymeal.Model;

public class Users {
    private String nombre, email, currentPlace;

    public Users() {}

    public Users(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurrentPlace () { return currentPlace; }

    public void setCurrentPlace (String currentPlace) { this.currentPlace = currentPlace; }
}
