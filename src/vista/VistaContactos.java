package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.util.regex.Pattern;
import modelo.Contacto;

public class VistaContactos extends JFrame {

    // Campos para ingresar los datos
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtCategoria;
    private JTextField txtBuscar;

    // Botones
    private JButton btnAgregar;
    private JButton btnLimpiar;
    private JButton btnEliminar;
    private JButton btnFavorito;
    private JButton btnExportar;

    // Tabla y modelo
    private JTable tablaContactos;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> ordenador;

    // Pestañas y paneles
    private JTabbedPane pestañas;
    private JPanel panelContactos;
    private JPanel panelEstadisticas;

    // Estadísticas y progreso
    private JLabel lblTotal;
    private JLabel lblFavoritos;
    private JProgressBar barraProgreso;

    public VistaContactos() {

        setTitle("Gestión de Contactos");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Crear pestañas
        pestañas = new JTabbedPane();

        panelContactos = new JPanel(new BorderLayout(10, 10));
        panelContactos.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        // Barra de progreso
        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setValue(0);
        barraProgreso.setStringPainted(true);
        barraProgreso.setString("Listo");

        // Panel de estadísticas
        panelEstadisticas = new JPanel(new GridLayout(2, 1, 10, 10));

        lblTotal = new JLabel("Total de contactos: 0");
        lblFavoritos = new JLabel("Contactos favoritos: 0");

        lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
        lblFavoritos.setHorizontalAlignment(SwingConstants.CENTER);

        panelEstadisticas.add(lblTotal);
        panelEstadisticas.add(lblFavoritos);

        panelEstadisticas.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // Formulario
        JPanel panelFormulario = new JPanel(
                new GridLayout(4, 2, 10, 10)
        );

        txtNombre = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();
        txtCategoria = new JTextField();

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Teléfono:"));
        panelFormulario.add(txtTelefono);

        panelFormulario.add(new JLabel("Correo:"));
        panelFormulario.add(txtCorreo);

        panelFormulario.add(new JLabel("Categoría:"));
        panelFormulario.add(txtCategoria);

        // Botones
        btnAgregar = new JButton("Agregar contacto");
        btnLimpiar = new JButton("Limpiar");
        btnEliminar = new JButton("Eliminar contacto");
        btnFavorito = new JButton("Marcar favorito");
        btnExportar = new JButton("Exportar CSV");

        // Atajo Alt + N
        btnAgregar.setMnemonic('N');
        btnAgregar.setToolTipText(
                "Agregar contacto (Alt + N o Ctrl + N)"
        );

        // Atajo Ctrl + N
        panelContactos.getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW
        ).put(
                KeyStroke.getKeyStroke("control N"),
                "agregarContacto"
        );

