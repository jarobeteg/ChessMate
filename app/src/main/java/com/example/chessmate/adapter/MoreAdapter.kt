package com.example.chessmate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R

data class Item(val name: String, val isUrl: Boolean = false, val action: () -> Unit)
class MoreAdapter(private val items: List<Item>, private val context: Context) : RecyclerView.Adapter<MoreAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView = view.findViewById(R.id.item_more_fragment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_more_fragment_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.name
        if (item.isUrl) {
            val externalDrawable = ContextCompat.getDrawable(context, R.drawable.ic_external_24)
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                externalDrawable,
                null
            )
        }

        holder.itemView.setOnClickListener{
            item.action.invoke()
        }
    }


}