package ec.gob.igm.model;

public class MJRV {
    
    private String numCed;
    private String apellidosNombres;
    private String tipAsignacion;
    private int codAsignacion;

    public MJRV() {}

    public String getNumCed() { return numCed; }
    public void setNumCed(String numCed) { this.numCed = numCed; }

    public String getApellidosNombres() { return apellidosNombres; }
    public void setApellidosNombres(String apellidosNombres) { 
        this.apellidosNombres = apellidosNombres; 
    }

    public String getTipAsignacion() { return tipAsignacion; }
    public void setTipAsignacion(String tipAsignacion) { 
        this.tipAsignacion = tipAsignacion; 
    }

    public int getCodAsignacion() { return codAsignacion; }
    public void setCodAsignacion(int codAsignacion) { 
        this.codAsignacion = codAsignacion; 
    }

    @Override
    public String toString() {
        return numCed + " - " + apellidosNombres + " [" + tipAsignacion + "]";
    }
}