package ec.gob.igm.vista;

import ec.gob.igm.dao.DataAccessDBosco;
import ec.gob.igm.model.Canton;
import ec.gob.igm.model.Circunscripcion;
import ec.gob.igm.model.Parroquia;
import ec.gob.igm.model.Provincia;
import ec.gob.igm.model.Zona;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;

public class MainFrame extends javax.swing.JFrame {

    private final DataAccessDBosco dao = new DataAccessDBosco();

    public MainFrame() {
        initComponents();
        cargarProvincias();
        cargarDocumentos();
    }

    // ─────────────────────────────────────────
    // CARGA DE JERARQUÍA
    // ─────────────────────────────────────────

    private void cargarProvincias() {
        lblEstado.setText("Cargando datos...");
        List<Provincia> provincias = dao.getProvincias();
        DefaultComboBoxModel<Provincia> model = new DefaultComboBoxModel<>();
        for (Provincia p : provincias) {
            model.addElement(p);
        }
        cboProvincia.setModel(model);
        cargarCantones();
        lblEstado.setText("Listo.");
    }

    private void cargarCantones() {
        Provincia provincia = (Provincia) cboProvincia.getSelectedItem();
        if (provincia == null) return;

        List<Canton> cantones = dao.getCantonesByProvincia(provincia.getCodProvincia());
        DefaultComboBoxModel<Canton> model = new DefaultComboBoxModel<>();
        for (Canton c : cantones) {
            model.addElement(c);
        }
        cboCanton.setModel(model);
        cargarCircunscripciones();
    }

    private void cargarCircunscripciones() {
        Canton canton = (Canton) cboCanton.getSelectedItem();
        if (canton == null) return;

        List<Circunscripcion> circs = dao.getCircunscripcionesByCanton(canton.getCodCanton());
        DefaultComboBoxModel<Circunscripcion> model = new DefaultComboBoxModel<>();
        for (Circunscripcion ci : circs) {
            model.addElement(ci);
        }
        cboCircunscripcion.setModel(model);
        cargarParroquias();
    }

    private void cargarParroquias() {
        Canton canton = (Canton) cboCanton.getSelectedItem();
        Circunscripcion circ = (Circunscripcion) cboCircunscripcion.getSelectedItem();
        if (canton == null || circ == null) return;

        List<Parroquia> parroquias = dao.getParroquiasByCircunscripcion(
                canton.getCodCanton(), circ.getCodCircunscripcion());
        DefaultComboBoxModel<Parroquia> model = new DefaultComboBoxModel<>();
        for (Parroquia p : parroquias) {
            model.addElement(p);
        }
        cboParroquia.setModel(model);
        cargarZonas();
    }

    private void cargarZonas() {
        Canton canton = (Canton) cboCanton.getSelectedItem();
        Circunscripcion circ = (Circunscripcion) cboCircunscripcion.getSelectedItem();
        Parroquia parroquia = (Parroquia) cboParroquia.getSelectedItem();
        if (canton == null || circ == null || parroquia == null) return;

        List<Zona> zonas = dao.getZonasByParroquia(
                canton.getCodCanton(),
                circ.getCodCircunscripcion(),
                parroquia.getCodParroquia());
        DefaultComboBoxModel<Zona> model = new DefaultComboBoxModel<>();
        for (Zona z : zonas) {
            model.addElement(z);
        }
        cboZona.setModel(model);

        if (zonas.isEmpty()) {
            lblEstado.setText("⚠ Esta zona no tiene juntas en este proceso.");
        } else {
            lblEstado.setText("Zonas cargadas: " + zonas.size());
        }
    }

    private void cargarDocumentos() {
        int idKit = 1;
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader()
                    .getResourceAsStream("config.properties");
            props.load(input);
            idKit = Integer.parseInt(props.getProperty("proceso.id_kit", "1"));
        } catch (Exception e) {
            System.err.println("Error leyendo config: " + e.getMessage());
        }

