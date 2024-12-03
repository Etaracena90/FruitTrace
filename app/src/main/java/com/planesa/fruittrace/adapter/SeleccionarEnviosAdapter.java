package com.planesa.fruittrace.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.planesa.fruittrace.R;
import com.planesa.fruittrace.model.Corte;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeleccionarEnviosAdapter extends RecyclerView.Adapter<SeleccionarEnviosAdapter.ViewHolder> {

    private List<Corte> listaDocumentos; // Lista de documentos a mostrar
    private Context context; // Contexto de la aplicación
    private Set<Integer> documentosSeleccionados = new HashSet<>(); // IDs de documentos seleccionados

    public SeleccionarEnviosAdapter(List<Corte> listaDocumentos, Context context) {
        this.listaDocumentos = listaDocumentos;
        this.context = context;
    }

    /**
     * Devuelve los documentos seleccionados
     */
    public Set<Integer> getDocumentosSeleccionados() {
        return documentosSeleccionados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout para cada elemento del RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.item_seleccionar_envios, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obtener el documento en la posición actual
        Corte documento = listaDocumentos.get(position);

        // Configurar los textos
        holder.tvDocumentoId.setText("ID: " + documento.getId_enc_corte());
        holder.tvFecha.setText("Fecha: " + documento.getFecha());

        // Configurar el CheckBox para gestionar la selección
        holder.checkBox.setOnCheckedChangeListener(null); // Limpiar oyente previo para evitar conflictos
        holder.checkBox.setChecked(documentosSeleccionados.contains(documento.getId_enc_corte()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                documentosSeleccionados.add(documento.getId_enc_corte());
            } else {
                documentosSeleccionados.remove(documento.getId_enc_corte());
            }
            // Mostrar un mensaje al usuario con la cantidad de seleccionados
            Toast.makeText(context, "Seleccionados: " + documentosSeleccionados.size(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        // Devuelve el tamaño de la lista de documentos
        return listaDocumentos.size();
    }



    public void updateData(List<Corte> nuevaLista) {
        this.listaDocumentos = nuevaLista;
        notifyDataSetChanged();
    }

    /**
     * Clase ViewHolder para gestionar las vistas de cada elemento
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocumentoId, tvFecha; // TextViews para mostrar datos del documento
        CheckBox checkBox; // CheckBox para seleccionar el documento

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vincular las vistas del layout XML con los elementos de la clase
            tvDocumentoId = itemView.findViewById(R.id.tvDocumentoId);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            checkBox = itemView.findViewById(R.id.checkBoxSeleccionar);
        }
    }
}
