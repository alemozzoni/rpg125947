package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Una sala del museo: ha uno sfondo, eventuali personaggi presenti, oggetti
 * ispezionabili (hotspot) e uscite verso le sale adiacenti.
 *
 * <p>Identita di dominio sull'{@code id}: due stanze sono la stessa stanza se
 * hanno lo stesso id ({@code equals}/{@code hashCode}). Le collezioni interne
 * sono esposte in sola lettura per preservare l'incapsulamento.</p>
 */
public final class Stanza {

    private final String id;
    private final String nome;
    private final String sfondoPath;
    private final List<Uscita> uscite = new ArrayList<>();
    private final List<Hotspot> hotspot = new ArrayList<>();
    private final List<Personaggio> personaggi = new ArrayList<>();
    // Se valorizzato, la stanza e chiusa a chiave e si apre solo quando il
    // taccuino contiene l'indizio con questo id (es. la chiave dell'ufficio).
    private String idChiaveRichiesta;

    public Stanza(String id, String nome, String sfondoPath) {
        this.id = Validazioni.nonVuota(id, "id stanza");
        this.nome = Validazioni.nonVuota(nome, "nome stanza");
        this.sfondoPath = Validazioni.nonVuota(sfondoPath, "sfondo stanza");
    }

    /** Marca la stanza come chiusa a chiave, apribile con l'indizio indicato. */
    public void richiedeChiave(String idIndizioChiave) {
        this.idChiaveRichiesta = Validazioni.nonVuota(idIndizioChiave, "indizio chiave");
    }

    public void aggiungiUscita(Uscita uscita) {
        uscite.add(Validazioni.nonNullo(uscita, "uscita"));
    }

    public void aggiungiHotspot(Hotspot h) {
        hotspot.add(Validazioni.nonNullo(h, "hotspot"));
    }

    public void aggiungiPersonaggio(Personaggio p) {
        personaggi.add(Validazioni.nonNullo(p, "personaggio"));
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSfondoPath() {
        return sfondoPath;
    }

    public boolean isChiusaAChiave() {
        return idChiaveRichiesta != null;
    }

    /** @return l'id dell'indizio-chiave che apre la stanza, se chiusa a chiave */
    public java.util.Optional<String> getIdChiaveRichiesta() {
        return java.util.Optional.ofNullable(idChiaveRichiesta);
    }

    public List<Uscita> getUscite() {
        return Collections.unmodifiableList(uscite);
    }

    public List<Hotspot> getHotspot() {
        return Collections.unmodifiableList(hotspot);
    }

    public List<Personaggio> getPersonaggi() {
        return Collections.unmodifiableList(personaggi);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stanza other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Stanza[" + id + ": " + nome + "]";
    }
}
