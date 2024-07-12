package com.garudahacks.falconnect.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.AdapterHomeNewsBinding
import com.garudahacks.falconnect.model.News
import java.util.*

class HomeNewsAdapter(
    private var news: List<News>,
    private val listener: AdapterListener
) : RecyclerView.Adapter<HomeNewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeNewsAdapter.ViewHolder {
        val binding = AdapterHomeNewsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = news.size

    override fun onBindViewHolder(holder: HomeNewsAdapter.ViewHolder, position: Int) {
        val item = news[position]

        holder.binding.title.text = item.title
        Glide.with(holder.itemView.context)
            .load(item.photoUrl)
            .placeholder(R.drawable.img_placeholder)
            .into(holder.binding.photoUrl)

        holder.binding.container.setOnClickListener {
            listener?.onClick(item)
        }
    }


    class ViewHolder(val binding: AdapterHomeNewsBinding) : RecyclerView.ViewHolder(binding.root)

    fun setData(data: List<News>) {
        news = data
        notifyDataSetChanged()
    }

    interface AdapterListener {
        fun onClick(news: News)
    }
}
