/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportes;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import modelos.Factura;
import modelos.ReciboC;

/**
 *
 * @author IT Acer
 */
public class ImprimirRecibo implements Serializable {

    public String getPath() { //para obtener toda la direccion url (localhost/AltaIdea/)
        try {
            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
                    .getExternalContext().getContext();
            return ctx.getRealPath("/"); //quiere decir que desde la carpeta raiz tomara la direccion
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "getPath() " + e.getLocalizedMessage(), ""));
        }
        return null;
    }

    public String decimal(Double doub) {
        String formato;
        if (doub != null) {
            DecimalFormat format = new DecimalFormat("##,###,##0.00");
            formato = format.format(doub);
        } else {
            formato = "0.00";
        }
        return formato;
    }

    public String formatoFecha(Date date) {
        String fecha;
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                fecha = sdf.format(date);
            } catch (Exception e) {
                fecha = "";
            }
        } else {
            fecha = "---";
        }
        return fecha;
    }

    public Image agregarImagen(String ruta) {
        Image image = null;
        try {
            String rut = getPath() + "resources/logos/" + ruta;
            System.out.println("Ruta de la imagen: " + rut);
            image = Image.getInstance(rut);
            float alto, ancho, w, h, r;
            alto = image.getHeight();
            ancho = image.getWidth();
            System.out.println("Ancho: " + image.getWidth());
            System.out.println("alto: " + image.getHeight());
            if (alto >= ancho) {
                h = 50;
                r = alto / ancho;
                w = h / r;
                float margen = (50 - w) / 2;
                image.setAbsolutePosition(70 + margen, 680);
            } else {
                w = 70;
                r = ancho / alto;
                h = w / r;
                float margen = (70 - h) / 2;
                image.setAbsolutePosition(70, 680 + margen);
            }
            image.setAlignment(Element.ALIGN_CENTER);
            image.scaleAbsolute(w, h);

        } catch (BadElementException ex) {
            Logger.getLogger(ImprimirRecibo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImprimirRecibo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

    public String formatRCE(Integer doub) {
        String formato;
        if (doub != null) {
            DecimalFormat format = new DecimalFormat("000000");
            formato = format.format(doub);
        } else {
            formato = "000";
        }
        return formato;
    }

    public Boolean printLista(List<Factura> lista, ReciboC recibo) {
        Boolean ok;
        String cliente = recibo.getNombreCliente();
        System.out.println("Cliente: " + cliente);
        FileOutputStream archivo = null;
        int tipo = 0;
        String pago = "";
        Boolean e = false, ch1 = false, ch2 = false, t = false, d = false;
        if (recibo.getEfectivo() != null && recibo.getEfectivo() != 0) {
            tipo++;
            pago = "Efectivo";
            e = true;
        }
        if (recibo.getMontoCheque1() != null && recibo.getMontoCheque1() != 0) {
            tipo++;
            pago = "Cheque";
            ch1 = true;
        }
        if (recibo.getMontoDeposito() != null && recibo.getMontoDeposito() != 0) {
            tipo++;
            pago = "Depósito";
            d = true;
        }
        if (recibo.getMontoCheque2() != null && recibo.getMontoCheque2() != 0) {
            tipo++;
            if (ch1) {
                pago = "Cheques";
            } else {
                pago = "Cheque";
            }
            ch2 = true;
        }
        if (recibo.getMontoTC() != null && recibo.getMontoTC() != 0) {
            tipo++;
            pago = "Tarjeta";
            t = true;
        }
        if (tipo == 2 && ch1 && ch2) {
            pago = "Cheques";
        } else if (tipo > 1) {
            pago = "Varios";
        }
        try {
            String ruta = getPath() + "resources/archivos/";
            String pdf = ruta + "RCE-" + formatRCE(recibo.getNumRecibo()) + ".pdf";
            System.out.println("Ruta: " + pdf);
            archivo = new FileOutputStream(pdf);
            Document documento = new Document();
            documento.addCreationDate();
            documento.addHeader("Recibo de caja", "Recibo de caja");
            documento.addAuthor("Tihsa");
            documento.addTitle("Recibo de caja");
            if (recibo.getTipoRecibo() != null && recibo.getTipoRecibo()) {
                Rectangle pageSize = new Rectangle(590f, 390f);
                documento.setPageSize(pageSize);
            } else {
                documento.setPageSize(PageSize.LETTER);
            }
            documento.setMargins(25f, 25f, 25f, 25f);
            //PdfWriter.getInstance(documento, archivo);
            PdfWriter writer = PdfWriter.getInstance(documento, archivo);
            PieDePaginaEvent footer = new PieDePaginaEvent();
            writer.setPageEvent(footer);
            documento.open();
            try {
                Image image = Image.getInstance(getPath() + "resources/img/tihsa.png");
                image.setAlignment(Element.ALIGN_CENTER);
                image.scaleAbsolute(100f, 40f);
                if (recibo.getTipoRecibo() != null && recibo.getTipoRecibo()) {
                    image.setAbsolutePosition(50, 320);
                } else {
                    image.setAbsolutePosition(50, 720);
                }
                documento.add(image);
            } catch (BadElementException | IOException ex) {
                Logger.getLogger(ImprimirRecibo.class.getName()).log(Level.SEVERE, null, ex);
            }
            BaseColor azul = new BaseColor(2, 53, 94);
            Font fontBold = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
            fontBold.setColor(azul);
            Font fuenteD;
            fuenteD = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            fuenteD.setColor(azul);
            Paragraph parrafo = new Paragraph();
            parrafo.add(new Chunk("Tienda Industrial de Herramientas, S.A.", fontBold));
            parrafo.add(new Chunk("\n3a. calle 4-17 zona 9, Ciudad de Guatemala", fuenteD));
            parrafo.add(new Chunk("\nNIT: 40816389", fuenteD));
            parrafo.add(new Chunk("\n\nRECIBO DE CAJA     No. RCE-" + formatRCE(recibo.getNumRecibo()) + "\n", fontBold));
            parrafo.setAlignment(Element.ALIGN_CENTER);
            documento.add(parrafo);

            parrafo = new Paragraph();
            parrafo.add(new Chunk("\n", fontBold));
            documento.add(parrafo);

            PdfPTable tablaPdf2 = new PdfPTable(6);
            tablaPdf2.setWidthPercentage(100);
            tablaPdf2.setTotalWidth(500);
            tablaPdf2.setLockedWidth(true);
            float[] anchoColumnas2 = {80, 80, 70, 80, 89, 90};
            tablaPdf2.setWidths(anchoColumnas2);
            tablaPdf2.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell cell2;
            Font fuente2;
            Font fuente1;

            Chunk texto2;

            fuente2 = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
            fuente1 = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

            cell2 = new PdfPCell();
            texto2 = new Chunk("Nombre:", fuente2);
            cell2.addElement(texto2);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            //cell2.setCellEvent(new BordeRedondo());
            texto2 = new Chunk(cliente, fuente1);
            cell2.addElement(texto2);
            cell2.setUseAscender(true);
            cell2.setColspan(5);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            cell2.setBorder(Rectangle.NO_BORDER);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            texto2 = new Chunk("Código cliente:", fuente2);
            cell2.addElement(texto2);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            texto2 = new Chunk(recibo.getCodCliente() + " ", fuente1);
            cell2.addElement(texto2);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            texto2 = new Chunk("NIT:", fuente2);
            cell2.addElement(texto2);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            texto2 = new Chunk(recibo.getNit() + " ", fuente1);
            cell2.addElement(texto2);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            texto2 = new Chunk("Recibo No.:", fuente2);
            cell2.addElement(texto2);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            parrafo = new Paragraph();
            parrafo.add(new Chunk("RCE-" + formatRCE(recibo.getNumRecibo()), fuente1));
            parrafo.setAlignment(Element.ALIGN_LEFT);
            parrafo.setIndentationRight(8f);
            cell2.addElement(parrafo);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            texto2 = new Chunk("Monto del recibo:", fuente2);
            cell2.addElement(texto2);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            parrafo = new Paragraph();
            parrafo.add(new Chunk("Q " + decimal(recibo.getMontoRecibo()), fuente1));
            parrafo.setIndentationRight(8f);
            cell2.addElement(parrafo);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            texto2 = new Chunk("Forma de pago:", fuente2);
            cell2.addElement(texto2);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            parrafo = new Paragraph();
            parrafo.add(new Chunk(pago, fuente1));
            parrafo.setIndentationRight(8f);
            cell2.addElement(parrafo);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            texto2 = new Chunk("Fecha de emisión:", fuente2);
            cell2.addElement(texto2);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaPdf2.addCell(cell2);

            cell2 = new PdfPCell();
            parrafo = new Paragraph();
            parrafo.add(new Chunk(formatoFecha(recibo.getFecha()), fuente1));
            parrafo.setAlignment(Element.ALIGN_LEFT);
            parrafo.setIndentationRight(8f);
            cell2.addElement(parrafo);
            cell2.setBorder(0);
            cell2.setUseAscender(true);
            cell2.setPaddingBottom(5f);
            cell2.setPaddingTop(5f);
            tablaPdf2.addCell(cell2);
            
            if (recibo.getTipoRecibo()) {
                cell2 = new PdfPCell();
                texto2 = new Chunk("Observaciones:", fuente2);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk(recibo.getConcepto(), fuente1));
                parrafo.setIndentationRight(8f);
                cell2.addElement(parrafo);
                cell2.setBorder(0);
                cell2.setColspan(3);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                texto2 = new Chunk("Número de orden:", fuente2);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk(recibo.getNumOrden() != null ? recibo.getNumOrden()+"":"", fuente1));
                parrafo.setAlignment(Element.ALIGN_LEFT);
                parrafo.setIndentationRight(8f);
                cell2.addElement(parrafo);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);
            }
            
            if (e && tipo == 1) {
                
            } else {
                cell2 = new PdfPCell();
                texto2 = new Chunk("DESGLOSE DE PAGOS", fuente2);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setColspan(6);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPdf2.addCell(cell2);
            }
            if (e && tipo > 1) {
                cell2 = new PdfPCell();
                texto2 = new Chunk("Monto Efectivo:", fuente2);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("Q " + decimal(recibo.getEfectivo()), fuente1));
                parrafo.setIndentationRight(8f);
                cell2.addElement(parrafo);
                cell2.setColspan(5);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPdf2.addCell(cell2);
            }
            if (t) {
                cell2 = new PdfPCell();
                texto2 = new Chunk("Monto Tarjeta:", fuente2);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("Q " + decimal(recibo.getMontoTC()), fuente1));
                parrafo.setIndentationRight(8f);
                cell2.addElement(parrafo);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                texto2 = new Chunk("Tipo:", fuente2);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                texto2 = new Chunk(recibo.getTipoTC().toString(), fuente1);
                cell2.addElement(texto2);
                cell2.setColspan(3);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);
            }
            if (d) {
                cell2 = new PdfPCell();
                texto2 = new Chunk("Monto Depósito:", fuente2);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("Q " + decimal(recibo.getMontoDeposito()), fuente1));
                parrafo.setIndentationRight(8f);
                cell2.addElement(parrafo);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                texto2 = new Chunk("Boleta:", fuente2);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                texto2 = new Chunk(recibo.getNumBoleta(), fuente1);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                texto2 = new Chunk("Banco:", fuente2);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);

                cell2 = new PdfPCell();
                texto2 = new Chunk(recibo.getNombreBancoD(), fuente1);
                cell2.addElement(texto2);
                cell2.setBorder(0);
                cell2.setUseAscender(true);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingTop(5f);
                tablaPdf2.addCell(cell2);
            }
            
            documento.add(tablaPdf2);
            if (ch1 || ch2) {
                PdfPTable tablaCheque = new PdfPTable(8);
                tablaCheque.setWidthPercentage(100);
                tablaCheque.setTotalWidth(500);
                tablaCheque.setLockedWidth(true);
                float[] anchoColumnas3 = {80, 62, 63, 62, 40, 91, 40, 62};
                tablaCheque.setWidths(anchoColumnas3);
                tablaCheque.setHorizontalAlignment(Element.ALIGN_CENTER);

                if (ch1) {
                    cell2 = new PdfPCell();
                    texto2 = new Chunk("Monto Cheque" + (ch2 ? " 1:" : ":"), fuente2);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk("Q " + decimal(recibo.getMontoCheque1()), fuente1));
                    parrafo.setIndentationRight(8f);
                    cell2.addElement(parrafo);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    texto2 = new Chunk("No. Cheque:", fuente2);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    texto2 = new Chunk(recibo.getNumCheque1().toString(), fuente1);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    texto2 = new Chunk("Banco:", fuente2);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    texto2 = new Chunk(recibo.getNombreBanco1(), fuente1);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    texto2 = new Chunk("Fecha:", fuente2);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk(formatoFecha(recibo.getFechaCheque1()), fuente1));
                    parrafo.setAlignment(Element.ALIGN_RIGHT);
                    parrafo.setIndentationRight(8f);
                    cell2.addElement(parrafo);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);
                }
                if (ch2) {
                    cell2 = new PdfPCell();
                    texto2 = new Chunk("Monto Cheque" + (ch1 ? " 2:" : ":"), fuente2);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk("Q " + decimal(recibo.getMontoCheque2()), fuente1));
                    parrafo.setIndentationRight(8f);
                    cell2.addElement(parrafo);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    texto2 = new Chunk("No. Cheque:", fuente2);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    texto2 = new Chunk(recibo.getNumCheque2().toString(), fuente1);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    texto2 = new Chunk("Banco:", fuente2);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    texto2 = new Chunk(recibo.getNombreBanco2(), fuente1);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    texto2 = new Chunk("Fecha:", fuente2);
                    cell2.addElement(texto2);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaCheque.addCell(cell2);

                    cell2 = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk(formatoFecha(recibo.getFechaCheque2()), fuente1));
                    parrafo.setAlignment(Element.ALIGN_RIGHT);
                    parrafo.setIndentationRight(8f);
                    cell2.addElement(parrafo);
                    cell2.setBorder(0);
                    cell2.setUseAscender(true);
                    cell2.setPaddingBottom(5f);
                    cell2.setPaddingTop(5f);
                    tablaCheque.addCell(cell2);
                }
                documento.add(tablaCheque);
            }
            if (tipo == 1) {
                parrafo = new Paragraph();
                parrafo.add(new Chunk("\n", fontBold));
                parrafo.setAlignment(Element.ALIGN_CENTER);
                documento.add(parrafo);
            }
            if (!recibo.getTipoRecibo()) {
                PdfPTable tablaPdf = new PdfPTable(7);
                tablaPdf.setWidthPercentage(100);
                tablaPdf.setTotalWidth(490);
                tablaPdf.setLockedWidth(true);
                float[] anchoColumnas = {25, 75, 85, 75, 75, 75, 75};
                tablaPdf.setWidths(anchoColumnas);
                tablaPdf.setHorizontalAlignment(Element.ALIGN_CENTER);
                PdfPCell cell;
                BaseColor colorFondo = new BaseColor(230, 230, 230);
                cell = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("DETALLE DE ABONOS", fuente2));
                parrafo.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(parrafo);
                cell.setUseAscender(true);
                cell.setPaddingBottom(5f);
                cell.setPaddingTop(5f);
                cell.setColspan(7);
                cell.setBackgroundColor(colorFondo);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPdf.addCell(cell);

                cell = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("No.", fuente2));
                parrafo.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(parrafo);
                cell.setUseAscender(true);
                cell.setPaddingBottom(5f);
                cell.setPaddingTop(5f);
                cell.setBackgroundColor(colorFondo);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPdf.addCell(cell);

                cell = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("Serie", fuente2));
                parrafo.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(parrafo);
                cell.setUseAscender(true);
                cell.setPaddingBottom(5f);
                cell.setPaddingTop(5f);
                cell.setBackgroundColor(colorFondo);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPdf.addCell(cell);

                cell = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("Factura/DTE", fuente2));
                parrafo.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(parrafo);
                cell.setUseAscender(true);
                cell.setPaddingBottom(5f);
                cell.setPaddingTop(5f);
                cell.setBackgroundColor(colorFondo);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPdf.addCell(cell);

                cell = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("Fecha Fact.", fuente2));
                parrafo.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(parrafo);
                cell.setUseAscender(true);
                cell.setPaddingBottom(5f);
                cell.setPaddingTop(5f);
                cell.setBackgroundColor(colorFondo);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPdf.addCell(cell);

                cell = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("Saldo ant.", fuente2));
                parrafo.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(parrafo);
                cell.setUseAscender(true);
                cell.setPaddingBottom(5f);
                cell.setPaddingTop(5f);
                cell.setBackgroundColor(colorFondo);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPdf.addCell(cell);

                cell = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("Abono", fuente2));
                parrafo.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(parrafo);
                cell.setUseAscender(true);
                cell.setPaddingBottom(5f);
                cell.setPaddingTop(5f);
                cell.setBackgroundColor(colorFondo);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPdf.addCell(cell);

                cell = new PdfPCell();
                parrafo = new Paragraph();
                parrafo.add(new Chunk("Nuevo Saldo", fuente2));
                parrafo.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(parrafo);
                cell.setUseAscender(true);
                cell.setPaddingBottom(5f);
                cell.setPaddingTop(5f);
                cell.setBackgroundColor(colorFondo);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPdf.addCell(cell);
                tablaPdf.completeRow();

                for (int i = 0; i < lista.size(); i++) {
                    cell = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk("" + (i + 1), fuente1));
                    parrafo.setAlignment(Element.ALIGN_CENTER);
                    cell.addElement(parrafo);
                    cell.setUseAscender(true);
                    cell.setPaddingBottom(4f);
                    cell.setPaddingTop(4f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tablaPdf.addCell(cell);

                    cell = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk(" " + lista.get(i).getSerie(), fuente1));
                    parrafo.setAlignment(Element.ALIGN_CENTER);
                    cell.addElement(parrafo);
                    cell.setUseAscender(true);
                    cell.setPaddingBottom(4f);
                    cell.setPaddingTop(4f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tablaPdf.addCell(cell);

                    cell = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk(lista.get(i).getNumDTE(), fuente1));
                    parrafo.setAlignment(Element.ALIGN_CENTER);
                    parrafo.setIndentationRight(8f);
                    cell.addElement(parrafo);
                    cell.setUseAscender(true);
                    cell.setPaddingBottom(4f);
                    cell.setPaddingTop(4f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaPdf.addCell(cell);

                    cell = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk(formatoFecha(lista.get(i).getFecha()), fuente1));
                    parrafo.setAlignment(Element.ALIGN_RIGHT);
                    parrafo.setIndentationRight(8f);
                    cell.addElement(parrafo);
                    cell.setUseAscender(true);
                    cell.setPaddingBottom(4f);
                    cell.setPaddingTop(4f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaPdf.addCell(cell);

                    cell = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk(" Q " + decimal(lista.get(i).getTotal()), fuente1));
                    parrafo.setAlignment(Element.ALIGN_RIGHT);
                    parrafo.setIndentationRight(8f);
                    cell.addElement(parrafo);
                    cell.setUseAscender(true);
                    cell.setPaddingBottom(4f);
                    cell.setPaddingTop(4f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaPdf.addCell(cell);

                    cell = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk(" Q " + decimal(lista.get(i).getAbono()), fuente1));
                    parrafo.setAlignment(Element.ALIGN_RIGHT);
                    parrafo.setIndentationRight(8f);
                    cell.addElement(parrafo);
                    cell.setUseAscender(true);
                    cell.setPaddingBottom(4f);
                    cell.setPaddingTop(4f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaPdf.addCell(cell);

                    cell = new PdfPCell();
                    parrafo = new Paragraph();
                    parrafo.add(new Chunk(" Q " + decimal(lista.get(i).getSaldo()), fuente1));
                    parrafo.setAlignment(Element.ALIGN_RIGHT);
                    parrafo.setIndentationRight(8f);
                    cell.addElement(parrafo);
                    cell.setUseAscender(true);
                    cell.setPaddingBottom(4f);
                    cell.setPaddingTop(4f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tablaPdf.addCell(cell);
                }
                documento.add(tablaPdf);
            }
            documento.close();
            ok = true;
        } catch (FileNotFoundException ex) {
            ok = false;
            ex.printStackTrace();
        } catch (DocumentException ex) {
            ok = false;
            ex.printStackTrace();
        } finally {
            try {
                if (archivo != null) {
                    archivo.close();
                }
            } catch (IOException ex) {
                ok = false;
            }
        }
        return ok;
    }

}
