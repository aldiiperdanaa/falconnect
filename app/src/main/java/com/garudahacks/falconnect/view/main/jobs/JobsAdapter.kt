package com.garudahacks.falconnect.view.main.jobs

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.AdapterJobsBinding
import com.garudahacks.falconnect.model.Jobs
import java.util.*

class JobsAdapter(
    private var communities: List<Jobs>,
    private val listener: AdapterListener
) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsAdapter.ViewHolder {
        val binding = AdapterJobsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = communities.size

    override fun onBindViewHolder(holder: JobsAdapter.ViewHolder, position: Int) {
        val item = communities[position]

        holder.binding.roleJob.text = item.roleJob
        holder.binding.companyName.text = item.companyName
        holder.binding.category.text = item.category
        holder.binding.salary.text = item.salary

        Glide.with(holder.itemView.context)
            .load(item.companyImg)
            .placeholder(R.drawable.img_placeholder)
            .into(holder.binding.companyImg)

        item.createdAt?.let { timestamp ->
            val now = Date()
            val date = timestamp.toDate()
            val diff = now.time - date.time

            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val weeks = days / 7
            val months = days / 30
            val years = days / 365

            val formattedDate = when {
                years > 0 -> "$years" + " years ago"
                months > 0 -> "$months" + " months ago"
                weeks > 0 -> "$weeks" + " weeks ago"
                days > 0 -> "$days" + " days ago"
                hours > 0 -> "$hours" + " hours ago"
                minutes > 0 -> "$minutes" + " minutes ago"
                else -> "$seconds" + " seconds ago"
            }

            holder.binding.createdAt.text = formattedDate
        }

        holder.binding.isDisability.visibility = if (item.isDisability == true) View.VISIBLE else View.GONE
        holder.binding.isBad.visibility = if (item.isBad == true) View.VISIBLE else View.GONE

        holder.binding.container.setOnClickListener {
            listener?.onClick(item)
        }
    }

    class ViewHolder(val binding: AdapterJobsBinding) : RecyclerView.ViewHolder(binding.root)

    fun setData(data: List<Jobs>) {
        communities = data
        notifyDataSetChanged()
    }

    interface AdapterListener {
        fun onClick(communities: Jobs)
    }
}
