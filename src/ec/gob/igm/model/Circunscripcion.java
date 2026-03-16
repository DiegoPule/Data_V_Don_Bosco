package ec.gob.igm.model;

public class Circunscripcion {
    
    private int codCircunscripcion;
    private String nombre;

    public Circunscripcion(int codCircunscripcion, String nombre) {
        this.codCircunscripcion = codCircunscripcion;
        this.nombre = nombre;
    }

    public int getCodCircunscripcion() { return codCircunscripcion; }
    public String getNombre() { return nombre; }

@Override
public String toString() {
    if (nombre == null || nombre.trim().isEmpty()) {
        return codCircunscripcion + " - NINGUNA";
    }
    return codCircunscripcion + " - " + nombre;
}
}