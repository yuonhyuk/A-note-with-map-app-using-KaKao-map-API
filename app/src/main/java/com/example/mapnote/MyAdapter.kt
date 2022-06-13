package com.example.mapnote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mapnote.Room.MarkerInfo

class MyAdapter (val context: Context, var list : List<MarkerInfo>): RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.note_item,parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val marker = list[position]

        holder.name.text = marker.name
        holder.memo.text = marker.content
        holder.date.text = marker.date
        holder.time.text = marker.time

        holder.root.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(p0: View?): Boolean {

                return true
            }
        })
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.adapter_name)
        val memo = itemView.findViewById<TextView>(R.id.adapter_content)
        val date = itemView.findViewById<TextView>(R.id.adapter_date)
        val time = itemView.findViewById<TextView>(R.id.adapter_time)
        val deletion = itemView.findViewById<ImageView>(R.id.delete)
        val modification = itemView.findViewById<ImageView>(R.id.modify)
        val root = itemView.findViewById<ConstraintLayout>(R.id.root)
    }

}