package com.anetos.parkme.core

import android.content.ClipData
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: Any)
}