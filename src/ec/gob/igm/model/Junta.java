package ec.gob.igm.model;

public class Junta {

    private String provincia;
    private int codProvincia;
    private String canton;
    private int codCanton;
    private String circunscripcion;
    private int codCircunscripcion;
    private String parroquia;
    private int codParroquia;
    private String zona;
    private int codZona;
    private int numJunta;
    private String genero;
    private String codActa;
    private String cod1;
    private String cod2;
    private String nomPriJunta;
    private String nomUltJunta;
    private int numEleJunta;
    private String codActa18;
    private String codActa19;

    public Junta() {}

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public int getCodProvincia() { return codProvincia; }
    public void setCodProvincia(int codProvincia) { this.codProvincia = codProvincia; }

    public String getCanton() { return canton; }
    public void setCanton(String canton) { this.canton = canton; }

    public int getCodCanton() { return codCanton; }
    public void setCodCanton(int codCanton) { this.codCanton = codCanton; }

    public String getCircunscripcion() { return circunscripcion; }
    public void setCircunscripcion(String circunscripcion) { this.circunscripcion = circunscripcion; }

    public int getCodCircunscripcion() { return codCircunscripcion; }
    public void setCodCircunscripcion(int codCircunscripcion) { this.codCircunscripcion = codCircunscripcion; }

    public String getParroquia() { return parroquia; }
    public void setParroquia(String parroquia) { this.parroquia = parroquia; }

    public int getCodParroquia() { return codParroquia; }
    public void setCodParroquia(int codParroquia) { this.codParroquia = codParroquia; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public int getCodZona() { return codZona; }
    public void setCodZona(int codZona) { this.codZona = codZona; }

    public int getNumJunta() { return numJunta; }
    public void setNumJunta(int numJunta) { this.numJunta = numJunta; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getCodActa() { return codActa; }
    public void setCodActa(String codActa) { this.codActa = codActa; }

    public String getCod1() { return cod1; }
    public void setCod1(String cod1) { this.cod1 = cod1; }

    public String getCod2() { return cod2; }
    public void setCod2(String cod2) { this.cod2 = cod2; }

    public String getNomPriJunta() { return nomPriJunta; }
    public void setNomPriJunta(String nomPriJunta) { this.nomPriJunta = nomPriJunta; }

    public String getNomUltJunta() { return nomUltJunta; }
    public void setNomUltJunta(String nomUltJunta) { this.nomUltJunta = nomUltJunta; }

    public int getNumEleJunta() { return numEleJunta; }
    public void setNumEleJunta(int numEleJunta) { this.numEleJunta = numEleJunta; }

    public String getCodActa18() { return codActa18; }
    public void setCodActa18(String codActa18) { this.codActa18 = codActa18; }

    public String getCodActa19() { return codActa19; }
    public void setCodActa19(String codActa19) { this.codActa19 = codActa19; }

    @Override
    public String toString() {
        return "Junta " + numJunta + " - " + genero + " | " + parroquia + " | Zona " + codZona;
    }
}