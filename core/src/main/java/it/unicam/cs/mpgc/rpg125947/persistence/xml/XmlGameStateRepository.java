package it.unicam.cs.mpgc.rpg125947.persistence.xml;

import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Investigatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.Taccuino;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Sospettato;
import it.unicam.cs.mpgc.rpg125947.persistence.GameStateRepository;
import it.unicam.cs.mpgc.rpg125947.persistence.PersistenzaException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione XML del {@link GameStateRepository}: salva e ricarica lo stato
 * della partita su file, costruendo un albero DOM e serializzandolo con un
 * {@code Transformer} su uno stream <strong>bufferizzato</strong>
 * ({@link BufferedWriter}, pattern Decorator) entro un try-with-resources.
 *
 * <p>Sono persistiti l'investigatore, la stanza corrente, gli indizi raccolti,
 * gli appunti e i sospettati annotati. Al caricamento gli indizi (salvati per
 * id) vengono ricollegati alle entita del {@link Caso} fornito.</p>
 */
public final class XmlGameStateRepository implements GameStateRepository {

    private static final String ESTENSIONE = ".save.xml";

    private final Path cartella;

    /** Usa la cartella predefinita {@code salvataggi/} nella directory di lavoro. */
    public XmlGameStateRepository() {
        this(Path.of("salvataggi"));
    }

    public XmlGameStateRepository(Path cartella) {
        this.cartella = cartella;
    }

    @Override
    public void salva(Partita partita, String slot) {
        try {
            Files.createDirectories(cartella);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Element root = doc.createElement("partita");
            root.setAttribute("investigatore", partita.getInvestigatore().getNome());
            root.setAttribute("stanza", partita.getStanzaCorrente().getId());
            doc.appendChild(root);

            Element taccuinoEl = doc.createElement("taccuino");
            Taccuino taccuino = partita.getTaccuino();
            taccuino.getIndizi().forEach(indizio -> {
                Element e = doc.createElement("indizio");
                e.setAttribute("id", indizio.getId());
                taccuinoEl.appendChild(e);
            });
            taccuino.getSospettatiNoti().forEach(s -> {
                Element e = doc.createElement("sospettato");
                e.setAttribute("nome", s.getNome());
                taccuinoEl.appendChild(e);
            });
            taccuino.getAppunti().forEach(appunto -> {
                Element e = doc.createElement("appunto");
                e.setTextContent(appunto);
                taccuinoEl.appendChild(e);
            });
            root.appendChild(taccuinoEl);

            scrivi(doc, fileDi(slot));
        } catch (PersistenzaException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenzaException("Salvataggio fallito per lo slot '" + slot + "'", e);
        }
    }

    @Override
    public Optional<Partita> carica(String slot, Caso caso) {
        Path file = fileDi(slot);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file.toFile());
            Element root = doc.getDocumentElement();

            Partita partita = new Partita(new Investigatore(root.getAttribute("investigatore")), caso);
            partita.muoviVerso(caso.getStanza(root.getAttribute("stanza")));
            Taccuino taccuino = partita.getTaccuino();

            for (Element indizioEl : figli(root, "taccuino", "indizio")) {
                caso.getIndizio(indizioEl.getAttribute("id")).ifPresent(taccuino::registra);
            }
            for (Element sospettatoEl : figli(root, "taccuino", "sospettato")) {
                trovaSospettato(caso, sospettatoEl.getAttribute("nome")).ifPresent(taccuino::notaSospettato);
            }
            for (Element appuntoEl : figli(root, "taccuino", "appunto")) {
                String testo = appuntoEl.getTextContent().trim();
                if (!testo.isBlank()) {
                    taccuino.aggiungiAppunto(testo);
                }
            }
            return Optional.of(partita);
        } catch (Exception e) {
            throw new PersistenzaException("Caricamento fallito per lo slot '" + slot + "'", e);
        }
    }

    @Override
    public List<String> slotDisponibili() {
        if (!Files.isDirectory(cartella)) {
            return List.of();
        }
        try (var stream = Files.list(cartella)) {
            return stream
                    .map(p -> p.getFileName().toString())
                    .filter(nome -> nome.endsWith(ESTENSIONE))
                    .map(nome -> nome.substring(0, nome.length() - ESTENSIONE.length()))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile elencare i salvataggi", e);
        }
    }

    private void scrivi(Document doc, Path file) throws Exception {
        // Decorator: BufferedWriter avvolge il writer su file per una scrittura efficiente.
        try (Writer w = new BufferedWriter(Files.newBufferedWriter(file, StandardCharsets.UTF_8))) {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");          // XML leggibile/indentato
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.transform(new DOMSource(doc), new StreamResult(w));
        }
    }

    private Path fileDi(String slot) {
        return cartella.resolve(slot + ESTENSIONE);
    }

    private static Optional<Sospettato> trovaSospettato(Caso caso, String nome) {
        return caso.getSospettati().stream()
                .filter(s -> s.getNome().equals(nome))
                .findFirst();
    }

    /** Figli {@code <tag>} contenuti nel wrapper {@code <wrapper>} sotto root. */
    private static List<Element> figli(Element root, String wrapper, String tag) {
        List<Element> result = new ArrayList<>();
        NodeList wrappers = root.getElementsByTagName(wrapper);
        if (wrappers.getLength() == 0) {
            return result;
        }
        NodeList children = wrappers.item(0).getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(tag)) {
                result.add((Element) n);
            }
        }
        return result;
    }
}
