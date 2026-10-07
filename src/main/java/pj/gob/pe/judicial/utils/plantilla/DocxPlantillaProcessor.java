package pj.gob.pe.judicial.utils.plantilla;

import jakarta.xml.bind.JAXBElement;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.finders.ClassFinder;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Motor de plantillas .docx: detecta y reemplaza variables ${nombre} directamente sobre el Word
 * completo (cuerpo, encabezados y pies), conservando el formato del documento.
 *
 * A diferencia de {@code MainDocumentPart.variableReplace}, el reemplazo se hace a nivel de
 * párrafo: Word suele partir un mismo ${nombre} en varios runs (corrector ortográfico, cambios de
 * formato, historial de edición) y aquí la variable se reconoce aunque esté repartida entre varios
 * w:t. El valor se escribe en el run donde empieza la variable, con su formato. Los valores se
 * asignan vía JAXB, por lo que no hace falta escapar &amp;, &lt; ni &gt;.
 *
 * Una instancia corresponde a un documento en memoria: no es thread-safe ni reutilizable.
 */
public class DocxPlantillaProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DocxPlantillaProcessor.class);

    /** ${nombre}: letras, dígitos, punto, guion y guion bajo (admite los nombres "title.juzgado" del flujo anterior). */
    public static final Pattern PATRON_VARIABLE = Pattern.compile("\\$\\{\\s*([A-Za-z0-9_.\\-]+)\\s*\\}");

    private static final ObjectFactory WML = new ObjectFactory();

    private final WordprocessingMLPackage paquete;

    private DocxPlantillaProcessor(WordprocessingMLPackage paquete) {
        this.paquete = paquete;
    }

    /**
     * Carga el .docx. VariablePrepare une runs contiguos con el mismo formato y quita marcas del
     * corrector; es una ayuda, no un requisito, porque el reemplazo ya tolera runs partidos.
     */
    public static DocxPlantillaProcessor cargar(byte[] contenido) throws Exception {
        WordprocessingMLPackage paquete = WordprocessingMLPackage.load(new ByteArrayInputStream(contenido));
        try {
            VariablePrepare.prepare(paquete);
        } catch (Exception e) {
            logger.warn("VariablePrepare no pudo normalizar la plantilla, se continúa sin normalizar: {}", e.getMessage());
        }
        return new DocxPlantillaProcessor(paquete);
    }

    public WordprocessingMLPackage getPaquete() {
        return paquete;
    }

    public byte[] guardar() throws Exception {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        paquete.save(salida);
        return salida.toByteArray();
    }

    // ====================================================================
    // Recorrido del documento
    // ====================================================================

    /** Párrafos del cuerpo, encabezados y pies (incluye los de tablas). */
    public List<P> parrafos() {
        List<List<Object>> contenidos = new ArrayList<>();
        contenidos.add(paquete.getMainDocumentPart().getContent());
        for (Part parte : paquete.getParts().getParts().values()) {
            if (parte instanceof HeaderPart header) {
                contenidos.add(header.getContent());
            } else if (parte instanceof FooterPart footer) {
                contenidos.add(footer.getContent());
            }
        }

        List<P> parrafos = new ArrayList<>();
        for (List<Object> contenido : contenidos) {
            ClassFinder finder = new ClassFinder(P.class);
            new TraversalUtil(contenido, finder);
            for (Object o : finder.results) {
                parrafos.add((P) o);
            }
        }
        return parrafos;
    }

    /**
     * Nodos w:t del párrafo en orden. Excluye w:instrText (códigos de campo) y no entra en párrafos
     * anidados (cuadros de texto), que se recorren como párrafos propios.
     */
    public static List<Text> textos(P parrafo) {
        List<Text> textos = new ArrayList<>();
        for (Object hijo : parrafo.getContent()) {
            recolectarTextos(hijo, textos);
        }
        return textos;
    }

    private static void recolectarTextos(Object nodo, List<Text> salida) {
        Object o = XmlUtils.unwrap(nodo);

        if (o instanceof P) {
            return;
        }
        if (o instanceof R run) {
            for (Object c : run.getContent()) {
                if (c instanceof JAXBElement<?> je) {
                    if (je.getValue() instanceof Text t && "t".equals(je.getName().getLocalPart())) {
                        salida.add(t);
                    }
                } else if (c instanceof Text t) {
                    salida.add(t);
                }
            }
            return;
        }

        List<Object> hijos = TraversalUtil.getChildrenImpl(o);
        if (hijos != null) {
            for (Object h : hijos) {
                recolectarTextos(h, salida);
            }
        }
    }

    public static String textoParrafo(P parrafo) {
        StringBuilder sb = new StringBuilder();
        for (Text t : textos(parrafo)) {
            sb.append(valor(t));
        }
        return sb.toString();
    }

    // ====================================================================
    // Variables
    // ====================================================================

    /** Nombres de variables presentes en el documento, en orden de aparición y sin duplicados. */
    public Set<String> detectarVariables() {
        Set<String> nombres = new LinkedHashSet<>();
        for (P p : parrafos()) {
            String texto = textoParrafo(p);
            if (!texto.contains("${")) continue;
            Matcher m = PATRON_VARIABLE.matcher(texto);
            while (m.find()) {
                nombres.add(m.group(1));
            }
        }
        return nombres;
    }

    /** Párrafos que contienen al menos una variable cuyo nombre cumple el filtro. */
    public List<P> parrafosConVariables(Predicate<String> filtro) {
        List<P> resultado = new ArrayList<>();
        for (P p : parrafos()) {
            String texto = textoParrafo(p);
            if (!texto.contains("${")) continue;
            Matcher m = PATRON_VARIABLE.matcher(texto);
            while (m.find()) {
                if (filtro.test(m.group(1))) {
                    resultado.add(p);
                    break;
                }
            }
        }
        return resultado;
    }

    /**
     * Reemplaza las variables cuyo nombre está en el mapa; las demás quedan intactas (así se pueden
     * reemplazar en fases, por ejemplo primero SIJ/manuales y luego los bloques de la IA).
     * Los saltos de línea del valor se convierten en w:br.
     *
     * @return cantidad de ocurrencias reemplazadas
     */
    public int reemplazarVariables(Map<String, String> valores) {
        if (valores == null || valores.isEmpty()) return 0;

        Set<Text> modificados = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
        int total = 0;
        for (P p : parrafos()) {
            total += reemplazarEnParrafo(p, valores, modificados);
        }
        for (Text t : modificados) {
            convertirSaltosYTabulaciones(t);
        }
        return total;
    }

    private int reemplazarEnParrafo(P parrafo, Map<String, String> valores, Set<Text> modificados) {
        List<Text> textos = textos(parrafo);
        if (textos.isEmpty()) return 0;

        int[] inicios = new int[textos.size()];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textos.size(); i++) {
            inicios[i] = sb.length();
            sb.append(valor(textos.get(i)));
        }
        String completo = sb.toString();
        if (!completo.contains("${")) return 0;

        List<int[]> coincidencias = new ArrayList<>();
        List<String> nombres = new ArrayList<>();
        Matcher m = PATRON_VARIABLE.matcher(completo);
        while (m.find()) {
            if (valores.containsKey(m.group(1))) {
                coincidencias.add(new int[]{m.start(), m.end()});
                nombres.add(m.group(1));
            }
        }

        // De atrás hacia adelante: los offsets de las coincidencias anteriores siguen siendo válidos
        for (int k = coincidencias.size() - 1; k >= 0; k--) {
            int inicio = coincidencias.get(k)[0];
            int fin = coincidencias.get(k)[1];
            String reemplazo = sanear(valores.get(nombres.get(k)));

            int i = nodoEnPosicion(textos, inicios, inicio);
            int j = nodoEnPosicion(textos, inicios, fin - 1);

            Text ti = textos.get(i);
            String vi = valor(ti);
            int desdeI = inicio - inicios[i];

            if (i == j) {
                int hastaI = fin - inicios[i];
                asignar(ti, vi.substring(0, desdeI) + reemplazo + vi.substring(hastaI));
            } else {
                asignar(ti, vi.substring(0, desdeI) + reemplazo);
                for (int x = i + 1; x < j; x++) {
                    asignar(textos.get(x), "");
                }
                Text tj = textos.get(j);
                asignar(tj, valor(tj).substring(fin - inicios[j]));
            }
            modificados.add(ti);
        }
        return coincidencias.size();
    }

    private static int nodoEnPosicion(List<Text> textos, int[] inicios, int posicion) {
        for (int i = 0; i < textos.size(); i++) {
            int largo = valor(textos.get(i)).length();
            if (largo > 0 && posicion >= inicios[i] && posicion < inicios[i] + largo) {
                return i;
            }
        }
        throw new IllegalStateException("Posición fuera del párrafo: " + posicion);
    }

    /**
     * Divide un w:t que contiene saltos de línea o tabulaciones en varios w:t separados por w:br /
     * w:tab dentro del mismo run (así conserva el formato). Si no se ubica el run padre, se cambian
     * por espacios.
     */
    private static void convertirSaltosYTabulaciones(Text texto) {
        String v = valor(texto);
        if (v.indexOf('\n') < 0 && v.indexOf('\t') < 0) return;

        if (!(texto.getParent() instanceof R run)) {
            asignar(texto, v.replace('\n', ' ').replace('\t', ' '));
            return;
        }

        List<Object> contenido = run.getContent();
        int indice = -1;
        for (int i = 0; i < contenido.size(); i++) {
            if (XmlUtils.unwrap(contenido.get(i)) == texto) {
                indice = i;
                break;
            }
        }
        if (indice < 0) {
            asignar(texto, v.replace('\n', ' ').replace('\t', ' '));
            return;
        }

        List<Object> nuevos = new ArrayList<>();
        StringBuilder tramo = new StringBuilder();
        for (int i = 0; i <= v.length(); i++) {
            char c = i < v.length() ? v.charAt(i) : '\0';
            if (i < v.length() && c != '\n' && c != '\t') {
                tramo.append(c);
                continue;
            }
            if (!tramo.isEmpty()) {
                Text t = WML.createText();
                asignar(t, tramo.toString());
                t.setParent(run);
                nuevos.add(WML.createRT(t));
                tramo.setLength(0);
            }
            if (c == '\n') {
                Br br = WML.createBr();
                br.setParent(run);
                nuevos.add(br);
            } else if (c == '\t') {
                R.Tab tab = WML.createRTab();
                tab.setParent(run);
                nuevos.add(WML.createRTab(tab));
            }
        }
        contenido.remove(indice);
        contenido.addAll(indice, nuevos);
    }

    /** Asigna el valor y conserva espacios iniciales/finales (xml:space="preserve"). */
    public static void asignar(Text texto, String valor) {
        texto.setValue(valor);
        texto.setSpace("preserve");
    }

    public static String valor(Text texto) {
        return texto.getValue() == null ? "" : texto.getValue();
    }

    /**
     * Normaliza el valor para w:t: quita caracteres de control inválidos en XML y unifica saltos de
     * línea. Los \n y \t se conservan (luego se convierten en w:br / w:tab).
     */
    public static String sanear(String valor) {
        if (valor == null) return "";
        String v = valor.replace("\r\n", "\n").replace('\r', '\n');
        StringBuilder sb = new StringBuilder(v.length());
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (c == '\n' || c == '\t' || c >= 0x20) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
