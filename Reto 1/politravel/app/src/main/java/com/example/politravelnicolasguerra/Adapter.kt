package com.example.politravelnicolasguerra

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.RangeValueIterator
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.renderscript.Element


import java.io.File

class Adapter(
    private val context: Context,
    private var itemList: List<ClasePaquetes>,
    private val listener: OnItemClickListener,
    private var onItemLongClickListener: OnItemLongClickListener? = null
) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: ClasePaquetes)
    }
    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int): Boolean
    }
    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        onItemLongClickListener = listener
    }


    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        val nombre: TextView = view.findViewById(R.id.nombre)
        val pais: TextView = view.findViewById(R.id.pais)
        val precio: TextView = view.findViewById(R.id.precio)
        val img: ImageView = view.findViewById(R.id.imagenCarta)

        override fun onClick(p0: View?) {
            listener.onItemClick(itemList[position])
        }

        override fun onLongClick(p0: View?): Boolean {
            return onItemLongClickListener?.onItemLongClick(adapterPosition) ?: false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cardElement = itemList[position]
        holder.nombre.text = cardElement.nombre
        holder.precio.text = cardElement.precio
        holder.pais.text = cardElement.pais

        val imagePath = File(context.filesDir, "img/${cardElement.img}")
        val bitmap = BitmapFactory.decodeFile(imagePath.absolutePath)
        val blurredBitmap = getBlurImage(bitmap)
        holder.img.setImageBitmap(blurredBitmap)

        holder.itemView.setOnClickListener {
            listener.onItemClick(cardElement)
        }
    }

    fun getBlurImage(imagenRAW: Bitmap?): Bitmap? {
        val salidaBitmap = Bitmap.createBitmap(imagenRAW!!)
        val renderScript: RenderScript = RenderScript.create(context)
        val entradaTemp: Allocation = Allocation.createFromBitmap(renderScript, imagenRAW)
        val salidaTemp: Allocation = Allocation.createFromBitmap(renderScript, salidaBitmap)
        val scriptIntrinsicBlur: ScriptIntrinsicBlur =
            ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setRadius(3f)
        scriptIntrinsicBlur.setInput(entradaTemp)
        scriptIntrinsicBlur.forEach(salidaTemp)
        salidaTemp.copyTo(salidaBitmap)
        return salidaBitmap
    }

    override fun getItemCount() = itemList.size
}
