package it.unicam.cs.mpgc.rpg125947.model.interfaces;

import it.unicam.cs.mpgc.rpg125947.model.dialogo.Dialogo;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Testimonianza;

import java.util.List;

/**
 * Contratto di un'entita con cui l'investigatore puo dialogare per raccogliere
 * testimonianze.
 *
 * <p>Descrive un <em>comportamento</em>, non una struttura: chiunque "sappia
 * farsi interrogare" e {@code Interrogabile}, indipendentemente dalla sua
 * gerarchia di classe. L'interfaccia e volutamente piccola e mirata
 * (Interface Segregation Principle).</p>
 */
public interface Interrogabile {

    /**
     * @return il dialogo da mostrare al giocatore quando avvia la conversazione
     */
    Dialogo avviaDialogo();

    /**
     * @return tutte le testimonianze che questa entita puo rilasciare
     */
    List<Testimonianza> testimonianze();
}
