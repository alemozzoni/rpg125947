package it.unicam.cs.mpgc.rpg125947.model;

/**
 * Posizione relativa di uno sprite o di un hotspot all'interno della scena,
 * espressa come frazione delle dimensioni (0.0 = bordo sinistro/alto,
 * 1.0 = bordo destro/basso).
 *
 * <p>Usare coordinate <em>relative</em> e una scelta di dominio: rende il
 * posizionamento indipendente dalla risoluzione e permette al front-end di
 * collocare gli elementi tramite binding responsivo, senza che il core
 * conosca i pixel reali della finestra.</p>
 *
 * @param x frazione orizzontale, in [0,1]
 * @param y frazione verticale, in [0,1]
 */
public record Coordinata(double x, double y) {

    public Coordinata {
        if (x < 0 || x > 1 || y < 0 || y > 1) {
            throw new IllegalArgumentException("Coordinate fuori dall'intervallo [0,1]: (" + x + "," + y + ")");
        }
    }
}
