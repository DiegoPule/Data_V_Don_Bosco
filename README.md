# GeneradorSDBosco

Sistema de generación de documentos electorales para el proceso **Elección de Autoridades del Cantón Sevilla Don Bosco** — 17 de agosto de 2025.

Desarrollado por el **Instituto Geográfico Militar (IGM)** — Dirección de Tecnologías de la Información y Comunicación (DTIC).

---

## Tabla de contenidos

1. [Descripción general](#descripción-general)
2. [Arquitectura del proyecto](#arquitectura-del-proyecto)
3. [Requisitos](#requisitos)
4. [Configuración](#configuración)
5. [Base de datos](#base-de-datos)
6. [Documentos electorales](#documentos-electorales)
7. [Estructura de carpetas de salida](#estructura-de-carpetas-de-salida)
8. [Flujo de generación](#flujo-de-generación)
9. [Clases principales](#clases-principales)
10. [Convenciones de código](#convenciones-de-código)
11. [Historial de versiones](#historial-de-versiones)

---

## Descripción general

`GeneradorSDBosco` es una aplicación de escritorio Java que se conecta a una base de datos Oracle institucional y genera en formato PDF los 17 tipos de documentos electorales requeridos por contrato para el proceso electoral de Sevilla Don Bosco 2025.

Cada documento se genera por **Junta Receptora del Voto (JRV)**, organizando la salida en una jerarquía de carpetas geográfica que permite al operador imprimir, auditar y redistribuir los documentos de forma ordenada.

---

## Arquitectura del proyecto

```
GeneradorSDBosco/
├── src/
│   └── main/
│       ├── java/
│       │   ├── ec.gob.igm.dao/
│       │   │   ├── DataAccessDBosco.java     ← Acceso a Oracle (CNEDONBOSCO25)
│       │   │   └── DocumentoEnum.java        ← Catálogo de los 17 documentos
│       │   ├── ec.gob.igm.model/
│       │   │   ├── Junta.java                ← Modelo de Junta Receptora del Voto
│       │   │   ├── Candidato.java            ← Modelo de candidato electoral
│       │   │   └── MJRV.java                 ← Modelo de Miembro de JRV
│       │   ├── ec.gob.igm.generador/
│       │   │   ├── PadronBuilder.java        ← Genera padrón electoral
│       │   │   ├── ActaInstalacion.java      ← Genera actas de instalación
│       │   │   ├── ActaEscrutinio.java       ← Genera actas de escrutinio
│       │   │   ├── ActaEscCPublico.java      ← Genera actas conocimiento público
│       │   │   ├── BorradorEsc.java          ← Genera borradores de escrutinio
│       │   │   ├── ListadoMateriales.java    ← Genera listado de materiales
│       │   │   ├── Identificativo.java       ← Genera identificativos de JRV
│       │   │   ├── EtiquetaKit.java          ← Genera etiquetas de kit
│       │   │   ├── EtiquetaPaquetes.java     ← Genera etiquetas papeletas/documentos
│       │   │   └── Sobres.java               ← Genera sobres electorales
│       │   ├── ec.gob.igm.util/
│       │   │   ├── GlobalResources.java      ← Lee rutas y constantes de config.properties
│       │   │   ├── CNEFileUtils.java         ← Construye rutas de archivos de salida
│       │   │   └── CodigoUtil.java           ← Genera códigos de barras y QR
│       │   └── ec.gob.igm.vista/
│       │       └── MainFrame.java            ← Interfaz gráfica principal (Swing)
│       └── resources/
│           └── config.properties             ← Configuración de BD y rutas (NO subir a Git)
├── docs/
│   ├── arquitectura.md
│   ├── documentos_electorales.md
│   └── base_de_datos.md
└── README.md
```

### Principio de diseño

> **Una sola fuente de verdad para cada responsabilidad.**
> - Rutas y constantes → `config.properties` + `GlobalResources`
> - Catálogo de documentos → `DocumentoEnum`
> - Construcción de rutas de salida → `CNEFileUtils`
> - Acceso a datos → `DataAccessDBosco`

---

## Requisitos

| Componente | Versión |
|---|---|
| Java JDK | 17 (LTS) |
| NetBeans | 21 |
| Oracle JDBC Driver | `ojdbc6.jar` |
| JasperReports | 6.x |
| Base de datos | Oracle 11g Release 11.2.0.4.0 — 64bit |

---

## Configuración

Toda la configuración del sistema vive en el archivo `src/main/resources/config.properties`.

```properties
# ── Base de datos ──────────────────────────────────────────
db.host=192.168.1.80
db.service=igm1
db.username=CNEDONBOSCO25
db.password=CNEDONBOSCO25

# ── Rutas del proceso ──────────────────────────────────────
app.output.folder=C:\\DATA VARIABLE\\DON_BOSCO\\OUTPUT
app.jaspers.path=C:\\DATA VARIABLE\\DON_BOSCO\\JASPER
app.backgrounds.path=C:\\DATA VARIABLE\\DON_BOSCO\\FONDOS\\01 GENERAL URBANO
app.fotos.path=C:\\DATA VARIABLE\\DON_BOSCO\\IMG_PADRON

# ── Datos del proceso electoral ────────────────────────────
app.codigo.proceso=134
app.fecha.proceso=17 DE AGOSTO DE 2025
app.nombre.proceso=Elección de Autoridades del Cantón Sevilla Don Bosco
```

> ⚠️ **IMPORTANTE:** `config.properties` contiene credenciales de base de datos.
> **No subir este archivo a repositorios públicos.**
> Agregar `config.properties` al `.gitignore`.

---

## Base de datos

- **Servidor:** `192.168.1.80:1521`
- **Servicio:** `igm1`
- **Esquema:** `CNEDONBOSCO25`

### Tablas principales

| Tabla | Descripción |
|---|---|
| `T_BASE` | Tabla base con la estructura geográfica electoral |
| `T_JUNTA` | Juntas Receptoras del Voto con códigos de acta y seguridad |
| `T_MJRV` | Miembros asignados a cada JRV (vocales, secretario) |
| `T_CANDIDATO` | Candidatos por dignidad (Alcalde cod=18, Concejales cod=19) |

### Relación principal

```sql
T_BASE.COD_ACTA_JUNTA = T_JUNTA.COD_ACT_JUNTA
```

### Estructura geográfica electoral

```
Provincia (14 - MORONA SANTIAGO)
  └── Cantón (925 - SAN JUAN BOSCO)
        └── Circunscripción
              └── Parroquia (6445 - SAN JUAN BOSCO)
                    └── Zona
                          └── JRV (Junta + Género M/F)
```

### Dignidades electorales

| Código | Dignidad |
|---|---|
| 18 | Alcalde |
| 19 | Concejales |

---

## Documentos electorales

El contrato define 17 tipos de documentos. Cada uno está catalogado en `DocumentoEnum`:

| # | Nombre | Carpeta de salida | Por JRV |
|---|---|---|---|
| 1 | Listado de Materiales | `01_LISTADO_DE_MATERIALES` | ✅ |
| 2 | Padrón Electoral | `02_PADRON_ELECTORAL` | ✅ |
| 3 | Acta de Instalación P1 | `03_ACTA_INSTALACION_P1` | ✅ |
| 4 | Acta de Instalación T1 | `04_ACTA_INSTALACION_T1` | ✅ |
| 5 | Acta de Instalación Conocimiento Público | `05_ACTA_INSTALACION_CONOCIMIENTO_PUBLICO` | ✅ |
| 6 | Borrador de Escrutinio Alcaldes | `06_BORRADOR_ESCRUTINIO_ALCALDES` | ✅ |
| 7 | Borrador de Escrutinio Concejales | `07_BORRADOR_ESCRUTINIO_CONCEJALES` | ✅ |
| 8 | Acta de Escrutinio T1 Alcaldes | `08_ACTA_ESCRUTINIO_T1_ALCALDES` | ✅ |
| 9 | Acta de Escrutinio P1 Alcaldes | `09_ACTA_ESCRUTINIO_P1_ALCALDES` | ✅ |
| 10 | Acta de Escrutinio T2 Concejales | `10_ACTA_ESCRUTINIO_T2_CONCEJALES` | ✅ |
| 11 | Acta de Escrutinio P1 Concejales | `11_ACTA_ESCRUTINIO_P1_CONCEJALES` | ✅ |
| 12 | Acta CP Alcaldes ej1 | `12_ACTA_CP_ALCALDES` | ✅ |
| 12 | Acta CP Alcaldes ej2 | `12_ACTA_CP_ALCALDES` | ✅ (misma carpeta) |
| 13 | Acta CP Concejales ej1 | `13_ACTA_CP_CONCEJALES` | ✅ |
| 13 | Acta CP Concejales ej2 | `13_ACTA_CP_CONCEJALES` | ✅ (misma carpeta) |
| 14 | Sobres | `14_SOBRES` | ✅ |
| 15 | Identificativos | `15_IDENTIFICATIVOS` | ✅ |
| 16 | Etiquetas Kit | `16_ETIQUETAS_KIT` | ✅ |
| 17 | Etiq. Documentos-Papeletas | `17_ETIQ_DOCUMENTOS_PAPELETAS` | ✅ |

> **Nota sobre códigos 12 y 13:** Tienen dos ejemplares (ej1 y ej2) que comparten
> el mismo código numérico y la misma carpeta de salida, pero generan archivos
> con nombres distintos. Esto es intencional según el contrato.

---

## Estructura de carpetas de salida

La jerarquía de salida sigue el orden: **territorio → tipo de documento → archivo**.

```
C:\DATA VARIABLE\DON_BOSCO\OUTPUT\
  └── MORONA SANTIAGO\
        └── SAN JUAN BOSCO\
              └── SAN JUAN BOSCO\          ← parroquia
                    └── ZONA_1\
                          ├── 02_PADRON_ELECTORAL\
                          │     └── Padron_ZONA_1_0001F.pdf
                          ├── 03_ACTA_INSTALACION_P1\
                          │     └── Acta_de_Instalacion_P1_ZONA_1_0001F.pdf
                          ├── 08_ACTA_ESCRUTINIO_T1_ALCALDES\
                          │     └── Acta_de_Escrutinio_T1_Alcaldes_ZONA_1_0001F.pdf
                          └── ...
```

### Regla de nombrado de archivos

```
{NombreDocumento}_{Zona}_{NumJunta}{Genero}.pdf

Ejemplo:
  Acta_de_Escrutinio_P1_Alcaldes_ZONA_1_0001F.pdf
  Padron_Electoral_ZONA_1_0002M.pdf
```

---

## Flujo de generación

```
Usuario selecciona parámetros en MainFrame
  (Provincia → Cantón → Parroquia → Zona → documentos a generar)
          ↓
DataAccessDBosco consulta Oracle
  → getJuntas()      Lista de JRV de la zona seleccionada
  → getCandidatos()  Lista de candidatos por dignidad
  → getMJRV()        Miembros asignados a cada JRV
          ↓
Para cada JRV y cada documento seleccionado:
  GlobalResources.getJaspersPath() → ruta del template .jrxml
  CNEFileUtils.createFilePathISSFA(DocumentoEnum, ...) → ruta de salida
          ↓
JasperReports compila .jrxml → .jasper
JasperReports llena parámetros + datos
JasperReports exporta → .pdf en la ruta de salida
          ↓
Sistema reporta progreso en consola / barra de progreso
```

---

## Clases principales

### `DataAccessDBosco`
Única clase de acceso a la base de datos Oracle del esquema `CNEDONBOSCO25`.
Lee credenciales y configuración desde `config.properties`.
Usa `PreparedStatement` para prevenir SQL Injection.

### `DocumentoEnum`
Catálogo completo de los 17 documentos del contrato.
Cada entrada define: código numérico, nombre descriptivo, carpeta de salida y alias.

### `GlobalResources`
Lee todas las rutas y constantes del proceso desde `config.properties`.
Punto único de configuración — ninguna otra clase tiene rutas hardcodeadas.

### `CNEFileUtils`
Construye la ruta completa de cada archivo de salida.
Recibe un `DocumentoEnum` y los datos geográficos de la JRV.
Crea automáticamente las carpetas si no existen.

### `CodigoUtil`
Genera los códigos de barras y códigos QR que se imprimen en cada documento.
El código de barras combina: `codActa + codTipoDoc + codigoProceso + codSeguridad`.

---

## Convenciones de código

- **Sin rutas hardcodeadas** — todo sale de `config.properties` vía `GlobalResources`.
- **Sin credenciales en código fuente** — solo en `config.properties`.
- **Un `DocumentoEnum` por documento** — no Strings sueltos para identificar tipos.
- **`CNEFileUtils` para toda ruta de salida** — ninguna clase construye rutas por su cuenta.
- **`PreparedStatement` para toda consulta con parámetros** — nunca concatenar SQL.
- **Javadoc en todos los métodos públicos** — especialmente en `DataAccessDBosco` y generadores.
- **`connect()` / `disconnect()` siempre en bloque `try/finally`** — garantiza cierre de conexión.

---

## Historial de versiones

| Versión | Fecha | Descripción |
|---|---|---|
| 1.0.0 | 2025-07 | Esqueleto inicial — conexión Oracle, modelos base, MainFrame de prueba |

---

## Autor

**Diego Oswaldo Pulé López**
Analista de Soporte en Tecnología Geoinformática (SP5)
Dirección de Tecnologías de la Información y Comunicación — DTIC
Instituto Geográfico Militar — IGM
