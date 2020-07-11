/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportes;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import javax.faces.context.FacesContext;

/**
 *
 * @author Emilio
 */
public class PieDePaginaEvent extends PdfPageEventHelper {

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        String usuario, tipo;
        try {
            usuario = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario").toString();
            tipo = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("tipo").toString();
        } catch (NullPointerException e) {
            usuario = "Sin usuario";
            tipo = "impresión";
        }
        Funciones funcion = new Funciones();
        Font fuente = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
        Phrase frase = new Phrase();
        String pie = "Fecha y hora de " + tipo + ": " + funcion.fechaReporte() + "                          Usuario: " + usuario;
        frase.add(new Chunk(pie, fuente));
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, frase, 300, 15, 0);
    }

}
