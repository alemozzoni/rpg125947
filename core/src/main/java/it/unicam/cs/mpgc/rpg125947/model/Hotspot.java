package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.model.interfaces.Ispezionabile;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Optional;

/**
 * Area cliccabile della scena (un oggetto, una teca, un documento) che il
 * giocatore puo ispezionare. Implementa {@link Ispezionabile}.
 *
 * <p>Un hotspot puo rivelare un {@link Indizio} oppure essere puramente
 * descrittivo (nessun indizio associato). Tiene traccia di essere gia stato
 * ispezionato: questo e l'unico stato mutabile del dominio statico dello
 * scenario, motivo per cui l'ispezione e modellata come comportamento e non
 * come dato.</p>
 */
public final class Hotspot implements Ispezionabile {

    private final String id;
    private final String nome;
    private final String descrizione;
    private final Coordinata posizione;
    private final Indizio indizio; // puo essere null: hotspot solo descrittivo
    private boolean giaIspezionato;

    public Hotspot(String id, String nome, String descrizione, Coordinata posizione, Indizio indizio) {
        this.id = Validazioni.nonVuota(id, "id hotspot");
        this.nome = Validazioni.nonVuota(nome, "nome hotspot");
        this.descrizione = Validazioni.nonVuota(descrizione, "descrizione hotspot");
        this.posizione = Validazioni.nonNullo(posizione, "posizione hotspot");
        this.indizio = indizio; // facoltativo
    }

    @Override
    public Optional<Indizio> ispeziona() {
        giaIspezionato = true;
        return Optional.ofNullable(indizio);
    }

    @Override
    public boolean giaIspezionato() {
        return giaIspezionato;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Coordinata getPosizione() {
        return posizione;
    }
}
