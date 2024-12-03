package com.planesa.fruittrace.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.planesa.fruittrace.R;
import com.planesa.fruittrace.model.Corte;
import com.planesa.fruittrace.ui.SeleccionarEnvioActivity;

import java.util.List;

public class UnificarEnviosAdapter extends RecyclerView.Adapter<UnificarEnviosAdapter.ViewHolder> {

    private List<Corte> listaCorte; // Lista de documentos
    private String usuarioLogeado; // Usuario logeado
    private Context context; // Contexto de la aplicación
    private OnSelectClickListener onSelectClickListener; // Listener para el botón seleccionar
    private OnCancelClickListener onCancelClickListener; // Listener para el botón cancelar

    public interface OnSelectClickListener {
        void onSelectClick(Corte corte); // Método para manejar clics en seleccionar
    }

    public interface OnCancelClickListener {
        void onCancelClick(Corte corte); // Método para manejar clics en cancelar
    }

    // Constructor
    public UnificarEnviosAdapter(List<Corte> listaCorte, Context context, String usuarioLogeado) {
        this.listaCorte = listaCorte;
        this.context = context;
        this.usuarioLogeado = usuarioLogeado; // Asignar el usuario recibido
    }

    // Métodos para configurar los listeners
    public void setOnSelectClickListener(OnSelectClickListener listener) {
        this.onSelectClickListener = listener;
    }

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.onCancelClickListener = listener;
    }

    // Método para actualizar la lista de datos en el adaptador
    public void updateData(List<Corte> nuevaLista) {
        this.listaCorte = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout para cada elemento del RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.item_unificar_envios, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obtener el documento actual
        Corte corte = listaCorte.get(position);

        // Configurar los textos en el ViewHolder
        holder.tvDocumentoId.setText("ID: " + corte.getId_enc_corte());
        holder.tvFecha.setText("Fecha: " + corte.getFecha());

        // Configurar el botón "Seleccionar"
        holder.btnSeleccionar.setOnClickListener(v -> {
            if (context != null) {
                Intent intent = new Intent(context, SeleccionarEnvioActivity.class);
                intent.putExtra("id_corte",corte.getId_enc_corte()); // ID del documento
                intent.putExtra("usuario", usuarioLogeado); // Usuario logeado
                context.startActivity(intent);
            }
        });

        // Configurar el botón "Cancelar"
        holder.btnCancelar.setOnClickListener(v -> {
            if (onCancelClickListener != null) {
                onCancelClickListener.onCancelClick(corte);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCorte.size(); // Tamaño de la lista
    }

    // Clase ViewHolder para gestionar las vistas de cada elemento
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDocumentoId; // TextView para mostrar el ID del documento
        TextView tvFecha; // TextView para mostrar la fecha del documento

        Button btnSeleccionar; // Botón para seleccionar un documento
        Button btnCancelar; // Botón para cancelar la selección

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vincular vistas del layout XML
            tvDocumentoId = itemView.findViewById(R.id.tvDocumentoId);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionar);
            btnCancelar = itemView.findViewById(R.id.btnCancelar);
        }
    }
}
