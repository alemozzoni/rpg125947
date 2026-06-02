package it.unicam.cs.mpgc.rpg125947.app.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Effetto "macchina da scrivere": rivela progressivamente il testo di una
 * {@link Label}, dando ritmo ai dialoghi e alle descrizioni.
 */
public final class EffettoTesto {

    private static final double MS_PER_CARATTERE = 16;

    private EffettoTesto() {
    }

    /**
     * Anima la comparsa del testo nella label. Eventuali animazioni precedenti
     * sulla stessa label non vengono tracciate: si assume un uso sequenziale.
     */
    public static void scrivi(Label label, String testo) {
        label.setText("");
        Timeline timeline = new Timeline();
        for (int i = 1; i <= testo.length(); i++) {
            final String parziale = testo.substring(0, i);
            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(MS_PER_CARATTERE * i),
                    e -> label.setText(parziale)));
        }
        timeline.play();
    }
}
