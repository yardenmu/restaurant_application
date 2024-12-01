package com.example.yarden_mugany

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(private var items: List<MenuItem> ) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val menuImage: ImageView = view.findViewById(R.id.cardImage)
        val menuTitle: TextView = view.findViewById(R.id.cardTitle)
        val menuDescription: TextView = view.findViewById(R.id.cardDescription)
        val menuPrice: TextView = view.findViewById(R.id.cardPrice)
        val menuIsVegan: ImageView = view.findViewById(R.id.cardVegan)
    }
    fun updateMenu(newMenuItems: List<MenuItem>){
        items = newMenuItems
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_card,parent,false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = items[position]
        holder.menuImage.setImageResource(item.image)
        holder.menuTitle.text = item.title
        holder.menuDescription.text = item.description
        holder.menuPrice.text = item.price
        if(item.isVegan){
            holder.menuIsVegan.visibility = View.VISIBLE
        }
        else{
            holder.menuIsVegan.visibility = View.INVISIBLE
        }
    }
}