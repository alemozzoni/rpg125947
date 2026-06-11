package it.unicam.cs.mpgc.rpg125947.persistence.xml;

import it.unicam.cs.mpgc.rpg125947.model.Attributo;
import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Coordinata;
import it.unicam.cs.mpgc.rpg125947.model.Direzione;
import it.unicam.cs.mpgc.rpg125947.model.Hotspot;
import it.unicam.cs.mpgc.rpg125947.model.Indizio;
import it.unicam.cs.mpgc.rpg125947.model.Stanza;
import it.unicam.cs.mpgc.rpg125947.model.TipoIndizio;
import it.unicam.cs.mpgc.rpg125947.model.Uscita;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Dialogo;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.OpzioneDialogo;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Testimonianza;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Sospettato;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Testimone;
import it.unicam.cs.mpgc.rpg125947.persistence.PersistenzaException;
import it.unicam.cs.mpgc.rpg125947.persistence.ScenarioLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link ScenarioLoader} basato su XML che applica direttamente i capitoli XML
 * del corso: il documento {@code caso.xml} viene letto in <strong>DOM</strong>,
 * <strong>validato</strong> contro {@code scenario.xsd} durante il parsing e
 * navigato con <strong>XPath</strong> per costruire l'aggregato {@link Caso}.
 *
 * <p>Un caso non conforme allo schema viene rifiutato: l'{@link ErrorHandler}
 * trasforma gli errori di validazione in eccezioni, cosi il dominio riceve solo
 * dati ben formati e validi.</p>
 */
public final class CaricatoreCasoXml implements ScenarioLoader {

    private static final String RISORSA_XSD = "/it/unicam/cs/mpgc/rpg125947/scenario/scenario.xsd";
    private static final String RISORSA_XML = "/it/unicam/cs/mpgc/rpg125947/scenario/caso.xml";

    private final String pathXml;
    private final String pathXsd;

    /** Carica lo scenario predefinito incluso tra le risorse. */
    public CaricatoreCasoXml() {
        this(RISORSA_XML, RISORSA_XSD);
    }

    /** Permette di caricare uno scenario alternativo (usato anche nei test). */
    public CaricatoreCasoXml(String pathXml, String pathXsd) {
        this.pathXml = pathXml;
        this.pathXsd = pathXsd;
    }

    @Override
    public Caso carica() {
        try {
            Document doc = parseValidato();
            return costruisciCaso(doc);
        } catch (PersistenzaException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenzaException("Impossibile caricare lo scenario: " + e.getMessage(), e);
        }
    }

