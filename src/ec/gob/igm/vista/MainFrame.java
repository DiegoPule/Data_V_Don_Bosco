package ec.gob.igm.vista;

import ec.gob.igm.dao.DataAccessDBosco;
import ec.gob.igm.generador.GlobalResources;
import ec.gob.igm.model.Canton;
import ec.gob.igm.model.Circunscripcion;
import ec.gob.igm.model.Junta;
import ec.gob.igm.model.Parroquia;
import ec.gob.igm.model.Provincia;
import ec.gob.igm.model.Zona;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

public class MainFrame extends javax.swing.JFrame {

    private final DataAccessDBosco dao = new DataAccessDBosco();
    private List<String[]> documentosActuales;
    private DefaultListModel<CheckItem> modelDocumentos = new DefaultListModel<>();

    // ─────────────────────────────────────────
    // CLASE INTERNA — item con checkbox o cabecera
    // ─────────────────────────────────────────
    static class CheckItem {
        String texto;
        boolean seleccionado;
        boolean esCabecera;

        CheckItem(String texto, boolean seleccionado) {
            this.texto = texto;
            this.seleccionado = seleccionado;
            this.esCabecera = false;
        }
        static CheckItem cabecera(String titulo) {
            CheckItem item = new CheckItem(titulo, false);
            item.esCabecera = true;
            return item;
        }
        @Override public String toString() { return texto; }
    }

    // ─────────────────────────────────────────
    // RENDERER — documentos con checkbox, cabeceras con fondo azul
    // ─────────────────────────────────────────
    static class CheckBoxRenderer implements ListCellRenderer<CheckItem> {
        private final JCheckBox checkBox = new JCheckBox();
        private final javax.swing.JLabel cabLabel = new javax.swing.JLabel();

        @Override
        public Component getListCellRendererComponent(
                JList<? extends CheckItem> list, CheckItem value,
                int index, boolean isSelected, boolean cellHasFocus) {

            if (value.esCabecera) {
                cabLabel.setText("   " + value.texto);
                cabLabel.setFont(new Font("Arial", Font.BOLD, 11));
                cabLabel.setForeground(new Color(31, 56, 100));
                cabLabel.setBackground(new Color(210, 225, 245));
                cabLabel.setOpaque(true);
                cabLabel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(2, 3, 0, 0,
                        new Color(31, 56, 100)),
                    javax.swing.BorderFactory.createEmptyBorder(4, 6, 4, 6)));
                return cabLabel;
            }

