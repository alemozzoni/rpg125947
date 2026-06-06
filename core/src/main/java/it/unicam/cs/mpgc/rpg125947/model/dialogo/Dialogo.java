package it.unicam.cs.mpgc.rpg125947.model.dialogo;

import it.unicam.cs.mpgc.rpg125947.model.Attributo;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * La conversazione offerta da un personaggio: una battuta di apertura e una
 * lista di opzioni di domanda, ciascuna con la relativa testimonianza.
 *
 * <p>La lista di opzioni e restituita in sola lettura per proteggere lo stato
 * interno (incapsulamento).</p>
 */
public final class Dialogo {

    private final String battutaIniziale;
    private final List<OpzioneDialogo> opzioni;

    public Dialogo(String battutaIniziale, List<OpzioneDialogo> opzioni) {
        this.battutaIniziale = Validazioni.nonVuota(battutaIniziale, "battuta iniziale");
        this.opzioni = new ArrayList<>(Validazioni.nonNullo(opzioni, "opzioni dialogo"));
    }

    public String getBattutaIniziale() {
        return battutaIniziale;
    }

    public List<OpzioneDialogo> getOpzioni() {
        return Collections.unmodifiableList(opzioni);
    }

    /**
     * Le opzioni effettivamente disponibili per un dato <strong>stile
     * investigativo</strong>: tutte quelle universali piu quelle riservate
     * proprio a quell'attributo dominante. E qui che il dialogo si ramifica in
     * base a dove il giocatore ha investito i punti caratteristica.
     *
     * @param stileDominante attributo dominante dell'investigatore
     * @return la lista (in sola lettura) delle domande proponibili a quel profilo
     */
    public List<OpzioneDialogo> opzioniPer(Attributo stileDominante) {
        Validazioni.nonNullo(stileDominante, "stile dominante");
        return opzioni.stream()
                .filter(o -> o.disponibilePer(stileDominante))
                .toList();
    }
}
