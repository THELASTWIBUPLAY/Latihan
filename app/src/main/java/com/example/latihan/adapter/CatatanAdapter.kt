package com.example.latihan.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.latihan.databinding.ItemCatatanBinding
import com.example.latihan.entities.Catatan

class CatatanAdapter(
    private val dataset: MutableList<Catatan>,
    private val events: CatatanItemEvents
) : RecyclerView.Adapter<CatatanAdapter.CatatanViewHolder>() {

    interface CatatanItemEvents {
        fun onEdit(catatan: Catatan)
    }

    inner class CatatanViewHolder(
        val view: ItemCatatanBinding
    ) : RecyclerView.ViewHolder(view.root) {

        // fun setDatakeUI(data: Catatan) { // Baris ini dikomentari di gambar, tapi bisa digunakan
        fun setDatakeUI(data: Catatan) {
            view.judul.text = data.judul
            view.isi.text = data.isi

            view.root.setOnClickListener {
                events.onEdit(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatatanViewHolder {
        val binding = ItemCatatanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CatatanViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: CatatanViewHolder, position: Int) {
        val datasekarang = dataset[position]
        holder.setDatakeUI(datasekarang)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(dataBaru: List<Catatan>) {
        dataset.clear()
        dataset.addAll(dataBaru)
        notifyDataSetChanged()
    }
}