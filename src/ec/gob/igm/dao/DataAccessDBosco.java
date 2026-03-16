package ec.gob.igm.dao;

import ec.gob.igm.model.Candidato;
import ec.gob.igm.model.Canton;
import ec.gob.igm.model.Circunscripcion;
import ec.gob.igm.model.Junta;
import ec.gob.igm.model.MJRV;
import ec.gob.igm.model.Parroquia;
import ec.gob.igm.model.Provincia;
import ec.gob.igm.model.Zona;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DataAccessDBosco {

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    // Lista de juntas cargada una sola vez en memoria
    // Toda la jerarquía se filtra desde aquí sin ir a Oracle
    private List<Junta> todasLasJuntas = null;

    // ─────────────────────────────────────────
    // CONFIGURACIÓN
    // ─────────────────────────────────────────
    private String getProperty(String key) {
        Properties props = new Properties();
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(input);
            return props.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ─────────────────────────────────────────
    // CONEXIÓN
    // ─────────────────────────────────────────
    public void connect() {
        try {
            String url = "jdbc:oracle:thin:@//"
                    + getProperty("db.host") + ":1521/"
                    + getProperty("db.service");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(url,
                    getProperty("db.username"),
                    getProperty("db.password"));
            System.out.println("Conexión establecida: "
                    + con.getMetaData().getDatabaseProductName() + " "
                    + con.getMetaData().getDatabaseProductVersion());
        } catch (ClassNotFoundException e) {
            System.err.println("Driver no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    public void disconnect() {
        try { if (rs != null)   rs.close();   } catch (Exception e) {}
        try { if (stmt != null) stmt.close(); } catch (Exception e) {}
        try { if (con != null)  con.close();  } catch (Exception e) {}
    }

    // ─────────────────────────────────────────
    // JUNTAS — carga única en memoria
    // ─────────────────────────────────────────

    /**
     * Carga TODAS las juntas del proceso una sola vez.
     * El resto de métodos de jerarquía filtran desde esta lista
     * sin hacer consultas adicionales a Oracle.
     */
public List<Junta> getJuntas() {
    if (todasLasJuntas != null) {
        return todasLasJuntas;
    }
    connect();
    String sql = "select B.PROVINCIA, J.COD_PROVINCIA, B.CANTON, J.COD_CANTON, "
            + "B.NOM_CIRCUNSCRIPCION, J.COD_CIRCUNSCRIPCION, B.PARROQUIA, J.COD_PARROQUIA, "
            + "B.ZONA, J.COD_ZONA, B.ID_JUNTA, B.SEXO, B.COD_ACTA_JUNTA, J.COD1, J.COD2, "
            + "J.NOMPRI_JUNTA, J.NOMULT_JUNTA, J.NUMELE_JUNTA "
            + "from T_BASE B, T_JUNTA J "
            + "where B.COD_ACTA_JUNTA = J.COD_ACT_JUNTA";
    todasLasJuntas = new ArrayList<>();
    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Junta j = new Junta();
            j.setProvincia(rs.getString("PROVINCIA"));
            j.setCodProvincia(rs.getInt("COD_PROVINCIA"));
            j.setCanton(rs.getString("CANTON"));
            j.setCodCanton(rs.getInt("COD_CANTON"));
            j.setCircunscripcion(rs.getString("NOM_CIRCUNSCRIPCION"));
            j.setCodCircunscripcion(rs.getInt("COD_CIRCUNSCRIPCION"));
            j.setParroquia(rs.getString("PARROQUIA"));
            j.setCodParroquia(rs.getInt("COD_PARROQUIA"));
            j.setZona(rs.getString("ZONA"));
            j.setCodZona(rs.getInt("COD_ZONA"));
            j.setNumJunta(rs.getInt("ID_JUNTA"));
            j.setGenero(rs.getString("SEXO"));
            j.setCodActa(rs.getString("COD_ACTA_JUNTA"));
            j.setCod1(rs.getString("COD1"));
            j.setCod2(rs.getString("COD2"));
            j.setNomPriJunta(rs.getString("NOMPRI_JUNTA"));
            j.setNomUltJunta(rs.getString("NOMULT_JUNTA"));
            j.setNumEleJunta(rs.getInt("NUMELE_JUNTA"));
            // COD_ACT_CONOCIMIENTO18 y 19 son específicas de CNEDONBOSCO25
            // Se cargan aparte cuando se necesiten para los documentos
            todasLasJuntas.add(j);
        }
    } catch (SQLException e) {
        System.err.println("Error en getJuntas: " + e.getMessage());
    } finally {
        disconnect();
    }
    return todasLasJuntas;
}

    // ─────────────────────────────────────────
    // JERARQUÍA GEOGRÁFICA
    // Todos estos métodos filtran desde la lista
    // en memoria — sin consultas adicionales a Oracle
    // ─────────────────────────────────────────

    /**
     * Retorna las provincias únicas del proceso.
     * Si el proceso es cantonal (Sevilla), retorna solo 1.
     * Si el proceso es nacional, retorna las 24.
     */
public List<Provincia> getProvincias() {
    List<Junta> juntas = getJuntas();
    Map<Integer, Provincia> mapa = new LinkedHashMap<>();
    for (Junta j : juntas) {
        if (!mapa.containsKey(j.getCodProvincia())) {
            mapa.put(j.getCodProvincia(),
                    new Provincia(j.getCodProvincia(), j.getProvincia()));
        }
    }
    List<Provincia> lista = new ArrayList<>(mapa.values());
    lista.sort((a, b) -> Integer.compare(a.getCodProvincia(), b.getCodProvincia()));
    return lista;
}

    /**
     * Retorna los cantones de una provincia.
     */
    public List<Canton> getCantonesByProvincia(int codProvincia) {
        List<Junta> juntas = getJuntas();
        Map<Integer, Canton> mapa = new LinkedHashMap<>();
        for (Junta j : juntas) {
            if (j.getCodProvincia() == codProvincia
                    && !mapa.containsKey(j.getCodCanton())) {
                mapa.put(j.getCodCanton(),
                        new Canton(j.getCodCanton(), j.getCanton()));
            }
        }
        return new ArrayList<>(mapa.values());
    }

    /**
     * Retorna las circunscripciones de un cantón.
     */
    public List<Circunscripcion> getCircunscripcionesByCanton(int codCanton) {
        List<Junta> juntas = getJuntas();
        Map<Integer, Circunscripcion> mapa = new LinkedHashMap<>();
        for (Junta j : juntas) {
            if (j.getCodCanton() == codCanton
                    && !mapa.containsKey(j.getCodCircunscripcion())) {
                mapa.put(j.getCodCircunscripcion(),
                        new Circunscripcion(j.getCodCircunscripcion(), j.getCircunscripcion()));
            }
        }
        return new ArrayList<>(mapa.values());
    }

    /**
     * Retorna las parroquias de una circunscripción.
     */
    public List<Parroquia> getParroquiasByCircunscripcion(int codCanton, int codCircunscripcion) {
        List<Junta> juntas = getJuntas();
        Map<Integer, Parroquia> mapa = new LinkedHashMap<>();
        for (Junta j : juntas) {
            if (j.getCodCanton() == codCanton
                    && j.getCodCircunscripcion() == codCircunscripcion
                    && !mapa.containsKey(j.getCodParroquia())) {
                mapa.put(j.getCodParroquia(),
                        new Parroquia(j.getCodParroquia(), j.getParroquia()));
            }
        }
        return new ArrayList<>(mapa.values());
    }

    /**
     * Retorna las zonas de una parroquia.
     */
    public List<Zona> getZonasByParroquia(int codCanton, int codCircunscripcion, int codParroquia) {
        List<Junta> juntas = getJuntas();
        Map<Integer, Zona> mapa = new LinkedHashMap<>();
        for (Junta j : juntas) {
            if (j.getCodCanton() == codCanton
                    && j.getCodCircunscripcion() == codCircunscripcion
                    && j.getCodParroquia() == codParroquia
                    && !mapa.containsKey(j.getCodZona())) {
                mapa.put(j.getCodZona(),
                        new Zona(j.getCodZona(), j.getZona()));
            }
        }
        return new ArrayList<>(mapa.values());
    }

    /**
     * Retorna las juntas de una zona específica.
     * Si está vacío significa que esa zona no tiene juntas en este proceso.
     */
    public List<Junta> getJuntasByZona(int codCanton, int codCircunscripcion,
            int codParroquia, int codZona) {
        List<Junta> juntas = getJuntas();
        List<Junta> resultado = new ArrayList<>();
        for (Junta j : juntas) {
            if (j.getCodCanton() == codCanton
                    && j.getCodCircunscripcion() == codCircunscripcion
                    && j.getCodParroquia() == codParroquia
                    && j.getCodZona() == codZona) {
                resultado.add(j);
            }
        }
        return resultado;
    }

    // ─────────────────────────────────────────
    // DOCUMENTOS
    // ─────────────────────────────────────────

    /**
     * Retorna los documentos del kit indicado en orden de impresión.
     * El idKit viene de config.properties (proceso.id_kit).
     * Kit 1 = Nacional = Sevilla Don Bosco = 21 documentos.
     * Retorna array de 3 elementos: [idListado, descripcion, orden]
     */
    public List<String[]> getDocumentosByKit(int idKit) {
        connect();
        String sql = "SELECT A.ID_LISTADO, B.DESCRIPCION, A.ORDEN_EN_KIT "
                + "FROM T_TIPODOCUMENTO_VS_LISTADO A, T_LISTADO_DOCUMENTOS B "
                + "WHERE A.ID_LISTADO = B.ID_LISTADO "
                + "AND A.ID_KIT = ? "
                + "AND A.TIPOPROCESO IN ('SECCIONAL','AMBOS') "
                + "ORDER BY A.ORDEN_EN_KIT";
        List<String[]> documentos = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idKit);
            rs = ps.executeQuery();
            while (rs.next()) {
                documentos.add(new String[]{
                    rs.getString("ID_LISTADO"),
                    rs.getString("DESCRIPCION"),
                    rs.getString("ORDEN_EN_KIT")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error en getDocumentosByKit: " + e.getMessage());
        } finally {
            disconnect();
        }
        return documentos;
    }

    // ─────────────────────────────────────────
    // DIGNIDADES Y CANDIDATOS
    // ─────────────────────────────────────────

    /**
     * Retorna las dignidades vigentes del proceso activo.
     * Filtra por ESTADO = 'VIGENTE' en T_DIGNIDAD.
     * Retorna array de 3 elementos: [codDignidad, dignidad, nivel]
     * NOTA: el codDignidad de esta tabla NO es igual
     * al COD_DIGNIDAD de T_CANDIDATO.
     */
    public List<String[]> getDignidadesVigentes() {
        connect();
        String sql = "SELECT COD_DIGNIDAD, DIGNIDAD, NIVEL "
                + "FROM T_DIGNIDAD "
                + "WHERE ESTADO = 'VIGENTE' "
                + "ORDER BY ORD_DIGNIDAD";
        List<String[]> dignidades = new ArrayList<>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                dignidades.add(new String[]{
                    rs.getString("COD_DIGNIDAD"),
                    rs.getString("DIGNIDAD"),
                    rs.getString("NIVEL")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error en getDignidadesVigentes: " + e.getMessage());
        } finally {
            disconnect();
        }
        return dignidades;
    }

    /**
     * Retorna los candidatos de una dignidad específica.
     * NOTA: usar el COD_DIGNIDAD de T_CANDIDATO, no de T_DIGNIDAD.
     * Para Sevilla Don Bosco: Alcalde=18, Concejales=19.
     */
    public List<Candidato> getCandidatosByDignidad(int codDignidad) {
        connect();
        String sql = "SELECT LIS_PARTIDO AS LISTA, "
                + "NOMB_PARTIDO AS PARTIDO, "
                + "NOM_PAPELETA AS NOMBRE "
                + "FROM T_CANDIDATO "
                + "WHERE COD_DIGNIDAD = ? "
                + "ORDER BY ORD_CANDIDATO";
        List<Candidato> candidatos = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codDignidad);
            rs = ps.executeQuery();
            while (rs.next()) {
                Candidato c = new Candidato();
                c.setLista(rs.getString("LISTA"));
                c.setNombre(rs.getString("NOMBRE"));
                c.setPartido(rs.getString("PARTIDO"));
                candidatos.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error en getCandidatosByDignidad: " + e.getMessage());
        } finally {
            disconnect();
        }
        return candidatos;
    }

    // ─────────────────────────────────────────
    // MIEMBROS JRV
    // ─────────────────────────────────────────

    /**
     * Retorna los miembros JRV de una junta específica.
     * Usa PreparedStatement para evitar SQL Injection.
     */
    public List<MJRV> getMJRV(int codZona, String sexo, int idJunta) {
        connect();
        String sql = "select NUM_CED||DIG_COMPR_CED, APELLIDOS_NOMBRES, "
                + "ASIGNACION, COD_MJRV from T_MJRV "
                + "where COD_ZONA=? AND GENERO=? AND NUM_JUNTA=? "
                + "order by orden";
        List<MJRV> miembros = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codZona);
            ps.setString(2, sexo);
            ps.setInt(3, idJunta);
            rs = ps.executeQuery();
            while (rs.next()) {
                MJRV m = new MJRV();
                m.setNumCed(rs.getString("NUM_CED||DIG_COMPR_CED"));
                m.setApellidosNombres(rs.getString("APELLIDOS_NOMBRES"));
                m.setTipAsignacion(rs.getString("ASIGNACION"));
                m.setCodAsignacion(rs.getInt("COD_MJRV"));
                miembros.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error en getMJRV: " + e.getMessage());
        } finally {
            disconnect();
        }
        return miembros;
    }
}
