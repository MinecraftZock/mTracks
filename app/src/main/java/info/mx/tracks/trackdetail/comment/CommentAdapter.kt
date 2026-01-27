package info.mx.tracks.trackdetail.comment

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import info.mx.comlib.retrofit.service.data.Data
import info.mx.tracks.R
import info.mx.tracks.room.entity.Comment
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class CommentAdapter(private val context: Context) : RecyclerView.Adapter<CommentAdapter.PostViewHolder>() {

    private var data: MutableList<Comment>? = null
    private val layoutInflater: LayoutInflater

    init {
        this.data = ArrayList()
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_comment, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(data!![position])
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    internal fun setData(newData: Data<List<Comment>>) {
        Timber.d("setData " + newData.source.name + " " + newData.data.size)
        setData(newData.data.toMutableList())
    }

    private fun setData(newData: MutableList<Comment>) {
        var track = ""
        if (newData.size > 0)
            track = newData[0].trackId.toString()
        Timber.d("setDataAD ${newData.size} $track")
        if (data != null) {
            val commentDiffCallback = CommentDiffCallback(data!!, newData)
            val diffResult = DiffUtil.calculateDiff(commentDiffCallback)

            data!!.clear()
            data!!.addAll(newData)
            diffResult.dispatchUpdatesTo(this)
        } else {
            // first initialization
            data = newData
        }
        notifyDataSetChanged()
    }

    internal inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ratingComment: RatingBar = itemView.findViewById(R.id.comlst_rating)
        private val tvUser: TextView = itemView.findViewById(R.id.comlst_username)
        private val imageCountry: ImageView = itemView.findViewById(R.id.comlst_country)
        private val tvNote: TextView = itemView.findViewById(R.id.comlst_note)
        private val tvDatum: TextView = itemView.findViewById(R.id.comlst_datum)

        @SuppressLint("DiscouragedApi")
        fun bind(comment: Comment) {
            if (comment.username == "") {
                tvUser.text = context.getString(R.string.noname)
            } else {
                tvUser.text = comment.username
            }

            val valueL = comment.country.lowercase(Locale.getDefault())
            val id = context.resources.getIdentifier(valueL, "drawable", context.packageName)
            imageCountry.setImageResource(id)

            if (comment.note.equals("")) {
                tvNote.visibility = View.GONE
            } else {
                tvNote.visibility = View.VISIBLE
                tvNote.text = comment.note
            }
            tvNote.alpha = if (comment.id!! < 1) 0.5F else 1.0F

            ratingComment.rating = comment.rating.toFloat()
            tvDatum.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(comment.changed * 1000))

//                tvUser.setOnLongClickListener { _ ->
//                    mxDatabase.commentDao().delete(comment)
//                    return@setOnLongClickListener true
//                }
        }
    }

    internal inner class CommentDiffCallback(private val oldComment: List<Comment>, private val newComment: List<Comment>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldComment.size
        }

        override fun getNewListSize(): Int {
            return newComment.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldComment[oldItemPosition].id === newComment[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldComment[oldItemPosition] == newComment[newItemPosition]
        }
    }
}
