package pj.gob.pe.judicial.utils.plantilla;

import jakarta.xml.bind.JAXBElement;
import org.apache.commons.text.StringEscapeUtils;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Br;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.SdtBlock;
import org.docx4j.wml.SdtRun;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.UnderlineEnumeration;

import java.util.List;

/**
 * Convierte el cuerpo del .docx ya generado en HTML simple para la vista web (mismo estilo de
 * párrafo que el flujo por secciones). Conserva alineación, negrita, cursiva, subrayado,
 * tabulaciones, saltos de línea y tablas. No incluye encabezados/pies ni imágenes.
 */
public final class DocxHtmlConverter {

    private static final String ESTILO_PARRAFO = "margin-top: 1em; margin-bottom: 1em;";

    private DocxHtmlConverter() {
    }

    public static String convertir(WordprocessingMLPackage paquete) {
        StringBuilder html = new StringBuilder();
        bloques(paquete.getMainDocumentPart().getContent(), html);
        return html.toString();
    }

    private static void bloques(List<Object> contenido, StringBuilder html) {
        for (Object nodo : contenido) {
            Object o = XmlUtils.unwrap(nodo);
            if (o instanceof P p) {
                parrafo(p, html);
            } else if (o instanceof Tbl tbl) {
                tabla(tbl, html);
            } else if (o instanceof SdtBlock sdt && sdt.getSdtContent() != null) {
                bloques(sdt.getSdtContent().getContent(), html);
            }
        }
    }

    private static void parrafo(P p, StringBuilder html) {
        html.append("<p style='").append(ESTILO_PARRAFO).append(alineacion(p.getPPr())).append("'>");
        int inicio = html.length();
        inline(p.getContent(), html);
        if (html.length() == inicio) {
            html.append("&nbsp;");
        }
        html.append("</p>");
    }

    private static String alineacion(PPr pPr) {
        if (pPr == null || pPr.getJc() == null || pPr.getJc().getVal() == null) return "";
        JcEnumeration jc = pPr.getJc().getVal();
        return switch (jc) {
            case CENTER -> " text-align: center;";
            case RIGHT, END -> " text-align: right;";
            case BOTH, DISTRIBUTE -> " text-align: justify;";
            default -> "";
        };
    }

    private static void inline(List<Object> contenido, StringBuilder html) {
        for (Object nodo : contenido) {
            Object o = XmlUtils.unwrap(nodo);
            if (o instanceof R r) {
                run(r, html);
            } else if (o instanceof P) {
                // Párrafos anidados (cuadros de texto): se omiten en la vista web
            } else if (o instanceof SdtRun sdt && sdt.getSdtContent() != null) {
                inline(sdt.getSdtContent().getContent(), html);
            } else if (o instanceof ContentAccessor ca) {
                // Hipervínculos, smart tags, inserciones con control de cambios, etc.
                inline(ca.getContent(), html);
            }
        }
    }

    private static void run(R r, StringBuilder html) {
        RPr rPr = r.getRPr();
        boolean negrita = rPr != null && rPr.getB() != null && rPr.getB().isVal();
        boolean cursiva = rPr != null && rPr.getI() != null && rPr.getI().isVal();
        boolean subrayado = rPr != null && rPr.getU() != null
                && (rPr.getU().getVal() == null || rPr.getU().getVal() != UnderlineEnumeration.NONE);

        StringBuilder contenido = new StringBuilder();
        for (Object nodo : r.getContent()) {
            if (nodo instanceof JAXBElement<?> je) {
                String nombre = je.getName().getLocalPart();
                Object valor = je.getValue();
                if ("t".equals(nombre) && valor instanceof Text t) {
                    contenido.append(StringEscapeUtils.escapeHtml4(DocxPlantillaProcessor.valor(t)));
                } else if (valor instanceof R.Tab) {
                    contenido.append("&emsp;");
                } else if (valor instanceof Br) {
                    contenido.append("<br/>");
                }
            } else if (nodo instanceof Br) {
                contenido.append("<br/>");
            } else if (nodo instanceof R.Tab) {
                contenido.append("&emsp;");
            } else if (nodo instanceof Text t) {
                contenido.append(StringEscapeUtils.escapeHtml4(DocxPlantillaProcessor.valor(t)));
            }
        }
        if (contenido.isEmpty()) return;

        if (negrita) html.append("<b>");
        if (cursiva) html.append("<i>");
        if (subrayado) html.append("<u>");
        html.append(contenido);
        if (subrayado) html.append("</u>");
        if (cursiva) html.append("</i>");
        if (negrita) html.append("</b>");
    }

    private static void tabla(Tbl tbl, StringBuilder html) {
        html.append("<table style='border-collapse: collapse; width: 100%;'>");
        for (Object filaNodo : tbl.getContent()) {
            if (!(XmlUtils.unwrap(filaNodo) instanceof Tr tr)) continue;
            html.append("<tr>");
            for (Object celdaNodo : tr.getContent()) {
                if (!(XmlUtils.unwrap(celdaNodo) instanceof Tc tc)) continue;
                html.append("<td style='border: 1px solid #999; padding: 4px; vertical-align: top;'>");
                bloques(tc.getContent(), html);
                html.append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");
    }
}
