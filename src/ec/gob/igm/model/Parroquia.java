package ec.gob.igm.model;

public class Parroquia {
    
    private int codParroquia;
    private String nombre;

    public Parroquia(int codParroquia, String nombre) {
        this.codParroquia = codParroquia;
        this.nombre = nombre;
    }

    public int getCodParroquia() { return codParroquia; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return codParroquia + " - " + nombre;
    }
}