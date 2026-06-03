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
 * @param domanda    testo della domanda mostrata come pulsante di scelta
 * @param risposta   testimonianza restituita dal personaggio
 * @param attributo  attributo messo alla prova (puo essere {@code null})
 * @param difficolta soglia della prova; rilevante solo se {@code attributo} e presente
 */
public record OpzioneDialogo(String domanda, Testimonianza risposta,
                             Attributo attributo, int difficolta) {

    public OpzioneDialogo {
        Validazioni.nonVuota(domanda, "domanda");
        Validazioni.nonNullo(risposta, "risposta");
        if (attributo != null && difficolta <= 0) {
            throw new IllegalArgumentException("Una prova richiede una difficolta positiva");
        }
    }

    /** Opzione semplice, senza prova di abilita. */
    public OpzioneDialogo(String domanda, Testimonianza risposta) {
        this(domanda, risposta, null, 0);
    }

    /** @return {@code true} se ottenere la risposta richiede di superare una prova */
    public boolean richiedeProva() {
        return attributo != null;
    }

    /** @return l'attributo messo alla prova, se la domanda e una sfida */
    public Optional<Attributo> attributoRichiesto() {
        return Optional.ofNullable(attributo);
    }
}
