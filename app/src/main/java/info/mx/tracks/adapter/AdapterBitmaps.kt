package info.mx.tracks.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import info.mx.tracks.R

class AdapterBitmaps(private val photoList: List<Bitmap>) : RecyclerView.Adapter<AdapterBitmaps.ViewHolder>() {

    override fun getItemCount(): Int {
        return photoList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imagePlace: ImageView = view.findViewById(R.id.imagePicture)

    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_imageslider, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imagePlace.setImageBitmap(photoList[position])
    }

    override fun getItemViewType(i: Int): Int {
        return 0
    }

}
