package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.model.dialogo.Testimonianza;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Sospettato;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Il taccuino dell'investigatore: raccoglie indizi, testimonianze, sospettati
 * noti e appunti liberi. E la fonte di verita dello stato investigativo.
 *
 * <p>Scelte di struttura dati motivate:</p>
 * <ul>
 *   <li>gli <strong>indizi</strong> stanno in un {@link Set}: niente duplicati e
 *       {@code contains}/{@code add} in tempo costante medio (sfrutta
 *       {@code equals}/{@code hashCode} di {@link Indizio});</li>
 *   <li>le <strong>testimonianze</strong> sono in una {@link Map} indicizzata
 *       per fonte: recupero diretto di tutte le dichiarazioni di un personaggio
 *       senza scorrere l'intera collezione.</li>
 * </ul>
 */
public final class Taccuino {

    private final Set<Indizio> indizi = new LinkedHashSet<>();
    private final Map<String, List<Testimonianza>> testimonianzePerFonte = new HashMap<>();
    private final Set<Sospettato> sospettatiNoti = new LinkedHashSet<>();
    private final List<String> appunti = new ArrayList<>();

    /**
     * Registra un indizio. Grazie al {@link Set} eventuali ri-raccolte dello
     * stesso indizio non creano duplicati.
     *
     * @return {@code true} se l'indizio non era ancora presente
     */
    public boolean registra(Indizio indizio) {
        return indizi.add(Validazioni.nonNullo(indizio, "indizio"));
    }

    /** Registra una testimonianza raggruppandola per fonte. */
    public void registra(Testimonianza t) {
        Validazioni.nonNullo(t, "testimonianza");
        testimonianzePerFonte
                .computeIfAbsent(t.fonte(), fonte -> new ArrayList<>())
                .add(t);
    }

    /** Annota un sospettato (con il suo movente noto) nel taccuino. */
    public void notaSospettato(Sospettato s) {
        sospettatiNoti.add(Validazioni.nonNullo(s, "sospettato"));
    }

    /** Aggiunge una nota libera scritta dal giocatore. */
    public void aggiungiAppunto(String appunto) {
        appunti.add(Validazioni.nonVuota(appunto, "appunto"));
    }

    public boolean contiene(Indizio indizio) {
        return indizi.contains(indizio);
    }

    public Set<Indizio> getIndizi() {
        return Collections.unmodifiableSet(indizi);
    }

    public Map<String, List<Testimonianza>> getTestimonianzePerFonte() {
        return Collections.unmodifiableMap(testimonianzePerFonte);
    }

    public Set<Sospettato> getSospettatiNoti() {
        return Collections.unmodifiableSet(sospettatiNoti);
    }

    public List<String> getAppunti() {
        return Collections.unmodifiableList(appunti);
    }
}
