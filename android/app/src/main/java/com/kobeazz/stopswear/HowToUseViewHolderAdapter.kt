package com.kobeazz.stopswear

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HowToUseViewHolderAdapter(private var pageList: ArrayList<HowToUseData>) : RecyclerView.Adapter<HowToUsePagerViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HowToUsePagerViewHolder {
        return HowToUsePagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_use, parent, false))
    }

    override fun getItemCount(): Int {
        return pageList.size
    }

    override fun onBindViewHolder(holder: HowToUsePagerViewHolder, position: Int) {
        holder.bind(pageList.get(position))
    }
}

class HowToUsePagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val image = itemView.findViewById<ImageView>(R.id.useImageView)
    private val text = itemView.findViewById<TextView>(R.id.useTextView)

    fun bind(data: HowToUseData) {
        image.setImageResource(data.imageSrc)
        text.setText(data.text)
    }
}

data class HowToUseData(val imageSrc: Int, val text: String)