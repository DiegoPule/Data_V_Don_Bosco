package ec.gob.igm.model;

public class Candidato {
    
    private String lista;
    private String nombre;
    private String partido;

    public Candidato() {}

    public String getLista() { return lista; }
    public void setLista(String lista) { this.lista = lista; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPartido() { return partido; }
    public void setPartido(String partido) { this.partido = partido; }

    @Override
    public String toString() {
        return lista + " - " + nombre + " (" + partido + ")";
    }
}