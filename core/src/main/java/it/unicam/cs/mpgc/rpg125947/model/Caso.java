package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Sospettato;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Definizione completa di un caso investigativo: la sua trama, le stanze, la
 * vittima e la <strong>soluzione</strong> (colpevole + indizi decisivi).
 *
 * <p>E l'aggregato dei dati statici dello scenario, caricato da una fonte
 * esterna (XML) tramite uno
 * {@link it.unicam.cs.mpgc.rpg125947.persistence.ScenarioLoader}. Tenere la
 * soluzione dentro il caso, anziche cablata nel codice, e cio che rende
 * possibile aggiungere nuovi casi senza ricompilare (Open/Closed).</p>
 */
public final class Caso {

    private final String titolo;
    private final String descrizione;
    private final String vittima;
    private final String idStanzaIniziale;
    private final Sospettato colpevole;
    private final Set<Indizio> indiziDecisivi;
    private final String soluzioneNarrativa;
    private final Map<String, Stanza> stanze = new LinkedHashMap<>();
    // Registro di tutti gli indizi del caso, indicizzati per id: permette di
    // risolvere un indizio dal suo id (es. quando una testimonianza lo rivela
    // o quando si ricarica una partita salvata).
    private final Map<String, Indizio> indiziPerId = new LinkedHashMap<>();

    public Caso(String titolo, String descrizione, String vittima, String idStanzaIniziale,
                Sospettato colpevole, Set<Indizio> indiziDecisivi, String soluzioneNarrativa) {
        this.titolo = Validazioni.nonVuota(titolo, "titolo caso");
        this.descrizione = Validazioni.nonVuota(descrizione, "descrizione caso");
        this.vittima = Validazioni.nonVuota(vittima, "vittima");
        this.idStanzaIniziale = Validazioni.nonVuota(idStanzaIniziale, "stanza iniziale");
        this.colpevole = Validazioni.nonNullo(colpevole, "colpevole");
        this.indiziDecisivi = Collections.unmodifiableSet(
                new LinkedHashSet<>(Validazioni.nonNullo(indiziDecisivi, "indizi decisivi")));
        this.soluzioneNarrativa = Validazioni.nonVuota(soluzioneNarrativa, "soluzione narrativa");
    }

    public void aggiungiStanza(Stanza stanza) {
        Validazioni.nonNullo(stanza, "stanza");
        stanze.put(stanza.getId(), stanza);
    }

    /** Registra un indizio nel catalogo del caso (indicizzato per id). */
    public void registraIndizio(Indizio indizio) {
        Validazioni.nonNullo(indizio, "indizio");
        indiziPerId.put(indizio.getId(), indizio);
    }

    /** @return l'indizio con l'id dato, se presente nel catalogo del caso */
    public Optional<Indizio> getIndizio(String id) {
        return Optional.ofNullable(indiziPerId.get(id));
    }

    public Collection<Indizio> getTuttiGliIndizi() {
        return Collections.unmodifiableCollection(indiziPerId.values());
    }

    /**
     * @param id id della stanza
     * @return la stanza richiesta
     * @throws IllegalArgumentException se non esiste una stanza con quell'id
     */
    public Stanza getStanza(String id) {
        Stanza s = stanze.get(id);
        if (s == null) {
            throw new IllegalArgumentException("Stanza inesistente: " + id);
        }
        return s;
    }

    public Stanza getStanzaIniziale() {
        return getStanza(idStanzaIniziale);
    }

    public Collection<Stanza> getStanze() {
        return Collections.unmodifiableCollection(stanze.values());
    }

    /** @return tutti i sospettati presenti nello scenario (senza duplicati) */
    public List<Sospettato> getSospettati() {
        return stanze.values().stream()
                .flatMap(s -> s.getPersonaggi().stream())
                .filter(Personaggio::isSospettato)
                .map(Sospettato.class::cast)
                .distinct()
                .toList();
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getVittima() {
        return vittima;
    }

    public Sospettato getColpevole() {
        return colpevole;
    }

    public Set<Indizio> getIndiziDecisivi() {
        return indiziDecisivi;
    }

    public String getSoluzioneNarrativa() {
        return soluzioneNarrativa;
    }
}
