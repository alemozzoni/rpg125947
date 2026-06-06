package it.unicam.cs.mpgc.rpg125947.model.dialogo;

import it.unicam.cs.mpgc.rpg125947.model.Attributo;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Optional;

/**
 * Una domanda selezionabile dal giocatore in un dialogo e la testimonianza che
 * il personaggio fornisce in risposta.
 *
 * <p>Una domanda puo essere una <strong>prova di abilita</strong>: se
 * {@code attributo} e valorizzato, la testimonianza si ottiene solo superando
 * un controllo (es. convincere un testimone reticente con l'Eloquenza). In
 * assenza di attributo la risposta e sempre disponibile.</p>
 *
 * <p>Una domanda puo inoltre essere <strong>caratterizzante</strong>: se
 * {@code stile} e valorizzato, l'opzione compare solo all'investigatore il cui
 * attributo dominante coincide con esso. E il meccanismo dei dialoghi ramificati
 * per stile investigativo: a parita di indizi raggiungibili, profili diversi
 * pongono domande diverse e ricevono risposte diverse. Se {@code stile} e
 * {@code null} la domanda e <em>universale</em> (mostrata a chiunque).</p>
 *
 * @param domanda    testo della domanda mostrata come pulsante di scelta
 * @param risposta   testimonianza restituita dal personaggio
 * @param attributo  attributo messo alla prova (puo essere {@code null})
 * @param difficolta soglia della prova; rilevante solo se {@code attributo} e presente
 * @param stile      attributo dominante a cui la domanda e riservata (puo essere {@code null})
 */
public record OpzioneDialogo(String domanda, Testimonianza risposta,
                             Attributo attributo, int difficolta, Attributo stile) {

    public OpzioneDialogo {
        Validazioni.nonVuota(domanda, "domanda");
        Validazioni.nonNullo(risposta, "risposta");
        if (attributo != null && difficolta <= 0) {
            throw new IllegalArgumentException("Una prova richiede una difficolta positiva");
        }
    }

    /** Opzione con eventuale prova di abilita ma universale (nessuno stile). */
    public OpzioneDialogo(String domanda, Testimonianza risposta, Attributo attributo, int difficolta) {
        this(domanda, risposta, attributo, difficolta, null);
    }

    /** Opzione semplice, senza prova di abilita ne vincolo di stile. */
    public OpzioneDialogo(String domanda, Testimonianza risposta) {
        this(domanda, risposta, null, 0, null);
    }

    /** @return {@code true} se ottenere la risposta richiede di superare una prova */
    public boolean richiedeProva() {
        return attributo != null;
    }

    /** @return l'attributo messo alla prova, se la domanda e una sfida */
    public Optional<Attributo> attributoRichiesto() {
        return Optional.ofNullable(attributo);
    }

    /** @return lo stile investigativo a cui la domanda e riservata, se presente */
    public Optional<Attributo> stileRichiesto() {
        return Optional.ofNullable(stile);
    }

    /**
     * @param dominante stile investigativo (attributo dominante) del giocatore
     * @return {@code true} se la domanda e disponibile per quel profilo: lo e
     *         sempre se universale, altrimenti solo se lo stile coincide
     */
    public boolean disponibilePer(Attributo dominante) {
        return stile == null || stile == dominante;
    }
}
