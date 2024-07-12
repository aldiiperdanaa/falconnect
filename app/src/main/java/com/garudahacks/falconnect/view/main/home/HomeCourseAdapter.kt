package com.garudahacks.falconnect.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.AdapterHomeCourseBinding
import com.garudahacks.falconnect.model.Course

class HomeCourseAdapter(
    private var course: List<Course>,
    private val listener: AdapterListener
) : RecyclerView.Adapter<HomeCourseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCourseAdapter.ViewHolder {
        val binding = AdapterHomeCourseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = course.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = course[position]

        holder.binding.title.text = item.title
        holder.binding.instructor.text = item.instructor
        holder.binding.job.text = item.job
        Glide.with(holder.itemView.context)
            .load(item.imageCourse)
            .placeholder(R.drawable.img_placeholder)
            .into(holder.binding.imageCourse)
        holder.binding.price.text = item.price

        // Set the instructor initial to the TextView
        val instructorInitial = getInitials(item.instructor)
        holder.binding.initial.text = instructorInitial

        holder.binding.container.setOnClickListener {
            listener.onClick(item)
        }
    }

    class ViewHolder(val binding: AdapterHomeCourseBinding) : RecyclerView.ViewHolder(binding.root)

    fun setData(data: List<Course>) {
        course = data
        notifyDataSetChanged()
    }

    interface AdapterListener {
        fun onClick(course: Course)
    }

    private fun getInitials(name: String): String {
        return name.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.toString() }
            .joinToString("")
            .uppercase()
    }
}