    /** Parsa il documento in DOM applicando la validazione XSD. */
    private Document parseValidato() throws Exception {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try (InputStream xsd = risorsa(pathXsd)) {
            schema = sf.newSchema(new StreamSource(xsd));
        }
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);          // richiesto per usare correttamente lo schema
        f.setSchema(schema);                // il caso e VALIDO solo se rispetta lo schema
        DocumentBuilder builder = f.newDocumentBuilder();
        builder.setErrorHandler(new ErrorHandler() {
            @Override public void warning(SAXParseException e) { /* ignorato */ }
            @Override public void error(SAXParseException e) throws SAXException { throw e; }
            @Override public void fatalError(SAXParseException e) throws SAXException { throw e; }
        });
        try (InputStream xml = risorsa(pathXml)) {
            return builder.parse(xml);       // DOM (internamente usa SAX)
        }
    }

    /** Naviga il DOM con XPath e ricostruisce l'aggregato di dominio. */
    private Caso costruisciCaso(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        // 1. Catalogo degli indizi (per id), riferito da hotspot, testimonianze e soluzione.
        Map<String, Indizio> indizi = caricaIndizi(doc, xpath);

        // 2. Stanze (con hotspot e personaggi); raccolgo i sospettati per nome.
        List<Stanza> stanze = new ArrayList<>();
        Map<String, Sospettato> sospettatiPerNome = new LinkedHashMap<>();
        NodeList nodiStanza = (NodeList) xpath.evaluate("/caso/stanze/stanza", doc, XPathConstants.NODESET);
        for (Element stanzaEl : elementi(nodiStanza)) {
            stanze.add(caricaStanza(stanzaEl, indizi, sospettatiPerNome));
        }

        // 3. Soluzione: colpevole, indizi decisivi, narrazione.
        Element sol = (Element) xpath.evaluate("/caso/soluzione", doc, XPathConstants.NODE);
        String nomeColpevole = sol.getAttribute("colpevole");
        Sospettato colpevole = sospettatiPerNome.get(nomeColpevole);
        if (colpevole == null) {
            throw new PersistenzaException("Colpevole indicato ma non presente tra i sospettati: " + nomeColpevole);
        }
        Set<Indizio> decisivi = new LinkedHashSet<>();
        for (Element dec : figli(sol, "indizioDecisivo")) {
            decisivi.add(risolviIndizio(indizi, dec.getAttribute("ref")));
        }
        String narrazione = testo(primoFiglio(sol, "narrazione"));

        // 4. Dati di testata e assemblaggio del Caso.
        Element radice = doc.getDocumentElement();
        Caso caso = new Caso(
                radice.getAttribute("titolo"),
                testo((Element) xpath.evaluate("/caso/descrizione", doc, XPathConstants.NODE)),
                testo((Element) xpath.evaluate("/caso/vittima", doc, XPathConstants.NODE)),
                radice.getAttribute("stanzaIniziale"),
                colpevole, decisivi, narrazione);
        indizi.values().forEach(caso::registraIndizio);
        stanze.forEach(caso::aggiungiStanza);
        return caso;
    }

    private Map<String, Indizio> caricaIndizi(Document doc, XPath xpath) throws Exception {
        Map<String, Indizio> indizi = new LinkedHashMap<>();
        NodeList nodi = (NodeList) xpath.evaluate("/caso/indizi/indizio", doc, XPathConstants.NODESET);
        for (Element el : elementi(nodi)) {
            Indizio indizio = new Indizio(
                    el.getAttribute("id"),
                    el.getAttribute("nome"),
                    testo(el),
                    TipoIndizio.valueOf(el.getAttribute("tipo")));
            indizi.put(indizio.getId(), indizio);
        }
        return indizi;
    }

    private Stanza caricaStanza(Element stanzaEl, Map<String, Indizio> indizi,
                                Map<String, Sospettato> sospettatiPerNome) {
        Stanza stanza = new Stanza(
                stanzaEl.getAttribute("id"),
                stanzaEl.getAttribute("nome"),
                stanzaEl.getAttribute("sfondo"));

        Element chiave = primoFiglio(stanzaEl, "chiave");
        if (chiave != null) {
            stanza.richiedeChiave(chiave.getAttribute("indizio"));
        }
        for (Element u : figliNidificati(stanzaEl, "uscite", "uscita")) {
            stanza.aggiungiUscita(new Uscita(
                    u.getAttribute("verso"),
                    u.getAttribute("etichetta"),
                    Direzione.valueOf(u.getAttribute("direzione"))));
        }
        for (Element h : figliNidificati(stanzaEl, "hotspots", "hotspot")) {
            Indizio indizio = opzionale(h, "indizio") == null ? null : risolviIndizio(indizi, h.getAttribute("indizio"));
            stanza.aggiungiHotspot(new Hotspot(
                    h.getAttribute("id"),
                    h.getAttribute("nome"),
                    testo(h),
                    coordinata(h),
                    indizio,
                    attributoOpzionale(h),
                    difficoltaOpzionale(h)));
        }
        for (Element p : figliNidificati(stanzaEl, "personaggi", "personaggio")) {
            Personaggio personaggio = caricaPersonaggio(p);
            stanza.aggiungiPersonaggio(personaggio);
            if (personaggio instanceof Sospettato s) {
                sospettatiPerNome.put(s.getNome(), s);
            }
        }
        return stanza;
    }

    private Personaggio caricaPersonaggio(Element p) {
        String nome = p.getAttribute("nome");
        Dialogo dialogo = caricaDialogo(primoFiglio(p, "dialogo"), nome);
        boolean sospettato = Boolean.parseBoolean(p.getAttribute("sospettato"));
        double scala = scala(p);
        if (sospettato) {
            return new Sospettato(nome, p.getAttribute("ruolo"), p.getAttribute("sprite"),
                    coordinata(p), dialogo, p.getAttribute("movente"), p.getAttribute("alibi"), scala);
        }
        return new Testimone(nome, p.getAttribute("ruolo"), p.getAttribute("sprite"),
                coordinata(p), dialogo, scala);
    }

    /** Fattore di scala dello sprite; assente nello XML = 1.0 (standard). */
    private double scala(Element el) {
        String v = opzionale(el, "scala");
        return v == null ? 1.0 : Double.parseDouble(v);
    }

    private Dialogo caricaDialogo(Element dialogoEl, String fonte) {
        List<OpzioneDialogo> opzioni = new ArrayList<>();
        for (Element opz : figli(dialogoEl, "opzione")) {
            Element t = primoFiglio(opz, "testimonianza");
            Testimonianza testimonianza = new Testimonianza(fonte, testo(t), opzionale(t, "indizio"));
            opzioni.add(new OpzioneDialogo(opz.getAttribute("domanda"), testimonianza,
                    attributoOpzionale(opz), difficoltaOpzionale(opz), stileOpzionale(opz)));
        }
        return new Dialogo(dialogoEl.getAttribute("battutaIniziale"), opzioni);
    }

    // ===== Utility DOM =====

    private Indizio risolviIndizio(Map<String, Indizio> indizi, String id) {
        Indizio indizio = indizi.get(id);
        if (indizio == null) {
            throw new PersistenzaException("Riferimento a indizio inesistente: " + id);
        }
        return indizio;
    }

    private Coordinata coordinata(Element el) {
        return new Coordinata(
                Double.parseDouble(el.getAttribute("x")),
                Double.parseDouble(el.getAttribute("y")));
    }

    /** Attributo della prova di abilita; assente nello XML = nessuna prova. */
    private static Attributo attributoOpzionale(Element el) {
        String v = opzionale(el, "attributo");
        return v == null ? null : Attributo.valueOf(v);
    }

    /** Stile investigativo a cui la domanda e riservata; assente = domanda universale. */
    private static Attributo stileOpzionale(Element el) {
        String v = opzionale(el, "stile");
        return v == null ? null : Attributo.valueOf(v);
    }

    /** Difficolta della prova; assente = 0 (nessuna prova). */
    private static int difficoltaOpzionale(Element el) {
        String v = opzionale(el, "difficolta");
        return v == null ? 0 : Integer.parseInt(v);
    }

    private static InputStream risorsa(String path) {
        InputStream in = CaricatoreCasoXml.class.getResourceAsStream(path);
        if (in == null) {
            throw new PersistenzaException("Risorsa di scenario non trovata nel classpath: " + path);
        }
        return in;
    }

    private static String opzionale(Element el, String attr) {
        String v = el.getAttribute(attr);
        return v.isBlank() ? null : v;
    }

    private static String testo(Element el) {
        return el.getTextContent().trim();
    }

    /** Elementi figli diretti con un dato nome. */
    private static List<Element> figli(Element parent, String tag) {
        List<Element> result = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(tag)) {
                result.add((Element) n);
            }
        }
        return result;
    }

    /** Figli del tipo {@code figlio} contenuti dentro un wrapper opzionale. */
    private static List<Element> figliNidificati(Element parent, String wrapper, String figlio) {
        Element w = primoFiglio(parent, wrapper);
        return w == null ? List.of() : figli(w, figlio);
    }

    private static Element primoFiglio(Element parent, String tag) {
        List<Element> f = figli(parent, tag);
        return f.isEmpty() ? null : f.get(0);
    }

    private static List<Element> elementi(NodeList nodi) {
        List<Element> result = new ArrayList<>();
        for (int i = 0; i < nodi.getLength(); i++) {
            result.add((Element) nodi.item(i));
        }
        return result;
    }
}
