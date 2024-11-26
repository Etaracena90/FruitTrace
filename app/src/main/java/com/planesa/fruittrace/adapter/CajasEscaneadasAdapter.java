package com.planesa.fruittrace.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.planesa.fruittrace.R;
import com.planesa.fruittrace.model.Corte;

import java.util.List;

public class CajasEscaneadasAdapter extends RecyclerView.Adapter<CajasEscaneadasAdapter.CajasViewHolder> {

    private List<Corte> listaCajas;
    private int expandedPosition = -1; // Posición del item expandido actualmente

    public CajasEscaneadasAdapter(List<Corte> listaCajas) {
        this.listaCajas = listaCajas;
    }

    @NonNull
    @Override
    public CajasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detalle_caja, parent, false);
        return new CajasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CajasViewHolder holder, int position) {
        Corte corte = listaCajas.get(position);
        boolean isExpanded = position == expandedPosition;

        // Mostrar datos básicos del cortador
        holder.tvCortador.setText("Cortador: " + corte.getCodigo_cortador() + " - " + corte.getNombre_cortador());

        // Mostrar o ocultar detalles según si está expandido
        holder.detalleLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        if (isExpanded) {
            holder.tvLote.setText("Lote: " + corte.getLote());
            holder.tvVariedad.setText("Variedad: " + corte.getVariedad());
            holder.tvPresentacion.setText("Presentación: " + corte.getPresentacion());
            holder.tvDestino.setText("Destino: " + corte.getDestino());
            holder.tvCantidad.setText("Cantidad: " + corte.getCantidad());
            holder.tvClasificador.setText("Clasificador: " + corte.getCodigo_clasificador());
        }

        // Configurar botón para expandir o contraer el detalle
        holder.btnExpandCollapse.setOnClickListener(v -> {
            expandedPosition = isExpanded ? -1 : position; // Alternar entre expandido y contraído
            notifyDataSetChanged(); // Notificar cambios en la lista
        });
    }

    @Override
    public int getItemCount() {
        return listaCajas != null ? listaCajas.size() : 0;
    }

    public static class CajasViewHolder extends RecyclerView.ViewHolder {
        TextView tvCortador, tvLote, tvVariedad, tvPresentacion, tvDestino, tvCantidad, tvClasificador;
        View detalleLayout; // Layout que contiene los detalles
        ImageButton btnExpandCollapse;

        public CajasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCortador = itemView.findViewById(R.id.tvCortador);
            tvLote = itemView.findViewById(R.id.tvLote);
            tvVariedad = itemView.findViewById(R.id.tvVariedad);
            tvPresentacion = itemView.findViewById(R.id.tvPresentacion);
            tvDestino = itemView.findViewById(R.id.tvDestino);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvClasificador = itemView.findViewById(R.id.tvClasificador);
            detalleLayout = itemView.findViewById(R.id.detalleLayout); // Este layout contiene los detalles
            btnExpandCollapse = itemView.findViewById(R.id.btnExpandCollapse);
        }
    }
}
