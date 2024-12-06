package com.planesa.fruittrace.adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.planesa.fruittrace.R;
import com.planesa.fruittrace.model.Corte;
import java.io.File;
import java.util.List;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.planesa.fruittrace.dao.DAOCorte;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;

//import com.planesa.fruittrace.BuildConfig;


public class CorteAdapter extends RecyclerView.Adapter<CorteAdapter.CorteViewHolder> {
    private List<Corte> corteList;
    private OnScanClickListener onScanClickListener;
    private Context context;
    private OnDocumentoEnviadoListener onDocumentoEnviadoListener;


    public interface OnScanClickListener {
        void onScanClick(Corte corte);

        void onCancelClick(Corte corte);
    }

    public interface OnDocumentoEnviadoListener {
        void onDocumentoEnviado();
    }

    public CorteAdapter(List<Corte> corteList, OnScanClickListener onScanClickListener, Context context) {
        this.corteList = corteList;
        this.onScanClickListener = onScanClickListener;
        this.context = context;
    }
    public void setOnDocumentoEnviadoListener(OnDocumentoEnviadoListener listener) {
        this.onDocumentoEnviadoListener = listener;
    }

    @NonNull
    @Override
    public CorteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_corte, parent, false);
        return new CorteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CorteViewHolder holder, int position) {
        Corte corte = corteList.get(position);

        holder.tvNumeroDocumento.setText("Documento: " + corte.getId_enc_corte());
        holder.tvItemFecha.setText(corte.getFecha() != null ? corte.getFecha() : "Sin fecha");
        holder.tvItemApuntador.setText(corte.getApuntador() != null ? corte.getApuntador() : "Sin apuntador");
        holder.tvItemFinca.setText(corte.getNombrefinca() != null ? corte.getNombrefinca() : "Sin finca");

        Integer estado = corte.getEstado(); // Obtener el estado
        if (estado != null && estado == 3) { // Si el estado es "enviado"
            holder.btnEnviar.setText("Enviado");
            holder.btnEnviar.setEnabled(false);
            holder.btnEnviar.setBackgroundColor(context.getResources().getColor(R.color.gray));
        } else { // En caso contrario
            holder.btnEnviar.setText("Enviar");
            holder.btnEnviar.setEnabled(true);
            holder.btnEnviar.setBackgroundColor(context.getResources().getColor(R.color.green));
        }


        holder.btnCancelar.setOnClickListener(v -> {
            if (onScanClickListener != null) {
                onScanClickListener.onCancelClick(corte);
            }
        });

        holder.btnScan.setOnClickListener(v -> {
            if (onScanClickListener != null) {
                onScanClickListener.onScanClick(corte);
            }
        });




        holder.btnEnviar.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                DAOCorte daoCorte = new DAOCorte();
                try {


                    // Obtener el detalle completo del corte y sus datos
                    Corte corteCompleto = daoCorte.obtenerDetalleCorteCompleto(corte.getId_enc_corte());
                    List<Corte> detallesCorte = daoCorte.obtenerDetallesCorte(corte.getId_enc_corte());

                    if (corteCompleto != null && detallesCorte != null) {
                        corte.setEstado(3);
                        corte.setEstado_envio(7);
                        daoCorte.actualizarEstado(corte);
                        File pdfFile = generarPDFConFormato(corteCompleto, detallesCorte);
                        abrirPDF(pdfFile);
                    } else {
                        throw new Exception("No se encontraron datos completos del corte o detalles.");
                    }

                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Documento enviado y PDF generado con éxito.", Toast.LENGTH_SHORT).show();
                        holder.btnEnviar.setText("Enviado");
                        holder.btnEnviar.setEnabled(false);
                        holder.btnEnviar.setBackgroundColor(context.getResources().getColor(R.color.gray));

                        // Notificar al listener que recargue la lista
                        if (onDocumentoEnviadoListener != null) {
                            onDocumentoEnviadoListener.onDocumentoEnviado();

                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Error al procesar el documento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        holder.btnEnviar.setEnabled(true);
                        holder.btnEnviar.setText("ENVIAR");
                    });
                } finally {
                    executor.shutdown();
                }
            });
        });







    }

    private File generarPDFConFormato(Corte corte, List<Corte> detalles) {
        try {
            // Ruta donde se almacenará el PDF
            File pdfDir = context.getExternalFilesDir(null);
            if (pdfDir == null) {
                throw new IOException("No se pudo acceder al directorio de almacenamiento externo.");
            }

            String filePath = pdfDir.getAbsolutePath() + "/Envio_" + corte.getId_enc_corte() + ".pdf";

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            pdfDocument.setDefaultPageSize(PageSize.A4.rotate()); // Orientación horizontal
            Document document = new Document(pdfDocument);

            // Encabezado
            document.add(new Paragraph("ENVÍO A PLANTA No. " + corte.getId_enc_corte())
                    .setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            // Información del documento
            Table infoTable = new Table(1);
            infoTable.setWidth(UnitValue.createPercentValue(100));

            // Agrega información al documento usando Paragraph
            infoTable.addCell(new Cell().add(new Paragraph("FINCA: " + corte.getNombrefinca())).setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("FECHA: " + corte.getFecha())).setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("USUARIO: " + corte.getApuntador())).setBorder(Border.NO_BORDER));
            document.add(infoTable);

            // Encabezado de la tabla
            Table table = new Table(new float[]{3, 2, 3, 3, 3, 2}); // Configura las columnas
            table.setWidth(UnitValue.createPercentValue(100));

            // Cabeceras de la tabla
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("LOTE"))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("NOMBRE DE LOTE"))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("VARIEDAD"))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("ETIQUETA"))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("UM"))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("CANTIDAD"))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER));

            // Agregar detalles de los lotes
            for (Corte detalle : detalles) {
                table.addCell(new Cell()
                        .add(new Paragraph(detalle.getLote()))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell()
                        .add(new Paragraph(detalle.getNombre_lote()))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell()
                        .add(new Paragraph(detalle.getVariedad()))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell()
                        .add(new Paragraph(detalle.getEtiqueta()))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell()
                        .add(new Paragraph(detalle.getUnidad_medida()))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(detalle.getCantidad())))
                        .setTextAlignment(TextAlignment.CENTER));
            }

            // Total
            table.addCell(new Cell(1, 5)
                    .add(new Paragraph("TOTAL:"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(detalles.stream().mapToDouble(Corte::getCantidad).sum())))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(table);
            document.close();

            // Mostrar mensaje al usuario
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "PDF generado: " + filePath, Toast.LENGTH_SHORT).show());

            return new File(filePath); // Retornar el archivo generado
        } catch (Exception e) {
            Log.e("PDFGenerator", "Error al generar el PDF", e);
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Error al generar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
        return null;
    }

    private void abrirPDF(File pdfFile) {
        Uri pdfUri = FileProvider.getUriForFile(
                context,
                "com.planesa.fruittrace.provider", // Usa tu namespace manualmente.
                pdfFile
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(intent, "Abrir PDF con"));
    }

    public void updateData(List<Corte> nuevaLista) {
        if (nuevaLista == null || nuevaLista.isEmpty()) {
            this.corteList.clear(); // Limpiar la lista actual si es vacía
        } else {
            this.corteList = nuevaLista; // Actualizar con los nuevos datos
        }
        notifyDataSetChanged(); // Notificar al RecyclerView que los datos han cambiado
    }


    @Override
    public int getItemCount() {
        return corteList != null ? corteList.size() : 0;
    }

    public static class CorteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroDocumento, tvItemFecha, tvItemApuntador, tvItemFinca;
        Button btnCancelar, btnScan, btnEnviar;

        public CorteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroDocumento = itemView.findViewById(R.id.tvNumeroDocumento);
            tvItemFecha = itemView.findViewById(R.id.tvItemFecha);
            tvItemApuntador = itemView.findViewById(R.id.tvItemApuntador);
            tvItemFinca = itemView.findViewById(R.id.tvItemFinca);
            btnCancelar = itemView.findViewById(R.id.btnCancelar);
            btnScan = itemView.findViewById(R.id.btnScan);
            btnEnviar = itemView.findViewById(R.id.btnEnviar);
        }
    }
}