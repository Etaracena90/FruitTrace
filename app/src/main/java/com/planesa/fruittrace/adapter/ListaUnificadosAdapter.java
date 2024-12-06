package com.planesa.fruittrace.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.planesa.fruittrace.R;
import com.planesa.fruittrace.dao.DAOCorte;
import com.planesa.fruittrace.model.Corte;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ListaUnificadosAdapter extends RecyclerView.Adapter<ListaUnificadosAdapter.ViewHolder> {

    private List<Corte> listaUnificados;
    private Context context;

    public ListaUnificadosAdapter(List<Corte> listaUnificados, Context context) {
        this.listaUnificados = listaUnificados;
        this.context = context;
    }

    public void updateData(List<Corte> nuevaLista) {
        this.listaUnificados = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_unificado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Corte corte = listaUnificados.get(position);

        holder.tvIdDocumento.setText("ID: " + corte.getId_enc_corte());
        holder.tvFecha.setText("Fecha: " + corte.getFecha());holder.btnGenerarPDF.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                DAOCorte daoCorte = new DAOCorte();
                try {
                    // Usar el método listarDetallesEnvioPorId para obtener los detalles del envío
                    List<Corte> detallesCorte = daoCorte.listarDetallesEnvioPorId(corte.getId_enc_corte());

                    if (detallesCorte != null && !detallesCorte.isEmpty()) {
                        // Se usa el primer elemento para obtener datos generales del envío
                        Corte corteCompleto = detallesCorte.get(0);

                        // Generar y abrir el PDF
                        File pdfFile = generarPDFConFormato(corteCompleto, detallesCorte);
                        abrirPDF(pdfFile);
                    } else {
                        throw new Exception("No se encontraron detalles para el envío especificado.");
                    }
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context, "Error al generar el PDF: " + e.getMessage(), Toast.LENGTH_LONG).show());
                } finally {
                    executor.shutdown();
                }
            });
        });
    }

    private File generarPDFConFormato(Corte corte, List<Corte> detalles) {
        try {
            File pdfDir = context.getExternalFilesDir(null);
            if (pdfDir == null || !pdfDir.exists()) {
                pdfDir.mkdirs();
            }

            String filePath = pdfDir.getAbsolutePath() + "/Envio_" + corte.getId_enc_corte() + ".pdf";

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            pdfDocument.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDocument);

            // Estilo para colores
            DeviceRgb headerColor = new DeviceRgb(0, 102, 204); // Azul
            DeviceRgb sectionColor = new DeviceRgb(224, 235, 255); // Azul suave
            DeviceRgb subSectionColor = new DeviceRgb(240, 240, 240); // Gris claro
            DeviceRgb tableHeaderColor = new DeviceRgb(192, 192, 192); // Gris claro
            DeviceRgb totalColor = new DeviceRgb(255, 0, 0); // Rojo

            // Encabezado
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .useAllAvailableWidth();

            headerTable.addCell(new Cell()
                    .add(new Paragraph("FINCA: " + (corte.getNombrefinca() != null ? corte.getNombrefinca() : "N/A"))
                            .setFontSize(10)
                            .setBold())
                    .setBorder(Border.NO_BORDER));

            headerTable.addCell(new Cell()
                    .add(new Paragraph("FECHA: " + (corte.getFecha() != null ? corte.getFecha() : "N/A"))
                            .setFontSize(10)
                            .setBold())
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER));

            headerTable.addCell(new Cell()
                    .add(new Paragraph("APUNTADOR: " + (corte.getApuntador() != null ? corte.getApuntador() : "N/A"))
                            .setFontSize(10)
                            .setBold())
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(headerTable);

            // Título principal
            document.add(new Paragraph("ENVÍO UNIFICADO A PLANTA: " + corte.getNo_envio_unificado())
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(headerColor)
                    .setMarginBottom(10));

            // Total general inicializado
            double totalGeneral = 0;

            // Agrupar datos por descripción
            Map<String, List<Corte>> groupedByDescription = detalles.stream()
                    .collect(Collectors.groupingBy(detalle -> detalle.getDescripcion() != null ? detalle.getDescripcion() : "N/A"));

            for (Map.Entry<String, List<Corte>> entry : groupedByDescription.entrySet()) {
                String descripcion = entry.getKey();
                List<Corte> cortesByDescription = entry.getValue();

                // Sección de descripción
                document.add(new Paragraph(descripcion.toUpperCase())
                        .setFontSize(12)
                        .setBold()
                        .setFontColor(DeviceRgb.WHITE)
                        .setBackgroundColor(headerColor)
                        .setMarginTop(10)
                        .setMarginBottom(5)
                        .setTextAlignment(TextAlignment.CENTER));

                Map<String, List<Corte>> groupedByPresentation = cortesByDescription.stream()
                        .collect(Collectors.groupingBy(detalle -> detalle.getNombre_presentacion() != null ? detalle.getNombre_presentacion() : "N/A"));

                for (Map.Entry<String, List<Corte>> presentationEntry : groupedByPresentation.entrySet()) {
                    String presentacion = presentationEntry.getKey();
                    List<Corte> cortesByPresentation = presentationEntry.getValue();

                    // Sección de presentación
                    document.add(new Paragraph("PRESENTACIÓN: " + presentacion)
                            .setFontSize(10)
                            .setBold()
                            .setMarginLeft(10)
                            .setBackgroundColor(subSectionColor)
                            .setMarginBottom(2));

                    Map<String, List<Corte>> groupedByEtiqueta = cortesByPresentation.stream()
                            .collect(Collectors.groupingBy(detalle -> detalle.getNombre_etiqueta() != null ? detalle.getNombre_etiqueta() : "N/A"));

                    for (Map.Entry<String, List<Corte>> etiquetaEntry : groupedByEtiqueta.entrySet()) {
                        String etiqueta = etiquetaEntry.getKey();
                        List<Corte> cortesByEtiqueta = etiquetaEntry.getValue();

                        // Sección de etiqueta
                        document.add(new Paragraph("ETIQUETA: " + etiqueta)
                                .setFontSize(10)
                                .setBold()
                                .setMarginLeft(20)
                                .setBackgroundColor(sectionColor)
                                .setMarginBottom(2));

                        Map<String, List<Corte>> groupedByCultivo = cortesByEtiqueta.stream()
                                .collect(Collectors.groupingBy(detalle -> detalle.getCultivo() != null ? detalle.getCultivo() : "N/A"));

                        for (Map.Entry<String, List<Corte>> cultivoEntry : groupedByCultivo.entrySet()) {
                            String cultivo = cultivoEntry.getKey();
                            List<Corte> cortesByCultivo = cultivoEntry.getValue();

                            // Sección de cultivo
                            document.add(new Paragraph("CULTIVO: " + cultivo)
                                    .setFontSize(10)
                                    .setBold()
                                    .setMarginLeft(30)
                                    .setBackgroundColor(subSectionColor)
                                    .setMarginBottom(5));

                            Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 1, 1}))
                                    .useAllAvailableWidth();

                            // Encabezados de tabla
                            detailsTable.addHeaderCell(new Cell().add(new Paragraph("LOTE").setBold()).setBackgroundColor(tableHeaderColor));
                            detailsTable.addHeaderCell(new Cell().add(new Paragraph("NOMBRE LOTE").setBold()).setBackgroundColor(tableHeaderColor));
                            detailsTable.addHeaderCell(new Cell().add(new Paragraph("VARIEDAD").setBold()).setBackgroundColor(tableHeaderColor));
                            detailsTable.addHeaderCell(new Cell().add(new Paragraph("UM").setBold()).setBackgroundColor(tableHeaderColor));
                            detailsTable.addHeaderCell(new Cell().add(new Paragraph("CANTIDAD").setBold()).setBackgroundColor(tableHeaderColor));

                            double totalCantidad = 0;

                            for (Corte detalle : cortesByCultivo) {
                                detailsTable.addCell(new Cell().add(new Paragraph(detalle.getLote() != null ? detalle.getLote() : "N/A")));
                                detailsTable.addCell(new Cell().add(new Paragraph(detalle.getNombre_lote() != null ? detalle.getNombre_lote() : "N/A")));
                                detailsTable.addCell(new Cell().add(new Paragraph(detalle.getVariedad() != null ? detalle.getVariedad() : "N/A")));
                                detailsTable.addCell(new Cell().add(new Paragraph(detalle.getUnidad_medida() != null ? detalle.getUnidad_medida() : "N/A")));
                                detailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(detalle.getCantidad()))
                                        .setFontColor(totalColor)));
                                totalCantidad += detalle.getCantidad();
                            }

                            totalGeneral += totalCantidad; // Sumar al total general

                            detailsTable.addCell(new Cell(1, 4)
                                    .add(new Paragraph("TOTAL:").setBold())
                                    .setTextAlignment(TextAlignment.RIGHT)
                                    .setBackgroundColor(tableHeaderColor));
                            detailsTable.addCell(new Cell()
                                    .add(new Paragraph(String.valueOf(totalCantidad))
                                            .setFontColor(totalColor))
                                    .setTextAlignment(TextAlignment.CENTER));

                            document.add(detailsTable);
                        }
                    }
                }
            }

            // Agregar el total general al final del documento
            document.add(new Paragraph("TOTAL GENERAL: " + totalGeneral)
                    .setFontSize(12)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(totalColor)
                    .setMarginTop(20));

            // Pie de página
            document.add(new Paragraph(new Date().toString())
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();

            // Mostrar mensaje al usuario
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "PDF generado: " + filePath, Toast.LENGTH_SHORT).show());

            return new File(filePath);
        } catch (Exception e) {
            Log.e("PDFGenerator", "Error al generar el PDF", e);
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Error al generar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
        return null;
    }




    private void abrirPDF(File pdfFile) {
        Uri pdfUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", pdfFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Abrir PDF con"));
    }

    @Override
    public int getItemCount() {
        return listaUnificados.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdDocumento, tvFecha;
        Button btnGenerarPDF;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdDocumento = itemView.findViewById(R.id.tvIdDocumento);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            btnGenerarPDF = itemView.findViewById(R.id.btnGenerarPDF);
        }
    }
}
