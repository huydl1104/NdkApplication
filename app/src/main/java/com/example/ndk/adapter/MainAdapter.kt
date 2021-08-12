package com.example.ndk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.ndk.R


class MainAdapter(val context: Context,val list: ArrayList<String>) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    var onItemClickListener: OnItemCLickListener? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main_list, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.textView.text = list[position]
        holder.textView.setOnClickListener {
            onItemClickListener?.onItemClick(list[position])
        }
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<Button>(R.id.button)!!
    }
}

interface OnItemCLickListener {
    fun onItemClick(case : String)
}