package com.example.simpletime

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.profile_list_entry.*

class ListEntryProfile (private val imgSize: Int, private val cont: Context, private val titleList: MutableList<String>, private val uri: Uri) : RecyclerView.Adapter<ListEntryProfile.YourViewHolder>() {

    inner class YourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile: ImageView = itemView.findViewById(R.id.profilepic)
        val textName: TextView = itemView.findViewById(R.id.profilename)
        //val layParams = itemView.layoutParams as RecyclerView.LayoutParams
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_list_entry, parent, false)
        return YourViewHolder(view)
    }

    override fun onBindViewHolder(holder: YourViewHolder, position: Int) {
        //holder.layParams.height = imgSize
        //holder.layParams.width = imgSize
        //holder.itemView.layoutParams = holder.layParams
        holder.textName.text = titleList[position]
        holder.profile.setImageURI(uri)
        //holder.textName.textSize = imgSize.toFloat()
    }

    override fun getItemCount(): Int {
        return titleList.size
    }
}