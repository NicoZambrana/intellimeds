package com.example.intellimeds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder> {

    private List<Medicamento> medicamentos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Medicamento medicamento);
    }

    public MedicamentoAdapter(List<Medicamento> medicamentos, OnItemClickListener listener) {
        this.medicamentos = medicamentos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicamento, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        Medicamento medicamento = medicamentos.get(position);
        holder.bind(medicamento, listener);
    }

    @Override
    public int getItemCount() {
        return medicamentos.size();
    }

    static class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombreMedicamento;
        private TextView tvDiaMedicamento;

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreMedicamento = itemView.findViewById(R.id.tv_nombre_medicamento);
            tvDiaMedicamento = itemView.findViewById(R.id.tv_dia_medicamento);
        }

        public void bind(final Medicamento medicamento, final OnItemClickListener listener) {
            tvNombreMedicamento.setText(medicamento.getNombre());
            //Traducimos los días para que cambien dependiendo del idioma
            String diasTraducidos = traducirDias(medicamento.getDias(), itemView.getContext());
            tvDiaMedicamento.setText(diasTraducidos);

            itemView.setOnClickListener(v -> listener.onItemClick(medicamento));
        }

        // Método para traducir los días según el idioma del sistema
        // Método para traducir los días
        private String traducirDias(String dias, Context context) {
            // Obtener los días localizados desde los recursos
            String[] diasLocalizados = new String[]{
                    context.getString(R.string.sunday_short),
                    context.getString(R.string.monday_short),
                    context.getString(R.string.tuesday_short),
                    context.getString(R.string.wednesday_short),
                    context.getString(R.string.thursday_short),
                    context.getString(R.string.friday_short),
                    context.getString(R.string.saturday_short)
            };

            // Mapear días en español a los localizados
            return dias
                    .replace("Lunes", diasLocalizados[1])
                    .replace("Martes", diasLocalizados[2])
                    .replace("Miércoles", diasLocalizados[3])
                    .replace("Jueves", diasLocalizados[4])
                    .replace("Viernes", diasLocalizados[5])
                    .replace("Sábado", diasLocalizados[6])
                    .replace("Domingo", diasLocalizados[0]);
        }
    }
}

