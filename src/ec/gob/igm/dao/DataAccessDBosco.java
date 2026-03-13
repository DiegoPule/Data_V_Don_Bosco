//Autor Diego Pule
package ec.gob.igm.dao;

import ec.gob.igm.model.Candidato;
import ec.gob.igm.model.Junta;
import ec.gob.igm.model.MJRV;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DataAccessDBosco {

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    // Lee las credenciales desde config.properties
    private String getConnectionUrl() {
        Properties props = new Properties();
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(input);
            String host    = props.getProperty("db.host");
            String service = props.getProperty("db.service");
            return "jdbc:oracle:thin:@//" + host + ":1521/" + service;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getUsername() {
        Properties props = new Properties();
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(input);
            return props.getProperty("db.username");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getPassword() {
        Properties props = new Properties();
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(input);
            return props.getProperty("db.password");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void connect() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(getConnectionUrl(), getUsername(), getPassword());
            System.out.println("Conexión establecida: " 
                + con.getMetaData().getDatabaseProductName() + " " 
                + con.getMetaData().getDatabaseProductVersion());
        } catch (ClassNotFoundException e) {
            System.err.println("Driver Oracle no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    public void disconnect() {
        try { if (rs != null)   rs.close();   } catch (Exception e) {}
        try { if (stmt != null) stmt.close(); } catch (Exception e) {}
        try { if (con != null)  con.close();  } catch (Exception e) {}
    }

    /**
     * Retorna todas las juntas del proceso Sevilla Don Bosco.
     * Se eliminó el filtro cod_zona=21 que era solo para pruebas.
     */
    public List<Junta> getJuntas() {
        connect();
        String sqlQuery = "select B.PROVINCIA, J.COD_PROVINCIA, B.CANTON, J.COD_CANTON, "
                + "B.NOM_CIRCUNSCRIPCION, J.COD_CIRCUNSCRIPCION, B.PARROQUIA, J.COD_PARROQUIA, "
                + "B.ZONA, J.COD_ZONA, B.ID_JUNTA, B.SEXO, B.COD_ACTA_JUNTA, J.COD1, J.COD2, "
                + "J.NOMPRI_JUNTA, J.NOMULT_JUNTA, J.NUMELE_JUNTA, "
                + "J.COD_ACT_CONOCIMIENTO18, J.COD_ACT_CONOCIMIENTO19 "
                + "from T_BASE B, T_JUNTA J "
                + "where B.COD_ACTA_JUNTA = J.COD_ACT_JUNTA";
        List<Junta> juntas = new ArrayList<>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            while (rs.next()) {
                Junta junta = new Junta();
                junta.setProvincia(rs.getString("PROVINCIA"));
                junta.setCodProvincia(rs.getInt("COD_PROVINCIA"));
                junta.setCanton(rs.getString("CANTON"));
                junta.setCodCanton(rs.getInt("COD_CANTON"));
                junta.setCircunscripcion(rs.getString("NOM_CIRCUNSCRIPCION"));
                junta.setCodCircunscripcion(rs.getInt("COD_CIRCUNSCRIPCION"));
                junta.setParroquia(rs.getString("PARROQUIA"));
                junta.setCodParroquia(rs.getInt("COD_PARROQUIA"));
                junta.setZona(rs.getString("ZONA"));
                junta.setCodZona(rs.getInt("COD_ZONA"));
                junta.setNumJunta(rs.getInt("ID_JUNTA"));
                junta.setGenero(rs.getString("SEXO"));
                junta.setCodActa(rs.getString("COD_ACTA_JUNTA"));
                junta.setCod1(rs.getString("COD1"));
                junta.setCod2(rs.getString("COD2"));
                junta.setNomPriJunta(rs.getString("NOMPRI_JUNTA"));
                junta.setNomUltJunta(rs.getString("NOMULT_JUNTA"));
                junta.setNumEleJunta(rs.getInt("NUMELE_JUNTA"));
                junta.setCodActa18(rs.getString("COD_ACT_CONOCIMIENTO18"));
                junta.setCodActa19(rs.getString("COD_ACT_CONOCIMIENTO19"));
                juntas.add(junta);
            }
        } catch (SQLException e) {
            System.err.println("Error en getJuntas: " + e.getMessage());
        } finally {
            disconnect();
        }
        return juntas;
    }

    /**
     * Retorna los candidatos según la dignidad:
     * COD_DIGNIDAD=18 → Alcalde
     * COD_DIGNIDAD=19 → Concejales
     */
    public List<Candidato> getCandidatos(String dignidad) {
        connect();
        String sqlQuery;
        if (dignidad.toUpperCase().contains("ALCALDE")) {
            sqlQuery = "select LIS_PARTIDO AS LISTA, NOMB_PARTIDO AS PARTIDO, "
                    + "NOM_PAPELETA AS NOMBRE "
                    + "from T_CANDIDATO "
                    + "where COD_DIGNIDAD=18 "
                    + "order by ORD_CANDIDATO";
        } else {
            sqlQuery = "select MIN(LIS_PARTIDO) AS LISTA, MIN(NOMB_PARTIDO) AS PARTIDO, "
                    + "MIN(NOM_PAPELETA) AS NOMBRE "
                    + "from T_CANDIDATO "
                    + "where COD_DIGNIDAD=19 "
                    + "group by LIS_PARTIDO";
        }
        List<Candidato> candidatos = new ArrayList<>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            while (rs.next()) {
                Candidato candidato = new Candidato();
                candidato.setLista(rs.getString("LISTA"));
                candidato.setNombre(rs.getString("NOMBRE"));
                candidato.setPartido(rs.getString("PARTIDO"));
                candidatos.add(candidato);
            }
        } catch (SQLException e) {
            System.err.println("Error en getCandidatos: " + e.getMessage());
        } finally {
            disconnect();
        }
        return candidatos;
    }

    /**
     * Retorna los miembros JRV de una junta específica.
     * Usa PreparedStatement para evitar SQL Injection.
     */
    public List<MJRV> getMJRV(int codZona, String sexo, int idJunta) {
        connect();
        String sqlQuery = "select NUM_CED||DIG_COMPR_CED, APELLIDOS_NOMBRES, "
                + "ASIGNACION, COD_MJRV "
                + "from T_MJRV "
                + "where COD_ZONA=? AND GENERO=? AND NUM_JUNTA=? "
                + "order by orden";
        List<MJRV> miembros = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, codZona);
            ps.setString(2, sexo);
            ps.setInt(3, idJunta);
            rs = ps.executeQuery();
            while (rs.next()) {
                MJRV miembro = new MJRV();
                miembro.setNumCed(rs.getString("NUM_CED||DIG_COMPR_CED"));
                miembro.setApellidosNombres(rs.getString("APELLIDOS_NOMBRES"));
                miembro.setTipAsignacion(rs.getString("ASIGNACION"));
                miembro.setCodAsignacion(rs.getInt("COD_MJRV"));
                miembros.add(miembro);
            }
        } catch (SQLException e) {
            System.err.println("Error en getMJRV: " + e.getMessage());
        } finally {
            disconnect();
        }
        return miembros;
    }
}