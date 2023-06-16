package com.example.batikcapstone.data.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.batikcapstone.R
import com.example.batikcapstone.data.model.News


class NewsAdapter(private var newsList: List<News>, private val isNewsFragment: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ONE = 1
    private val VIEW_TYPE_TWO = 2
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolderOne(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)

        init {
            tvName.maxLines = 1
            tvDescription.maxLines = 4
            tvDescription.ellipsize = TextUtils.TruncateAt.END
        }
    }

    inner class ViewHolderTwo(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvPostDate: TextView = itemView.findViewById(R.id.tv_item_postDate)

        init {
            tvName.maxLines = 2
            tvName.ellipsize = TextUtils.TruncateAt.END
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ONE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
                ViewHolderOne(view)
            }
            VIEW_TYPE_TWO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news_home, parent, false)
                ViewHolderTwo(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ( name, description, postDate, photoUrl, webUrl) = newsList[position]
        when (holder) {
            is ViewHolderOne -> {
                Glide.with(holder.itemView.context)
                    .load(photoUrl)
                    .placeholder(R.drawable.baseline_error_24)
                    .error(R.drawable.baseline_error_24)
                    .into(holder.imgPhoto)
                holder.tvName.text = name
                holder.tvDescription.text = description
                holder.itemView.setOnClickListener {
                    onItemClickCallback.onItemClicked(newsList[holder.adapterPosition])
                }
            }
            is ViewHolderTwo -> {
                Glide.with(holder.itemView.context)
                    .load(photoUrl)
                    .placeholder(R.drawable.baseline_error_24)
                    .error(R.drawable.baseline_error_24)
                    .into(holder.imgPhoto)
                holder.tvName.text = name
                holder.tvPostDate.text = postDate
                holder.itemView.setOnClickListener {
                    onItemClickCallback.onItemClicked(newsList[holder.adapterPosition])
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return newsList.size
    }
    override fun getItemViewType(position: Int): Int {
        return if (isNewsFragment) {
            VIEW_TYPE_ONE
        } else {
            VIEW_TYPE_TWO
        }
    }
    interface OnItemClickCallback {
        fun onItemClicked(data: News)
    }

    fun setData(newList: List<News>) {
        newsList = newList
        notifyDataSetChanged()
    }
}