package com.example.intellimeds;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
            tvDiaMedicamento.setText(medicamento.getDias());
            itemView.setOnClickListener(v -> listener.onItemClick(medicamento));
        }
    }
}

