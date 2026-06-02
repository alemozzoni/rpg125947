package it.unicam.cs.mpgc.rpg125947.model.interfaces;

import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.accusa.Accusa;
import it.unicam.cs.mpgc.rpg125947.model.accusa.EsitoAccusa;

/**
 * Strategy che stabilisce se un'accusa formulata dal giocatore e corretta.
 *
 * <p>Astrarre la regola dietro un'interfaccia rende il criterio di vittoria
 * intercambiabile senza toccare il resto del codice (Open/Closed e Dependency
 * Inversion): si potrebbe introdurre una valutazione "a punteggio" o "a
 * difficolta" come nuova implementazione.</p>
 */
public interface ValutatoreAccusa {

    /**
     * @param accusa l'accusa formulata (sospettato + prove a supporto)
     * @param caso   il caso con la soluzione corretta
     * @return l'esito (successo/fallimento) con la relativa narrazione
     */
    EsitoAccusa valuta(Accusa accusa, Caso caso);
}
