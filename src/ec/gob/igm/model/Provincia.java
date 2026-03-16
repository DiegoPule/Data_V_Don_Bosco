package ec.gob.igm.model;

public class Provincia {
    
    private int codProvincia;
    private String nombre;

    public Provincia(int codProvincia, String nombre) {
        this.codProvincia = codProvincia;
        this.nombre = nombre;
    }

    public int getCodProvincia() { return codProvincia; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return codProvincia + " - " + nombre;
    }
}