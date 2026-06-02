package it.unicam.cs.mpgc.rpg125947.app;

/**
 * Punto di ingresso dell'applicazione.
 *
 * <p>E una classe separata che <em>non</em> estende {@link javafx.application.Application}:
 * questo evita l'errore "JavaFX runtime components are missing" quando si avvia
 * il jar senza i moduli JavaFX sul module-path. Delega l'avvio a
 * {@link Applicazione}.</p>
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        Applicazione.main(args);
    }
}
