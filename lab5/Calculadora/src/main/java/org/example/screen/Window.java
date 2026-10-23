
package org.example.screen;

import org.example.Connection;
import org.example.screen.components.Buttons;
import org.example.screen.components.PanelResult;
import org.example.screen.components.TextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class Window extends JFrame {

    private static final String[][] EJEMPLOS = {
            {"10", "5"}, {"-3", "8"}, {"7.5", "2.5"}
    };

    private final TextField txtNumero1 = new TextField("Ej: 10");
    private final TextField txtNumero2 = new TextField("Ej: 5");
    private final PanelResult panelResultado = new PanelResult();
    private final JLabel lblEstado = new JLabel(" ");

    public Window(Connection motor) {
        super("Calculadora de sumas y restas en Prolog");

        txtNumero1.addActionListener(e -> ejecutar(motor, "suma"));
        txtNumero2.addActionListener(e -> ejecutar(motor, "suma"));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        agregar(contenido, etiqueta("Primer número"), 6);
        agregar(contenido, txtNumero1, 12);
        agregar(contenido, etiqueta("Segundo número"), 6);
        agregar(contenido, txtNumero2, 8);
        agregar(contenido, crearEjemplos(), 18);
        agregar(contenido, crearBotones(motor), 22);
        agregar(contenido, panelResultado, 10);
        agregar(contenido, crearPie(), 0);

        JPanel raiz = new JPanel(new BorderLayout(0, 20));
        raiz.setBackground(Theme.BG);
        raiz.setBorder(new EmptyBorder(24, 28, 24, 28));
        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(contenido, BorderLayout.CENTER);

        setContentPane(raiz);
        setMinimumSize(new Dimension(660, 560));
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel crearEncabezado() {
        JLabel titulo = new JLabel("Calculadora básica");
        titulo.setFont(Theme.sans(Font.BOLD, 24));
        titulo.setForeground(Theme.TEXT);

        JLabel subtitulo = new JLabel("Sumas y restas resueltas con reglas en Prolog");
        subtitulo.setFont(Theme.sans(Font.PLAIN, 13));
        subtitulo.setForeground(Theme.MUTED);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.add(titulo);
        p.add(Box.createVerticalStrut(4));
        p.add(subtitulo);
        return p;
    }

    private JPanel crearEjemplos() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);

        JLabel lbl = new JLabel("Ejemplos:");
        lbl.setForeground(Theme.MUTED);
        lbl.setFont(Theme.sans(Font.PLAIN, 12));
        p.add(lbl);

        for (String[] ejemplo : EJEMPLOS) {
            Buttons chip = new Buttons(ejemplo[0] + " y " + ejemplo[1],
                    Theme.CARD, Theme.TEXT, 12, 6, 11);
            chip.addActionListener(e -> {
                txtNumero1.setText(ejemplo[0]);
                txtNumero2.setText(ejemplo[1]);
                txtNumero1.requestFocusInWindow();
            });
            p.add(chip);
        }
        return p;
    }

    private JPanel crearBotones(Connection motor) {
        Buttons btnSumar = new Buttons("Sumar (+)", Theme.BLUE, Theme.BG, 26, 11, 14);
        Buttons btnRestar = new Buttons("Restar (-)", Theme.GREEN, Theme.BG, 26, 11, 14);
        btnSumar.addActionListener(e -> ejecutar(motor, "suma"));
        btnRestar.addActionListener(e -> ejecutar(motor, "resta"));

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        p.add(btnSumar);
        p.add(btnRestar);
        return p;
    }

    private JPanel crearPie() {
        lblEstado.setFont(Theme.sans(Font.PLAIN, 12));
        lblEstado.setForeground(Theme.MUTED);

        Buttons btnCopiar = new Buttons("Copiar", Theme.CARD, Theme.TEXT, 16, 7, 12);
        btnCopiar.addActionListener(e -> copiarResultado());

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(lblEstado, BorderLayout.WEST);
        p.add(btnCopiar, BorderLayout.EAST);
        return p;
    }

    private void ejecutar(Connection motor, String operacion) {
        String numero1 = txtNumero1.getText().trim();
        String numero2 = txtNumero2.getText().trim();
        String nombre = "suma".equals(operacion) ? "suma" : "resta";
        panelResultado.setTitulo("Resultado de la " + nombre);

        if (numero1.isEmpty() || numero2.isEmpty()) {
            mostrar("", "Escribe ambos números primero.", Theme.AMBER);
            return;
        }

        try {
            String resultado = motor.consultar(operacion, numero1, numero2);
            if (resultado.startsWith("Sin solución")) {
                mostrar("", resultado, Theme.AMBER);
            } else {
                mostrar(resultado, "Listo", Theme.GREEN);
            }
        } catch (IllegalArgumentException ex) {
            mostrar("", ex.getMessage(), Theme.AMBER);
        } catch (Exception ex) {
            mostrar("", "No se pudo calcular: " + ex.getMessage(), Theme.RED);
        }
    }

    private void mostrar(String resultado, String estado, Color colorEstado) {
        panelResultado.setTexto(resultado);
        lblEstado.setText(estado);
        lblEstado.setForeground(colorEstado);
    }

    private void copiarResultado() {
        String texto = panelResultado.getTexto();
        if (texto.isEmpty()) {
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(texto), null);
        lblEstado.setText("Copiado al portapapeles");
        lblEstado.setForeground(Theme.BLUE);
    }

    private static void agregar(JPanel panel, JComponent componente, int espacioDespues) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(componente);
        if (espacioDespues > 0) {
            panel.add(Box.createVerticalStrut(espacioDespues));
        }
    }

    private static JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(Theme.sans(Font.BOLD, 12));
        label.setForeground(Theme.MUTED);
        return label;
    }
}