            checkBox.setText("  " + value.texto);
            checkBox.setSelected(value.seleccionado);
            checkBox.setFont(new Font("Arial", Font.PLAIN, 11));
            checkBox.setOpaque(true);
            checkBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 18, 1, 4));

            if (isSelected) {
                checkBox.setBackground(new Color(210, 228, 252));
                checkBox.setForeground(new Color(31, 56, 100));
            } else {
                checkBox.setBackground(index % 2 == 0 ? Color.WHITE : new Color(248, 250, 255));
                checkBox.setForeground(Color.BLACK);
            }
            return checkBox;
        }
    }

    // ─────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────
    public MainFrame() {
        initComponents();
        mostrarInfoSistema();
        cargarProvincias();
        cargarDocumentos();
    }

    // ─────────────────────────────────────────
    // INFO DEL SISTEMA — cabecera
    // ─────────────────────────────────────────
    private void mostrarInfoSistema() {
        String host     = GlobalResources.get("db.host");
        String usuario  = GlobalResources.get("db.username");
        String contrato = GlobalResources.get("contrato.numero");
        String proceso  = GlobalResources.getNombreProceso();
        lblProceso.setText(proceso);
        lblHost.setText("BD: " + host + "  |  Usuario: " + usuario);
        lblContrato.setText("Contrato: " + contrato);
    }

    // ─────────────────────────────────────────
    // JERARQUIA EN CASCADA
    // ─────────────────────────────────────────
    private void cargarProvincias() {
        lblEstado.setText("Cargando datos...");
        List<Provincia> provincias = dao.getProvincias();
        DefaultComboBoxModel<Provincia> model = new DefaultComboBoxModel<>();
        for (Provincia p : provincias) model.addElement(p);
        cboProvincia.setModel(model);
        cargarCantones();
        lblEstado.setText("Listo.");
    }

    private void cargarCantones() {
        Provincia provincia = (Provincia) cboProvincia.getSelectedItem();
        if (provincia == null) return;
        List<Canton> cantones = dao.getCantonesByProvincia(provincia.getCodProvincia());
        DefaultComboBoxModel<Canton> model = new DefaultComboBoxModel<>();
        for (Canton c : cantones) model.addElement(c);
        cboCanton.setModel(model);
        cargarCircunscripciones();
    }

    private void cargarCircunscripciones() {
        Canton canton = (Canton) cboCanton.getSelectedItem();
        if (canton == null) return;
        List<Circunscripcion> circs = dao.getCircunscripcionesByCanton(canton.getCodCanton());
        DefaultComboBoxModel<Circunscripcion> model = new DefaultComboBoxModel<>();
        for (Circunscripcion ci : circs) model.addElement(ci);
        cboCircunscripcion.setModel(model);
        cargarParroquias();
    }

    private void cargarParroquias() {
        Canton canton       = (Canton) cboCanton.getSelectedItem();
        Circunscripcion circ = (Circunscripcion) cboCircunscripcion.getSelectedItem();
        if (canton == null || circ == null) return;
        List<Parroquia> parroquias = dao.getParroquiasByCircunscripcion(
                canton.getCodCanton(), circ.getCodCircunscripcion());
        DefaultComboBoxModel<Parroquia> model = new DefaultComboBoxModel<>();
        for (Parroquia p : parroquias) model.addElement(p);
        cboParroquia.setModel(model);
        cargarZonas();
    }

    private void cargarZonas() {
        Canton canton       = (Canton) cboCanton.getSelectedItem();
        Circunscripcion circ = (Circunscripcion) cboCircunscripcion.getSelectedItem();
        Parroquia parroquia  = (Parroquia) cboParroquia.getSelectedItem();
        if (canton == null || circ == null || parroquia == null) return;
        List<Zona> zonas = dao.getZonasByParroquia(
                canton.getCodCanton(),
                circ.getCodCircunscripcion(),
                parroquia.getCodParroquia());
        DefaultComboBoxModel<Zona> model = new DefaultComboBoxModel<>();
        for (Zona z : zonas) model.addElement(z);
        cboZona.setModel(model);
        actualizarInfoZona();
    }

    private void actualizarInfoZona() {
        Canton canton       = (Canton) cboCanton.getSelectedItem();
        Circunscripcion circ = (Circunscripcion) cboCircunscripcion.getSelectedItem();
        Parroquia parroquia  = (Parroquia) cboParroquia.getSelectedItem();
        Zona zona            = (Zona) cboZona.getSelectedItem();
        if (canton == null || circ == null || parroquia == null || zona == null) {
            lblInfoJuntas.setText("Juntas: \u2014");
            lblRutaSalida.setText("Ruta de salida: \u2014");
            return;
        }
        List<Junta> juntas = dao.getJuntasByZona(
                canton.getCodCanton(),
                circ.getCodCircunscripcion(),
                parroquia.getCodParroquia(),
                zona.getCodZona());
        long f = juntas.stream().filter(j -> "F".equals(j.getGenero())).count();
        long m = juntas.stream().filter(j -> "M".equals(j.getGenero())).count();
        lblInfoJuntas.setText("Juntas: " + juntas.size()
                + "   (" + f + " Femeninas  /  " + m + " Masculinas)");
        if (!juntas.isEmpty()) {
            String ruta = GlobalResources.getRutaPadron(juntas.get(0));
            int idx = ruta.indexOf("/JUNTA_");
            String rutaBase = (idx > 0) ? ruta.substring(0, idx) + "/" : ruta;
            lblRutaSalida.setText("Ruta: " + rutaBase);
            lblRutaSalida.setToolTipText(rutaBase);
        }
    }

    // ─────────────────────────────────────────
    // DOCUMENTOS CON CHECKBOXES Y CABECERAS
    // ─────────────────────────────────────────
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
        documentosActuales = dao.getDocumentosByKit(idKit);
        modelDocumentos.clear();

        // Mapa de grupos: orden del documento -> carpeta de salida
        // Basado en la estructura real del kit
        for (String[] doc : documentosActuales) {
            int orden = Integer.parseInt(doc[2].trim());

            // Insertar cabecera cuando empieza cada grupo
            if (orden == 1) {
                modelDocumentos.addElement(CheckItem.cabecera("ETIQUETAS  (Docs 1, 16, 17)"));
            } else if (orden == 2) {
                modelDocumentos.addElement(CheckItem.cabecera("PADRON ELECTORAL  (Doc 2)"));
            } else if (orden == 3) {
                modelDocumentos.addElement(CheckItem.cabecera("ACTAS DE INSTALACION  (Docs 3, 4, 5)"));
            } else if (orden == 6) {
                modelDocumentos.addElement(CheckItem.cabecera("BORRADORES DE ESCRUTINIO  (Docs 6, 7)"));
            } else if (orden == 8) {
                modelDocumentos.addElement(CheckItem.cabecera("ACTAS DE ESCRUTINIO  (Docs 8 - 15)"));
            } else if (orden == 16) {
                modelDocumentos.addElement(CheckItem.cabecera("ETIQUETAS  —  Formulario y Flujograma"));
            } else if (orden == 18) {
                modelDocumentos.addElement(CheckItem.cabecera("CERTIFICADOS  (Docs 18 - 21)"));
            }

            modelDocumentos.addElement(new CheckItem(doc[2] + ". " + doc[1], true));
        }

        listDocumentos.setModel(modelDocumentos);
        listDocumentos.setCellRenderer(new CheckBoxRenderer());

        // Toggle checkbox al hacer clic — ignora cabeceras
        listDocumentos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = listDocumentos.locationToIndex(e.getPoint());
                if (index >= 0) {
                    CheckItem item = modelDocumentos.getElementAt(index);
                    if (!item.esCabecera) {
                        item.seleccionado = !item.seleccionado;
                        listDocumentos.repaint();
                        actualizarContadorDocumentos();
                    }
                }
            }
        });
        actualizarContadorDocumentos();
    }

    private void seleccionarTodos() {
        for (int i = 0; i < modelDocumentos.size(); i++) {
            CheckItem item = modelDocumentos.getElementAt(i);
            if (!item.esCabecera) item.seleccionado = true;
        }
        listDocumentos.repaint();
        actualizarContadorDocumentos();
    }

    private void deseleccionarTodos() {
        for (int i = 0; i < modelDocumentos.size(); i++) {
            CheckItem item = modelDocumentos.getElementAt(i);
            if (!item.esCabecera) item.seleccionado = false;
        }
        listDocumentos.repaint();
        actualizarContadorDocumentos();
    }

    // ─────────────────────────────────────────
    // CONTADOR EN TIEMPO REAL + BOTON GENERAR
    // ─────────────────────────────────────────
    private void actualizarContadorDocumentos() {
        int total = 0, marcados = 0;
        for (int i = 0; i < modelDocumentos.size(); i++) {
            CheckItem item = modelDocumentos.getElementAt(i);
            if (!item.esCabecera) {
                total++;
                if (item.seleccionado) marcados++;
            }
        }
        if (total == 0) {
            lblDocumentos.setText("Documentos Electorales:");
        } else if (marcados == total) {
            lblDocumentos.setText("Documentos Electorales: todos (" + total + ")");
        } else if (marcados == 0) {
            lblDocumentos.setText("Documentos Electorales: ninguno seleccionado");
        } else {
            lblDocumentos.setText("Documentos Electorales: " + marcados + " de " + total + " seleccionados");
        }
        btnGenerar.setEnabled(marcados > 0);
        btnGenerar.setBackground(marcados > 0
                ? new Color(31, 56, 100)
                : new Color(160, 160, 160));
    }

    private List<String[]> getDocumentosSeleccionados() {
        List<String[]> seleccionados = new ArrayList<>();
        int docIndex = 0;
        for (int i = 0; i < modelDocumentos.size(); i++) {
            CheckItem item = modelDocumentos.getElementAt(i);
            if (!item.esCabecera) {
                if (item.seleccionado) seleccionados.add(documentosActuales.get(docIndex));
                docIndex++;
            }
        }
        return seleccionados;
    }

    // ─────────────────────────────────────────
    // ACCION GENERAR
    // ─────────────────────────────────────────
    private void generar() {
        Zona zona = (Zona) cboZona.getSelectedItem();
        if (zona == null) { lblEstado.setText("Seleccione una zona."); return; }
        List<String[]> seleccionados = getDocumentosSeleccionados();
        if (seleccionados.isEmpty()) { lblEstado.setText("Seleccione al menos un documento."); return; }

        Canton canton       = (Canton) cboCanton.getSelectedItem();
        Circunscripcion circ = (Circunscripcion) cboCircunscripcion.getSelectedItem();
        Parroquia parroquia  = (Parroquia) cboParroquia.getSelectedItem();

        List<Junta> juntas = dao.getJuntasByZona(
                canton.getCodCanton(),
                circ.getCodCircunscripcion(),
                parroquia.getCodParroquia(),
                zona.getCodZona());

        if (juntas.isEmpty()) {
            progressBar.setForeground(new Color(200, 40, 40));
            progressBar.setValue(100);
            lblEstado.setText("No hay juntas para esta zona.");
            return;
        }

        progressBar.setForeground(new Color(31, 56, 100));
        progressBar.setValue(0);
        lblEstado.setText("Preparando carpetas...");
        progressBar.setValue(10);
        GlobalResources.crearEstructuraCompleta(juntas);
        progressBar.setValue(30);
        lblEstado.setText("Generando " + seleccionados.size() + " doc(s) para " + juntas.size() + " juntas...");
        // Aqui ira JasperReports
        progressBar.setForeground(new Color(25, 140, 60));
        progressBar.setValue(100);
        lblEstado.setText("Listo: " + juntas.size() + " juntas | "
                + seleccionados.size() + " documentos | Zona: " + zona);
    }

    // ─────────────────────────────────────────
    // INTERFAZ GRAFICA
    // ─────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void initComponents() {

        panelInfo          = new javax.swing.JPanel();
        lblProceso         = new javax.swing.JLabel();
        lblHost            = new javax.swing.JLabel();
        lblContrato        = new javax.swing.JLabel();
        separador          = new javax.swing.JSeparator();
        lblProvincia       = new javax.swing.JLabel();
        lblCanton          = new javax.swing.JLabel();
        lblCircunscripcion = new javax.swing.JLabel();
        lblParroquia       = new javax.swing.JLabel();
        lblZona            = new javax.swing.JLabel();
        cboProvincia       = new javax.swing.JComboBox<>();
        cboCanton          = new javax.swing.JComboBox<>();
        cboCircunscripcion = new javax.swing.JComboBox<>();
        cboParroquia       = new javax.swing.JComboBox<>();
        cboZona            = new javax.swing.JComboBox<>();
        lblInfoJuntas      = new javax.swing.JLabel();
        lblRutaSalida      = new javax.swing.JLabel();
        lblDocumentos      = new javax.swing.JLabel();
        jScrollPane1       = new javax.swing.JScrollPane();
        listDocumentos     = new javax.swing.JList<>();
        btnSelTodo         = new javax.swing.JButton();
        btnDeselTodo       = new javax.swing.JButton();
        progressBar        = new javax.swing.JProgressBar();
        btnGenerar         = new javax.swing.JButton();
        chkSimulacro       = new javax.swing.JCheckBox();
        lblEstadoTitulo    = new javax.swing.JLabel();
        lblEstado          = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Generacion de Documentos - Sevilla Don Bosco 2025");
        setResizable(true);
        setMinimumSize(new java.awt.Dimension(480, 620));

        Color azulOscuro = new Color(31, 56, 100);
        Color azulClaro  = new Color(220, 235, 252);
        Color verde      = new Color(25, 110, 50);
        Color gris       = new Color(90, 90, 90);
        Font fNormal     = new Font("Arial", Font.PLAIN, 11);
        Font fBold       = new Font("Arial", Font.BOLD, 11);
        Font fTitulo     = new Font("Arial", Font.BOLD, 13);

        // Panel cabecera
        panelInfo.setBackground(azulClaro);
        panelInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, azulOscuro),
            javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        lblProceso.setFont(fTitulo);
        lblProceso.setForeground(azulOscuro);
        lblProceso.setText("Cargando...");
        lblHost.setFont(fNormal);
        lblHost.setForeground(gris);
        lblHost.setText("BD: -");
        lblContrato.setFont(fNormal);
        lblContrato.setForeground(gris);
        lblContrato.setText("Contrato: -");

        javax.swing.GroupLayout pi = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(pi);
        pi.setHorizontalGroup(pi.createParallelGroup()
            .addGroup(pi.createSequentialGroup()
                .addComponent(lblProceso)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHost).addGap(16).addComponent(lblContrato)));
        pi.setVerticalGroup(pi.createSequentialGroup()
            .addComponent(lblProceso).addGap(3)
            .addGroup(pi.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblHost).addComponent(lblContrato)));

        for (javax.swing.JLabel l : new javax.swing.JLabel[]{
                lblProvincia, lblCanton, lblCircunscripcion, lblParroquia, lblZona})
            l.setFont(fBold);
        lblProvincia.setText("Provincia:");
        lblCanton.setText("Canton:");
        lblCircunscripcion.setText("Circunscripcion:");
        lblParroquia.setText("Parroquia:");
        lblZona.setText("Zona:");

        lblInfoJuntas.setFont(fBold);
        lblInfoJuntas.setForeground(verde);
        lblInfoJuntas.setText("Juntas: -");
        lblRutaSalida.setFont(new Font("Arial", Font.PLAIN, 10));
        lblRutaSalida.setForeground(gris);
        lblRutaSalida.setText("Ruta de salida: -");

        cboProvincia.addActionListener(e -> cargarCantones());
        cboCanton.addActionListener(e -> cargarCircunscripciones());
        cboCircunscripcion.addActionListener(e -> cargarParroquias());
        cboParroquia.addActionListener(e -> cargarZonas());
        cboZona.addActionListener(e -> actualizarInfoZona());

        lblDocumentos.setFont(fBold);
        lblDocumentos.setText("Documentos Electorales:");
        listDocumentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listDocumentos.setVisibleRowCount(12);
        jScrollPane1.setViewportView(listDocumentos);

        btnSelTodo.setText("Todos");
        btnSelTodo.setFont(fNormal);
        btnSelTodo.addActionListener(e -> seleccionarTodos());
        btnDeselTodo.setText("Ninguno");
        btnDeselTodo.setFont(fNormal);
        btnDeselTodo.addActionListener(e -> deseleccionarTodos());

        progressBar.setStringPainted(true);
        progressBar.setForeground(azulOscuro);

        btnGenerar.setText("GENERAR DOCUMENTOS");
        btnGenerar.setFont(new Font("Arial", Font.BOLD, 13));
        btnGenerar.setBackground(azulOscuro);
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        btnGenerar.addActionListener(e -> generar());

        chkSimulacro.setText("JRV Simulacro");
        chkSimulacro.setFont(fNormal);
        lblEstadoTitulo.setText("Estado:");
        lblEstadoTitulo.setFont(fBold);
        lblEstado.setText("Iniciando...");
        lblEstado.setFont(fNormal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        int cW = 280, lW = 120;

        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addComponent(panelInfo)
            .addComponent(separador)
            .addGroup(layout.createSequentialGroup().addGap(15)
                .addGroup(layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblProvincia, lW, lW, lW)
                        .addComponent(cboProvincia, cW, cW, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCanton, lW, lW, lW)
                        .addComponent(cboCanton, cW, cW, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCircunscripcion, lW, lW, lW)
                        .addComponent(cboCircunscripcion, cW, cW, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblParroquia, lW, lW, lW)
                        .addComponent(cboParroquia, cW, cW, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblZona, lW, lW, lW)
                        .addComponent(cboZona, cW, cW, Short.MAX_VALUE))
                    .addComponent(lblInfoJuntas, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRutaSalida, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblDocumentos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSelTodo, 80, 80, 80).addGap(4)
                        .addComponent(btnDeselTodo, 80, 80, 80))
                    .addComponent(jScrollPane1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progressBar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGenerar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblEstadoTitulo).addGap(5)
                        .addComponent(lblEstado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(chkSimulacro))
                .addGap(15))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelInfo, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(separador, 4, 4, 4).addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProvincia).addComponent(cboProvincia)).addGap(6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCanton).addComponent(cboCanton)).addGap(6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCircunscripcion).addComponent(cboCircunscripcion)).addGap(6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblParroquia).addComponent(cboParroquia)).addGap(6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblZona).addComponent(cboZona)).addGap(8)
                .addComponent(lblInfoJuntas).addGap(3)
                .addComponent(lblRutaSalida).addGap(10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDocumentos)
                    .addComponent(btnSelTodo).addComponent(btnDeselTodo)).addGap(4)
                .addComponent(jScrollPane1, 240, 320, Short.MAX_VALUE)
                .addGap(7)
                .addComponent(progressBar, 20, 20, 20).addGap(7)
                .addComponent(btnGenerar, 38, 38, 38).addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstadoTitulo).addComponent(lblEstado))
                .addGap(5).addComponent(chkSimulacro).addGap(12))
        );

        pack();
        setMinimumSize(getSize());
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables
    private javax.swing.JPanel panelInfo;
    private javax.swing.JSeparator separador;
    private javax.swing.JLabel lblProceso, lblHost, lblContrato;
    private javax.swing.JButton btnGenerar, btnSelTodo, btnDeselTodo;
    private javax.swing.JCheckBox chkSimulacro;
    private javax.swing.JComboBox<Canton> cboCanton;
    private javax.swing.JComboBox<Circunscripcion> cboCircunscripcion;
    private javax.swing.JComboBox<Parroquia> cboParroquia;
    private javax.swing.JComboBox<Provincia> cboProvincia;
    private javax.swing.JComboBox<Zona> cboZona;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<CheckItem> listDocumentos;
    private javax.swing.JLabel lblCanton, lblCircunscripcion, lblDocumentos;
    private javax.swing.JLabel lblEstado, lblEstadoTitulo, lblInfoJuntas;
    private javax.swing.JLabel lblParroquia, lblProvincia, lblRutaSalida, lblZona;
    private javax.swing.JProgressBar progressBar;
}