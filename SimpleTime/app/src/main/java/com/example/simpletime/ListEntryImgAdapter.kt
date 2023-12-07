package com.example.simpletime

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_entry.*

class ListEntryImgAdapter (private val titleList: MutableList<String>, private val cont: Context, private val onItemClickListener: (Int) -> Unit) : RecyclerView.Adapter<ListEntryImgAdapter.YourViewHolder>() {

    inner class YourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.title)
        val button: Button = itemView.findViewById(R.id.button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_entry_img, parent, false)
        return YourViewHolder(view)
    }

    override fun onBindViewHolder(holder: YourViewHolder, position: Int) {
        holder.titleText.text = titleList[position]

        holder.button.setOnClickListener{
            onItemClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return titleList.size
    }
}