        List<String[]> documentos = dao.getDocumentosByKit(idKit);
        listDocumentos.setModel(new javax.swing.AbstractListModel<String>() {
            @Override public int getSize() { return documentos.size(); }
            @Override public String getElementAt(int i) {
                return documentos.get(i)[2] + ". " + documentos.get(i)[1];
            }
        });
    }

    // ─────────────────────────────────────────
    // ACCIÓN GENERAR
    // ─────────────────────────────────────────

    private void generar() {
        Zona zona = (Zona) cboZona.getSelectedItem();
        if (zona == null) {
            lblEstado.setText("⚠ Seleccione una zona.");
            return;
        }
        if (listDocumentos.getSelectedIndices().length == 0) {
            lblEstado.setText("⚠ Seleccione al menos un documento.");
            return;
        }
        lblEstado.setText("Generando documentos para " + zona + "...");
        // Aquí irá la lógica de generación con JasperReports
    }

    // ─────────────────────────────────────────
    // INTERFAZ GRÁFICA — generada por Matisse
    // ─────────────────────────────────────────

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        lblProvincia        = new javax.swing.JLabel();
        lblCanton           = new javax.swing.JLabel();
        lblCircunscripcion  = new javax.swing.JLabel();
        lblParroquia        = new javax.swing.JLabel();
        lblZona             = new javax.swing.JLabel();
        lblDocumentos       = new javax.swing.JLabel();
        lblEstadoTitulo     = new javax.swing.JLabel();
        lblEstado           = new javax.swing.JLabel();
        cboProvincia        = new javax.swing.JComboBox<>();
        cboCanton           = new javax.swing.JComboBox<>();
        cboCircunscripcion  = new javax.swing.JComboBox<>();
        cboParroquia        = new javax.swing.JComboBox<>();
        cboZona             = new javax.swing.JComboBox<>();
        jScrollPane1        = new javax.swing.JScrollPane();
        listDocumentos      = new javax.swing.JList<>();
        btnGenerar          = new javax.swing.JButton();
        chkSimulacro        = new javax.swing.JCheckBox();
        progressBar         = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Generación de Documentos - Sevilla Don Bosco");
        setResizable(false);

        lblProvincia.setText("Provincia:");
        lblCanton.setText("Cantón:");
        lblCircunscripcion.setText("Circunscripción:");
        lblParroquia.setText("Parroquia:");
        lblZona.setText("Zona:");
        lblDocumentos.setText("Documentos Electorales:");
        lblEstadoTitulo.setText("Estado:");
        lblEstado.setText("Iniciando...");

        btnGenerar.setText("Generar");
        chkSimulacro.setText("JRV Simulacro");

        // Listeners en cascada
        cboProvincia.addActionListener(e -> cargarCantones());
        cboCanton.addActionListener(e -> cargarCircunscripciones());
        cboCircunscripcion.addActionListener(e -> cargarParroquias());
        cboParroquia.addActionListener(e -> cargarZonas());
        btnGenerar.addActionListener(e -> generar());

        listDocumentos.setSelectionMode(
                javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(listDocumentos);

        // Layout
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        int comboW = 280;

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Provincia
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblProvincia, 110, 110, 110)
                        .addComponent(cboProvincia, comboW, comboW, comboW))
                    // Cantón
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCanton, 110, 110, 110)
                        .addComponent(cboCanton, comboW, comboW, comboW))
                    // Circunscripción
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCircunscripcion, 110, 110, 110)
                        .addComponent(cboCircunscripcion, comboW, comboW, comboW))
                    // Parroquia
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblParroquia, 110, 110, 110)
                        .addComponent(cboParroquia, comboW, comboW, comboW))
                    // Zona
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblZona, 110, 110, 110)
                        .addComponent(cboZona, comboW, comboW, comboW))
                    // Lista documentos
                    .addComponent(lblDocumentos)
                    .addComponent(jScrollPane1, 410, 410, 410)
                    // Barra progreso
                    .addComponent(progressBar, 410, 410, 410)
                    // Botón
                    .addComponent(btnGenerar, 410, 410, 410)
                    // Estado
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblEstadoTitulo)
                        .addGap(6)
                        .addComponent(lblEstado))
                    // Simulacro
                    .addComponent(chkSimulacro))
                .addGap(20))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProvincia)
                    .addComponent(cboProvincia))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCanton)
                    .addComponent(cboCanton))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCircunscripcion)
                    .addComponent(cboCircunscripcion))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblParroquia)
                    .addComponent(cboParroquia))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblZona)
                    .addComponent(cboZona))
                .addGap(12)
                .addComponent(lblDocumentos)
                .addGap(6)
                .addComponent(jScrollPane1, 250, 250, 250)
                .addGap(8)
                .addComponent(progressBar, 20, 20, 20)
                .addGap(8)
                .addComponent(btnGenerar, 35, 35, 35)
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstadoTitulo)
                    .addComponent(lblEstado))
                .addGap(6)
                .addComponent(chkSimulacro)
                .addGap(15))
        );

        pack();
    }// </editor-fold>

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }

    // Variables
    private javax.swing.JButton btnGenerar;
    private javax.swing.JCheckBox chkSimulacro;
    private javax.swing.JComboBox<Canton> cboCanton;
    private javax.swing.JComboBox<Circunscripcion> cboCircunscripcion;
    private javax.swing.JComboBox<Parroquia> cboParroquia;
    private javax.swing.JComboBox<Provincia> cboProvincia;
    private javax.swing.JComboBox<Zona> cboZona;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> listDocumentos;
    private javax.swing.JLabel lblCanton;
    private javax.swing.JLabel lblCircunscripcion;
    private javax.swing.JLabel lblDocumentos;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblEstadoTitulo;
    private javax.swing.JLabel lblParroquia;
    private javax.swing.JLabel lblProvincia;
    private javax.swing.JLabel lblZona;
    private javax.swing.JProgressBar progressBar;
}