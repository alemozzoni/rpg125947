package it.unicam.cs.mpgc.rpg125947.model.interfaces;

import it.unicam.cs.mpgc.rpg125947.model.Attributo;
import it.unicam.cs.mpgc.rpg125947.model.prova.EsitoProva;

/**
 * Strategia di risoluzione di una prova di abilita (skill check).
 *
 * <p>Astrarre la regola (pattern <em>Strategy</em>, come per
 * {@link ValutatoreAccusa}) disaccoppia il
 * {@link it.unicam.cs.mpgc.rpg125947.logic.MotorePartita} dal <em>come</em> si
 * decide l'esito: in gioco si usa un tiro di dado, nei test si iniettano esiti
 * deterministici tramite le factory {@link #sempreSuperata()} e
 * {@link #sempreFallita()}.</p>
 */
@FunctionalInterface
public interface RisolutoreProva {

    /**
     * Risolve una prova confrontando il valore di attributo con la difficolta.
     *
     * @param attributo       caratteristica messa alla prova
     * @param valoreAttributo valore dell'attributo dell'investigatore
     * @param difficolta      soglia da raggiungere
     * @return l'esito della prova
     */
    EsitoProva tira(Attributo attributo, int valoreAttributo, int difficolta);

    /** @return un risolutore deterministico che supera sempre la prova (per i test) */
    static RisolutoreProva sempreSuperata() {
        return (attributo, valore, difficolta) ->
                new EsitoProva(attributo, difficolta, valore, difficolta, true);
    }

    /** @return un risolutore deterministico che fallisce sempre la prova (per i test) */
    static RisolutoreProva sempreFallita() {
        return (attributo, valore, difficolta) ->
                new EsitoProva(attributo, 0, valore, difficolta, valore >= difficolta);
    }
}
