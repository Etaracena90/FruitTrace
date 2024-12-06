package com.planesa.fruittrace.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.planesa.fruittrace.R;
import com.planesa.fruittrace.model.Corte;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeleccionarEnviosAdapter extends RecyclerView.Adapter<SeleccionarEnviosAdapter.ViewHolder> {

    private List<Corte> listaDocumentos;
    private Context context;
    private Set<Integer> documentosSeleccionados = new HashSet<>();

    public SeleccionarEnviosAdapter(List<Corte> listaDocumentos, Context context) {
        this.listaDocumentos = listaDocumentos;
        this.context = context;
    }

    public Set<Integer> getDocumentosSeleccionados() {
        return documentosSeleccionados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seleccionar_envios, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Corte documento = listaDocumentos.get(position);
        holder.tvDocumentoId.setText("ID: " + documento.getId_enc_corte());
        holder.tvFecha.setText("Fecha: " + documento.getFecha());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(documentosSeleccionados.contains(documento.getId_enc_corte()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                documentosSeleccionados.add(documento.getId_enc_corte());
            } else {
                documentosSeleccionados.remove(documento.getId_enc_corte());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaDocumentos != null ? listaDocumentos.size() : 0;
    }

    public void updateData(List<Corte> nuevaLista) {
        this.listaDocumentos = nuevaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocumentoId, tvFecha;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDocumentoId = itemView.findViewById(R.id.tvDocumentoId);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            checkBox = itemView.findViewById(R.id.checkBoxSeleccionar);
        }
    }
}
