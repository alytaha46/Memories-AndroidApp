package com.example.memories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memories.database.Memory
import kotlinx.android.synthetic.main.item_memory.view.*
import java.io.File
import java.io.FileInputStream


class HomeRecycleAdapter(var items: List<Memory>?):RecyclerView.Adapter<HomeRecycleAdapter.ViewHolder>() {

    fun changeData(memories: List<Memory>)
    {
        items = memories
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val memory_name_label:TextView = itemView.memory_name_label
        val memory_description_label:TextView = itemView.memory_description_label
        val memory_pic:ImageView = itemView.memory_pic
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memory = items?.get(position)
        holder.memory_name_label.text = memory?.title
        holder.memory_description_label.text = memory?.description
        holder.memory_pic.setImageBitmap(getImageFromStorage(File(memory?.path)))
    }

    private fun getImageFromStorage(file:File):Bitmap?{
        try {
            val streamIn = FileInputStream(file)
            val bitmap = BitmapFactory.decodeStream(streamIn) //This gets the image
            streamIn.close()
            return bitmap
        } catch (e: Exception) {
            Log.e("ms", "" + e)
            return null
        }
    }

    override fun getItemCount(): Int = items?.size ?:0
}