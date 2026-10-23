package org.example;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Theory;

import java.io.InputStream;
import java.math.BigDecimal;

public class Connection {
    private final Prolog engine = new Prolog();

    public Connection() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/calculo.pl")) {
            if (in == null) throw new IllegalStateException("No se encontró calculo.pl");
            engine.setTheory(new Theory(in));
        }
    }

    public String consultar(String operacion, String numero1, String numero2) throws Exception {
        if (!"suma".equals(operacion) && !"resta".equals(operacion)) {
            throw new IllegalArgumentException("Solo se permiten sumas y restas.");
        }

        String a;
        String b;
        try {
            a = new BigDecimal(numero1.trim()).toPlainString();
            b = new BigDecimal(numero2.trim()).toPlainString();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Ingresa números válidos (usa punto para decimales).", ex);
        }

        SolveInfo info = engine.solve(operacion + "(" + a + ", " + b + ", R).");
        return info.isSuccess() ? info.getTerm("R").toString()
                : "Sin solución para esa operación";
    }
}