package com.example.notetakingapp.data.fragment.list

import android.view.LayoutInflater
import androidx.core.view.MenuProvider
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.R
import com.example.notetakingapp.data.model.User
import com.example.notetakingapp.data.viewmodel.UserViewModel

class ListAdapter(private val userViewModel: UserViewModel) :
    ListAdapter<User, ListAdapter.MyViewHolder>(UserDiffCallback) {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
        val rowLayout: View = itemView.findViewById(R.id.rowLayout)

        fun bind(user: User) {
            titleText.text = user.title
            descriptionText.text = user.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

        holder.rowLayout.setOnClickListener {
            userViewModel.updateLastOpened(currentItem.id)
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.rowLayout.findNavController().navigate(action)
        }
    }

    companion object UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}