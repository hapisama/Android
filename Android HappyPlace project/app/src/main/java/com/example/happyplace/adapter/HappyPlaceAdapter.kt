package com.example.happyplace.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplace.R
import com.example.happyplace.activites.AddHappyPlaceActivity
import com.example.happyplace.activites.MainActivity
import com.example.happyplace.database.DataBaseHandler
import com.example.happyplace.models.HappyPlaceModel

open class HappyPlaceAdapter (
    private val context: Context,
    private val list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener ?= null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_happy_place,
                null,
                false
            )
        )
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder){
            holder.itemView.findViewById<ImageView>(R.id.ivPlaceImage)
                .setImageURI(Uri.parse(model.image))
                holder.itemView.findViewById<TextView>(R.id.tvTitle).text = model.title
                holder.itemView.findViewById<TextView>(R.id.tvDescription).text = model.description

            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size

    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int ){
        val intent = Intent(context, AddHappyPlaceActivity :: class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)

        notifyItemChanged(position)
    }

    fun removeAt(position: Int){
        val dbHandler = DataBaseHandler(context)
        val isDelete = dbHandler.deleteHappyPlace(list[position])
        if (isDelete>0){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun  setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: HappyPlaceModel)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}