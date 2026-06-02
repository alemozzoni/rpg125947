package it.unicam.cs.mpgc.rpg125947.logic;

import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Indizio;
import it.unicam.cs.mpgc.rpg125947.model.accusa.Accusa;
import it.unicam.cs.mpgc.rpg125947.model.accusa.EsitoAccusa;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.ValutatoreAccusa;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementazione standard della regola di vittoria: l'accusa ha successo se e
 * solo se l'accusato e davvero il colpevole <strong>e</strong> tra le prove
 * fornite sono presenti <strong>tutti</strong> gli indizi decisivi del caso.
 *
 * <p>La verifica delle prove e espressa in modo funzionale con uno
 * {@link java.util.stream.Stream} ({@code allMatch}), che rende la regola
 * concisa e leggibile.</p>
 */
public final class ValutatoreAccusaStandard implements ValutatoreAccusa {

    @Override
    public EsitoAccusa valuta(Accusa accusa, Caso caso) {
        boolean colpevoleCorretto = accusa.accusato().equals(caso.getColpevole());

        // Indizi decisivi non presentati dal giocatore (in ordine, per il messaggio).
        Set<Indizio> mancanti = caso.getIndiziDecisivi().stream()
                .filter(indizio -> !accusa.proveFornite().contains(indizio))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (colpevoleCorretto && mancanti.isEmpty()) {
            return EsitoAccusa.successo(caso.getSoluzioneNarrativa());
        }
        return EsitoAccusa.fallimento(componiFallimento(accusa, colpevoleCorretto, mancanti));
    }

    private String componiFallimento(Accusa accusa, boolean colpevoleCorretto, Set<Indizio> mancanti) {
        if (!colpevoleCorretto) {
            return "L'accusa contro " + accusa.accusato().getNome()
                    + " non regge: le prove non conducono a questa persona. "
                    + "Il vero colpevole e ancora libero. Riprendi le indagini.";
        }
        String nomiMancanti = mancanti.stream()
                .map(Indizio::getNome)
                .collect(Collectors.joining(", "));
        return "Hai individuato la persona giusta, ma senza prove decisive l'accusa crolla in tribunale. "
                + "Mancano all'appello: " + nomiMancanti + ".";
    }
}
