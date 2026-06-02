package it.unicam.cs.mpgc.rpg125947.model.dialogo;

import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

/**
 * Una domanda selezionabile dal giocatore in un dialogo e la testimonianza che
 * il personaggio fornisce in risposta.
 *
 * @param domanda  testo della domanda mostrata come pulsante di scelta
 * @param risposta testimonianza restituita dal personaggio
 */
public record OpzioneDialogo(String domanda, Testimonianza risposta) {

    public OpzioneDialogo {
        Validazioni.nonVuota(domanda, "domanda");
        Validazioni.nonNullo(risposta, "risposta");
    }
}
