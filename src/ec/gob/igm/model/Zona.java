package ec.gob.igm.model;

public class Zona {
    
    private int codZona;
    private String nombre;

    public Zona(int codZona, String nombre) {
        this.codZona = codZona;
        this.nombre = nombre;
    }

    public int getCodZona() { return codZona; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return codZona + " - " + nombre;
    }
}