        panelContactos.getActionMap().put(
                "agregarContacto",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent e) {
                        btnAgregar.doClick();
                    }
                }
        );

        // Atajo Ctrl + L
        panelContactos.getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW
        ).put(
                KeyStroke.getKeyStroke("control L"),
                "limpiarCampos"
        );

        panelContactos.getActionMap().put(
                "limpiarCampos",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent e) {
                        btnLimpiar.doClick();
                    }
                }
        );

        // Modelo de la tabla: celdas no editables
        modeloTabla = new DefaultTableModel(
                new Object[]{
                    "Nombre", "Teléfono", "Correo",
                    "Categoría", "Favorito"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaContactos = new JTable(modeloTabla);
        tablaContactos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // Ordenamiento de columnas
        ordenador = new TableRowSorter<>(modeloTabla);
        tablaContactos.setRowSorter(ordenador);

        // Campo de búsqueda
        txtBuscar = new JTextField(20);

        JPanel panelBusqueda = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelBusqueda.add(new JLabel("Buscar contacto:"));
        panelBusqueda.add(txtBuscar);

        // Filtrado en tiempo real
        txtBuscar.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

            @Override
            public void insertUpdate(
                    javax.swing.event.DocumentEvent e) {
                filtrarContactos();
            }

            @Override
            public void removeUpdate(
                    javax.swing.event.DocumentEvent e) {
                filtrarContactos();
            }

            @Override
            public void changedUpdate(
                    javax.swing.event.DocumentEvent e) {
                filtrarContactos();
            }
        });

        // Panel superior: formulario y buscador
        JPanel panelSuperior = new JPanel(
                new BorderLayout(5, 5)
        );

        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);

        panelContactos.add(panelSuperior, BorderLayout.NORTH);

        // Tabla con desplazamiento
        JScrollPane scrollTabla = new JScrollPane(tablaContactos);
        panelContactos.add(scrollTabla, BorderLayout.CENTER);

        // Menú contextual de clic derecho
        JPopupMenu menuContextual = new JPopupMenu();

        JMenuItem opcionFavorito =
                new JMenuItem("Marcar favorito");

        JMenuItem opcionEliminar =
                new JMenuItem("Eliminar contacto");

        menuContextual.add(opcionFavorito);
        menuContextual.add(opcionEliminar);

        opcionFavorito.addActionListener(
                e -> btnFavorito.doClick()
        );

        opcionEliminar.addActionListener(
                e -> btnEliminar.doClick()
        );

        tablaContactos.setComponentPopupMenu(menuContextual);

        // Panel de botones (sin duplicados)
        JPanel panelBotones = new JPanel(new FlowLayout());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnFavorito);
        panelBotones.add(btnExportar);

        // Panel inferior: botones y barra de progreso
        JPanel panelInferior = new JPanel(
                new BorderLayout(5, 5)
        );

        panelInferior.add(panelBotones, BorderLayout.NORTH);
        panelInferior.add(barraProgreso, BorderLayout.SOUTH);

        panelContactos.add(panelInferior, BorderLayout.SOUTH);

        // Agregar pestañas
        pestañas.addTab("Contactos", panelContactos);
        pestañas.addTab("Estadísticas", panelEstadisticas);

        panelPrincipal.add(pestañas, BorderLayout.CENTER);
        setContentPane(panelPrincipal);
    }

    // Filtrar por cualquier columna de la tabla
    private void filtrarContactos() {
        String texto = txtBuscar.getText().trim();

        if (texto.isEmpty()) {
            ordenador.setRowFilter(null);
        } else {
            ordenador.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + Pattern.quote(texto)
                    )
            );
        }
    }

    // Obtener los datos del formulario
    public String getNombre() {
        return txtNombre.getText().trim();
    }

    public String getTelefono() {
        return txtTelefono.getText().trim();
    }

    public String getCorreo() {
        return txtCorreo.getText().trim();
    }

    public String getCategoria() {
        return txtCategoria.getText().trim();
    }

    // Obtener botones
    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JButton getBtnFavorito() {
        return btnFavorito;
    }

    public JButton getBtnExportar() {
        return btnExportar;
    }

    // Limpiar formulario
    public void limpiarCampos() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtCategoria.setText("");
        txtNombre.requestFocusInWindow();
    }

    // Agregar contacto a la tabla
    public void agregarFila(Contacto contacto) {
        modeloTabla.addRow(new Object[]{
            contacto.getNombre(),
            contacto.getTelefono(),
            contacto.getCorreo(),
            contacto.getCategoria(),
            contacto.isFavorito() ? "Sí" : "No"
        });
    }

    // Obtener la fila seleccionada en la vista de la tabla
    public int getFilaSeleccionada() {
        return tablaContactos.getSelectedRow();
    }

    // Convertir una fila visible al índice del modelo de tabla
    public int convertirFilaAModelo(int filaVista) {
        return tablaContactos.convertRowIndexToModel(filaVista);
    }

    // Eliminar fila usando índice del modelo de tabla
    public void eliminarFila(int filaModelo) {
        modeloTabla.removeRow(filaModelo);
    }

    // Actualizar fila usando índice del modelo de tabla
    public void actualizarFila(
            int filaModelo, Contacto contacto) {

        modeloTabla.setValueAt(contacto.getNombre(), filaModelo, 0);
        modeloTabla.setValueAt(contacto.getTelefono(), filaModelo, 1);
        modeloTabla.setValueAt(contacto.getCorreo(), filaModelo, 2);
        modeloTabla.setValueAt(contacto.getCategoria(), filaModelo, 3);
        modeloTabla.setValueAt(
                contacto.isFavorito() ? "Sí" : "No",
                filaModelo, 4
        );
    }

    // Actualizar estadísticas
    public void actualizarEstadisticas(
            int total, int favoritos) {

        lblTotal.setText("Total de contactos: " + total);
        lblFavoritos.setText("Contactos favoritos: " + favoritos);
    }

    // Actualizar barra de progreso
    public void actualizarProgreso(int valor, String mensaje) {
        barraProgreso.setValue(valor);
        barraProgreso.setString(mensaje);
    }

    // Limpiar campo de búsqueda
    public void limpiarBusqueda() {
        txtBuscar.setText("");
    }
}
