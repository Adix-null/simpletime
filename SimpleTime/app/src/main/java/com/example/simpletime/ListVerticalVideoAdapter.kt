package com.example.simpletime

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListVerticalVideoAdapter (private val cont: Context, private val titleList: MutableList<String>, private val uriList: MutableList<Uri>) : RecyclerView.Adapter<ListVerticalVideoAdapter.YourViewHolder>() {

    inner class YourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile: ImageView = itemView.findViewById(R.id.verticalVideoButton)
        val textName: TextView = itemView.findViewById(R.id.verticalVideoName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vertical_video_list_entry, parent, false)
        return YourViewHolder(view)
    }

    override fun onBindViewHolder(holder: YourViewHolder, position: Int) {
        holder.textName.text = titleList[position]
        holder.profile.setImageURI(uriList[position])
    }

    override fun getItemCount(): Int {
        return titleList.size
    }
}