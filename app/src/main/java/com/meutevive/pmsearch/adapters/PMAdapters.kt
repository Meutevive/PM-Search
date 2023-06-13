package com.meutevive.pmsearch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meutevive.pmsearch.models.PM
import com.meutevive.pmsearch.R

class PMAdapters(private var pmList: List<PM>, private val onItemClickListener: (PM) -> Unit) : RecyclerView.Adapter<PMAdapters.ViewHolder>() {

    // Cette classe interne représente un élément de la liste (un PM)
    inner class ViewHolder(itemView: View, private val onItemClickListener: (PM) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val pmNumberTextView: TextView = itemView.findViewById(R.id.pm_number)
        val adresseTextView: TextView = itemView.findViewById(R.id.locationEditText)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener(pmList[position])
                }
            }
        }
    }

    // Cette méthode crée un nouveau ViewHolder pour afficher un élément de la liste
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pm, parent, false)
        return ViewHolder(view, onItemClickListener)
    }

    // Cette méthode lie les données du PM à un ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pm = pmList[position]
        holder.pmNumberTextView.text = pm.pmNumber
        holder.adresseTextView.text = pm.address
        // Définissez les valeurs pour les autres vues si nécessaire
    }

    // Cette méthode retourne le nombre d'éléments dans la liste de PM
    override fun getItemCount() = pmList.size

    fun updatePMList(newPMList: List<PM>) {
        pmList = newPMList
        notifyDataSetChanged()
    }

}
