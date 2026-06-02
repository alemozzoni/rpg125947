package it.unicam.cs.mpgc.rpg125947.logic.fase;

import it.unicam.cs.mpgc.rpg125947.model.AzioneGiocatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.FaseDiGioco;

/**
 * Fase terminale: il caso e chiuso e viene mostrato l'epilogo. Nessuna azione
 * comporta ulteriori transizioni (stato finale).
 */
public final class FaseFinale implements FaseDiGioco {

    public static final FaseFinale INSTANCE = new FaseFinale();

    private FaseFinale() {
    }

    @Override
    public String nome() {
        return "Finale";
    }

    @Override
    public FaseDiGioco gestisci(AzioneGiocatore azione, Partita partita) {
        return this; // stato terminale
    }
}
