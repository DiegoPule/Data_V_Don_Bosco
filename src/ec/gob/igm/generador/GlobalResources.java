package ec.gob.igm.generador;

import ec.gob.igm.model.Junta;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;


/**
 * Clase de recursos globales del sistema.
 * Lee config.properties al arrancar y pre-construye
 * todas las rutas necesarias para la generación de documentos.
 * 
 * Uso: GlobalResources.getRutaPadron(provincia, canton, parroquia, zona)
 */
public class GlobalResources {

    private static final Properties props = new Properties();

    // Carga el config.properties una sola vez al arrancar
    static {
        try (InputStream input = GlobalResources.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(input);
        } catch (Exception e) {
            System.err.println("Error cargando config.properties: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // LECTURA BÁSICA DE PROPIEDADES
    // ─────────────────────────────────────────

    public static String get(String key) {
        return props.getProperty(key, "");
    }

    // ─────────────────────────────────────────
    // INFORMACIÓN DEL PROCESO
    // ─────────────────────────────────────────

    public static String getNombreProceso() {
        return get("proceso.nombre");
    }

    public static int getCodigoProceso() {
        return Integer.parseInt(get("proceso.codigo"));
    }

    public static int getIdKit() {
        return Integer.parseInt(get("proceso.id_kit"));
    }

    public static String getTipoProceso() {
        return get("proceso.tipo");
    }

    // ─────────────────────────────────────────
    // RUTAS — pre-construidas con jerarquía completa
    // Estructura: BASE\PROVINCIA\CANTON\PARROQUIA\ZONA_N\CARPETA\
    // ─────────────────────────────────────────

    /**
     * Construye la ruta base para una zona específica.
     * Ejemplo: C:\OUTPUT\MORONA SANTIAGO\SEVILLA DON BOSCO\SEVILLA DON BOSCO\ZONA_1\
     */
private static String getRutaZona(String provincia, int codProvincia,
                                   String canton, int codCanton,
                                   String circunscripcion, int codCircunscripcion,
                                   String parroquia, int codParroquia,
                                   String zona, int codZona,
                                   int numJunta,
                                   String genero) {
    return get("output.ruta.base")
            + "/" + codProvincia + "_" + limpiarNombre(provincia)
            + "/" + codCanton + "_" + limpiarNombre(canton)
            + "/" + codCircunscripcion + "_" + limpiarNombre(circunscripcion)
            + "/" + codParroquia + "_" + limpiarNombre(parroquia)
            + "/" + codZona + "_" + limpiarNombre(zona)
            + "/JUNTA_" + String.format("%04d", numJunta)
            + "/" + genero
            + "/";
}

public static String getRutaPadron(Junta junta) {
    return getRutaZona(
            junta.getProvincia(), junta.getCodProvincia(),
            junta.getCanton(), junta.getCodCanton(),
            junta.getCircunscripcion(), junta.getCodCircunscripcion(),
            junta.getParroquia(), junta.getCodParroquia(),
            junta.getZona(), junta.getCodZona(),
            junta.getNumJunta(),
            junta.getGenero())
            + get("output.carpeta.padron") + "/";
}

public static String getRutaActasInstalacion(Junta junta) {
    return getRutaZona(
            junta.getProvincia(), junta.getCodProvincia(),
            junta.getCanton(), junta.getCodCanton(),
            junta.getCircunscripcion(), junta.getCodCircunscripcion(),
            junta.getParroquia(), junta.getCodParroquia(),
            junta.getZona(), junta.getCodZona(),
            junta.getNumJunta(),
            junta.getGenero())
            + get("output.carpeta.actas_instalacion") + "/";
}

public static String getRutaBorradores(Junta junta) {
    return getRutaZona(
            junta.getProvincia(), junta.getCodProvincia(),
            junta.getCanton(), junta.getCodCanton(),
            junta.getCircunscripcion(), junta.getCodCircunscripcion(),
            junta.getParroquia(), junta.getCodParroquia(),
            junta.getZona(), junta.getCodZona(),
            junta.getNumJunta(),
            junta.getGenero())
            + get("output.carpeta.borradores") + "/";
}

public static String getRutaActasEscrutinio(Junta junta) {
    return getRutaZona(
            junta.getProvincia(), junta.getCodProvincia(),
            junta.getCanton(), junta.getCodCanton(),
            junta.getCircunscripcion(), junta.getCodCircunscripcion(),
            junta.getParroquia(), junta.getCodParroquia(),
            junta.getZona(), junta.getCodZona(),
            junta.getNumJunta(),
            junta.getGenero())
            + get("output.carpeta.actas_escrutinio") + "/";
}

public static String getRutaCertificados(Junta junta) {
    return getRutaZona(
            junta.getProvincia(), junta.getCodProvincia(),
            junta.getCanton(), junta.getCodCanton(),
            junta.getCircunscripcion(), junta.getCodCircunscripcion(),
            junta.getParroquia(), junta.getCodParroquia(),
            junta.getZona(), junta.getCodZona(),
            junta.getNumJunta(),
            junta.getGenero())
            + get("output.carpeta.certificados") + "/";
}

public static String getRutaEtiquetas(Junta junta) {
    return getRutaZona(
            junta.getProvincia(), junta.getCodProvincia(),
            junta.getCanton(), junta.getCodCanton(),
            junta.getCircunscripcion(), junta.getCodCircunscripcion(),
            junta.getParroquia(), junta.getCodParroquia(),
            junta.getZona(), junta.getCodZona(),
            junta.getNumJunta(),
            junta.getGenero())
            + get("output.carpeta.etiquetas") + "/";
}

    // ─────────────────────────────────────────
    // DELEGADOS
    // ─────────────────────────────────────────

    public static String getDelegadoIGMJefeProyecto() {
        return get("delegado.igm.jefe_proyecto");
    }

    public static String getDelegadoIGMJefeProduccion() {
        return get("delegado.igm.jefe_produccion");
    }

    public static String getDelegadoIGMEntregaDocumentos() {
        return get("delegado.igm.entrega_documentos");
    }

    public static String getDelegadoIGMEntregaPapeletas() {
        return get("delegado.igm.entrega_papeletas");
    }

    public static String getDelegadoCNEVeedorDocumentos() {
        return get("delegado.cne.veedor_documentos");
    }

    public static String getDelegadoCNEVeedorPapeletas() {
        return get("delegado.cne.veedor_papeletas");
    }

    public static String getDelegadoCNEValidaDocumentos() {
        return get("delegado.cne.valida_documentos");
    }

    public static String getDelegadoIntegradoraRecibe() {
        return get("delegado.integradora.recibe");
    }

    // ─────────────────────────────────────────
    // CONTRATO
    // ─────────────────────────────────────────

    public static String getNumeroContrato() {
        return get("contrato.numero");
    }

    public static String getTituloEtiquetaDocumentos() {
        return get("contrato.titulo");
    }

    // ─────────────────────────────────────────
    // UTILIDAD — limpia nombres para usar en rutas
    // ─────────────────────────────────────────

    /**
     * Elimina caracteres inválidos para nombres de carpetas en Windows.
     * Ejemplo: "MORONA SANTIAGO" → "MORONA SANTIAGO" (sin cambios)
     *          "SEVILLA/DON BOSCO" → "SEVILLA DON BOSCO"
     */
    
    // ─────────────────────────────────────────
// CREACIÓN DE CARPETAS EN DISCO
// ─────────────────────────────────────────

/**
 * Crea todas las carpetas necesarias para una junta específica.
 * Si las carpetas ya existen no hace nada — no sobreescribe.
 * Retorna true si todo salió bien, false si hubo algún error.
 */
public static boolean crearEstructuraCarpetas(Junta junta) {
    String[] rutas = {
        getRutaPadron(junta),
        getRutaActasInstalacion(junta),
        getRutaBorradores(junta),
        getRutaActasEscrutinio(junta),
        getRutaCertificados(junta),
        getRutaEtiquetas(junta)
    };

    boolean todoOk = true;
    for (String ruta : rutas) {
        java.io.File carpeta = new java.io.File(ruta);
        if (!carpeta.exists()) {
            boolean creada = carpeta.mkdirs();
            if (creada) {
                System.out.println("Carpeta creada: " + ruta);
            } else {
                System.err.println("Error al crear: " + ruta);
                todoOk = false;
            }
        } else {
            System.out.println("Ya existe: " + ruta);
        }
    }
    return todoOk;
}

/**
 * Crea las carpetas para TODAS las juntas del proceso.
 * Llámalo una sola vez al iniciar la generación.
 * Retorna cuántas juntas procesó correctamente.
 */
public static int crearEstructuraCompleta(List<Junta> juntas) {
    int exitosas = 0;
    System.out.println("Creando estructura para " + juntas.size() + " juntas...");
    for (Junta junta : juntas) {
        if (crearEstructuraCarpetas(junta)) {
            exitosas++;
        }
    }
    System.out.println("Estructura creada: " + exitosas + "/" + juntas.size() + " juntas.");
    return exitosas;
} 
    
private static String limpiarNombre(String nombre) {
    if (nombre == null || nombre.trim().isEmpty()) {
        return "NINGUNA";
    }
    return nombre
            .replace("/", " ")
            .replace("\\", " ")
            .replace(":", "")
            .replace("*", "")
            .replace("?", "")
            .replace("\"", "")
            .replace("<", "")
            .replace(">", "")
            .replace("|", "")
            .trim();
}

}