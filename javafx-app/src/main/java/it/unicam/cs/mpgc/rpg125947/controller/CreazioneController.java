package it.unicam.cs.mpgc.rpg125947.controller;

import it.unicam.cs.mpgc.rpg125947.app.AppContext;
import it.unicam.cs.mpgc.rpg125947.model.Attributo;
import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Investigatore;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.EnumMap;
import java.util.Map;

/**
 * Controller della schermata iniziale di creazione del personaggio.
 *
 * <p>Raccoglie il nome dell'investigatore e la <strong>distribuzione dei punti
 * caratteristica</strong> (la componente di ruolo: ogni attributo parte dal
 * valore base e il giocatore investe un pool di punti), crea la
 * {@link it.unicam.cs.mpgc.rpg125947.model.Partita} e passa alla prima stanza.
 * Il bottone resta disabilitato finche il nome e vuoto (binding dichiarativo).</p>
 */
public final class CreazioneController {

    private final AppContext context;

    /** Valori correnti scelti per ciascun attributo. */
    private final Map<Attributo, Integer> valori = new EnumMap<>(Attributo.class);
    /** Punti del pool non ancora distribuiti. */
    private int puntiResidui = Investigatore.PUNTI_CREAZIONE;
    /** Etichette del valore corrente, per aggiornarle al variare delle scelte. */
    private final Map<Attributo, Label> valoreLabel = new EnumMap<>(Attributo.class);

    @FXML private TextField campoNome;
    @FXML private Button inizia;
    @FXML private Label messaggioErrore;
    @FXML private Label puntiRimanenti;
    @FXML private Label stileScelto;
    @FXML private VBox schedaAttributi;

    public CreazioneController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        // Binding: niente nome, niente partita. Validazione dichiarativa.
        inizia.disableProperty().bind(campoNome.textProperty().isEmpty());

        for (Attributo attributo : Attributo.values()) {
            valori.put(attributo, Investigatore.VALORE_BASE);
            schedaAttributi.getChildren().add(rigaAttributo(attributo));
        }
        aggiornaPunti();
    }

    /** Costruisce la riga di un attributo: nome, valore corrente e pulsanti +/-. */
    private HBox rigaAttributo(Attributo attributo) {
        Label nome = new Label(attributo.etichetta());
        nome.getStyleClass().add("attributo-nome");
        nome.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nome, Priority.ALWAYS);

        Label valore = new Label();
        valore.getStyleClass().add("attributo-valore");
        valoreLabel.put(attributo, valore);

        Button meno = new Button("−"); // segno meno
        meno.getStyleClass().add("bottone-attributo");
        meno.setOnAction(e -> modifica(attributo, -1));

        Button piu = new Button("+");
        piu.getStyleClass().add("bottone-attributo");
        piu.setOnAction(e -> modifica(attributo, +1));

        HBox riga = new HBox(10, nome, meno, valore, piu);
        riga.setAlignment(Pos.CENTER_LEFT);
        riga.getStyleClass().add("riga-attributo");
        return riga;
    }

    /** Applica una variazione (+1/-1) a un attributo rispettando pool e limiti. */
    private void modifica(Attributo attributo, int delta) {
        int corrente = valori.get(attributo);
        int nuovo = corrente + delta;
        if (nuovo < Investigatore.VALORE_BASE || nuovo > Investigatore.MASSIMO_IN_CREAZIONE) {
            return;
        }
        if (delta > 0 && puntiResidui <= 0) {
            return; // niente punti da spendere
        }
        valori.put(attributo, nuovo);
        puntiResidui -= delta;
        aggiornaPunti();
    }

    /** Aggiorna le etichette dei valori, del pool residuo e dello stile risultante. */
    private void aggiornaPunti() {
        valori.forEach((attributo, valore) -> valoreLabel.get(attributo).setText(String.valueOf(valore)));
        puntiRimanenti.setText("Punti da distribuire: " + puntiResidui);
        stileScelto.setText("Stile investigativo: " + stileDominante().etichetta()
                + " — sblocchera domande e risposte dedicate nei dialoghi.");
    }

    /**
     * L'attributo con piu punti tra quelli scelti finora (a parita vince il primo
     * nell'ordine dell'enum): anticipa lo stile investigativo del personaggio,
     * coerente con {@link Investigatore#attributoDominante()}.
     */
    private Attributo stileDominante() {
        Attributo dominante = Attributo.values()[0];
        for (Attributo a : Attributo.values()) {
            if (valori.get(a) > valori.get(dominante)) {
                dominante = a;
            }
        }
        return dominante;
    }

    @FXML
    private void onInizia() {
        try {
            Caso caso = context.getCaso();
            Investigatore investigatore = new Investigatore(campoNome.getText().trim(), valori);
            context.getMotore().nuovaPartita(investigatore, caso);
            context.getSceneManager().mostraEsplorazione();
        } catch (RuntimeException e) {
            messaggioErrore.setText("Impossibile avviare il gioco: " + e.getMessage());
        }
    }
}
