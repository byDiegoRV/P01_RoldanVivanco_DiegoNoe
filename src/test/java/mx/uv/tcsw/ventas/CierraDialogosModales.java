package mx.uv.tcsw.ventas;

import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Extension de JUnit 5 (autodetectada, ver junit-platform.properties) que cierra los
 * cuadros de dialogo modales (JOptionPane) que las pruebas de Swing abren a proposito.
 *
 * Sin ella, una prueba que dispara un JOptionPane espera a que una persona pulse
 * "Aceptar": en una maquina con pantalla alguien lo hace a mano, pero en integracion
 * continua nadie lo hace y la ejecucion se queda bloqueada.
 *
 * Un dialogo se cierra solo si lleva visible mas de ESPERA_MS, para no competir con las
 * pruebas que leen el mensaje y lo cierran ellas mismas (VentasControllerPersistenteTest).
 */
public class CierraDialogosModales implements BeforeAllCallback {

    private static final long ESPERA_MS = 1000;
    private static final long PERIODO_MS = 200;
    private static Thread vigilante;

    @Override
    public void beforeAll(ExtensionContext context) {
        iniciar();
    }

    private static synchronized void iniciar() {
        if (vigilante != null || GraphicsEnvironment.isHeadless()) {
            return;
        }
        vigilante = new Thread(CierraDialogosModales::vigilar, "cierra-dialogos-modales");
        vigilante.setDaemon(true);
        vigilante.start();
    }

    private static void vigilar() {
        Map<Window, Long> vistos = new IdentityHashMap<>();
        while (true) {
            long ahora = System.currentTimeMillis();
            for (Window ventana : Window.getWindows()) {
                if (ventana instanceof JDialog && ventana.isShowing() && ((JDialog) ventana).isModal()) {
                    long desde = vistos.computeIfAbsent(ventana, k -> ahora);
                    if (ahora - desde >= ESPERA_MS) {
                        vistos.remove(ventana);
                        SwingUtilities.invokeLater(ventana::dispose);
                    }
                }
            }
            vistos.keySet().removeIf(v -> !v.isShowing());
            try {
                Thread.sleep(PERIODO_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
