package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.model.interfaces.Ispezionabile;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;
// Attributo e nello stesso package: nessun import necessario.

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
    private final Attributo attributoRichiesto; // null: ispezione senza prova
    private final int difficolta;               // soglia della prova (0 se assente)
    private boolean giaIspezionato;

    /** Hotspot la cui ispezione non richiede alcuna prova di abilita. */
    public Hotspot(String id, String nome, String descrizione, Coordinata posizione, Indizio indizio) {
        this(id, nome, descrizione, posizione, indizio, null, 0);
    }

    /**
     * @param attributoRichiesto attributo messo alla prova per scoprire l'indizio
     *                           (se {@code null} l'ispezione riesce sempre)
     * @param difficolta         soglia della prova; rilevante solo se l'attributo e presente
     */
    public Hotspot(String id, String nome, String descrizione, Coordinata posizione,
                   Indizio indizio, Attributo attributoRichiesto, int difficolta) {
        this.id = Validazioni.nonVuota(id, "id hotspot");
        this.nome = Validazioni.nonVuota(nome, "nome hotspot");
        this.descrizione = Validazioni.nonVuota(descrizione, "descrizione hotspot");
        this.posizione = Validazioni.nonNullo(posizione, "posizione hotspot");
        this.indizio = indizio; // facoltativo
        this.attributoRichiesto = attributoRichiesto; // facoltativo
        if (attributoRichiesto != null && difficolta <= 0) {
            throw new IllegalArgumentException("Una prova richiede una difficolta positiva");
        }
        this.difficolta = difficolta;
    }

    /** @return {@code true} se scoprire l'indizio richiede di superare una prova */
    public boolean richiedeProva() {
        return attributoRichiesto != null && indizio != null;
    }

    /** @return l'attributo messo alla prova, se l'ispezione e una sfida */
    public Optional<Attributo> getAttributoRichiesto() {
        return Optional.ofNullable(attributoRichiesto);
    }

    public int getDifficolta() {
        return difficolta;
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
