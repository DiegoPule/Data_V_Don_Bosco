package ec.gob.igm.model;

public class Canton {
    
    private int codCanton;
    private String nombre;

    public Canton(int codCanton, String nombre) {
        this.codCanton = codCanton;
        this.nombre = nombre;
    }

    public int getCodCanton() { return codCanton; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return codCanton + " - " + nombre;
    }
}