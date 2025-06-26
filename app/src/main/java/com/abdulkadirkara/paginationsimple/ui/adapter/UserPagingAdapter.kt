package com.abdulkadirkara.paginationsimple.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abdulkadirkara.paginationsimple.databinding.ItemUsersBinding
import com.abdulkadirkara.paginationsimple.data.model.Result
import com.abdulkadirkara.paginationsimple.util.ImageShape
import com.abdulkadirkara.paginationsimple.util.loadImage
import javax.inject.Inject


class UserPagingAdapter @Inject constructor() : PagingDataAdapter<Result, UserPagingAdapter.UserPagingViewHolder>(UserComparator) {

    var onItemClick: ((Result) -> Unit)? = null

    inner class UserPagingViewHolder(private val binding: ItemUsersBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(item: Result){
            binding.apply {
                tvName.text = "${item.name.first} ${item.name.last}"
                tvMail.text = item.email
                tvPhone.text = item.phone
                ivPhoto.loadImage(
                    url = item.picture.large,
                    shape = ImageShape.CIRCLE
                )

                root.setOnClickListener {
                    onItemClick?.invoke(item)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: UserPagingViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPagingViewHolder {
        val binding = ItemUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserPagingViewHolder(binding)
    }

    object UserComparator : DiffUtil.ItemCallback<Result>(){
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.phone == newItem.phone
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }

    }

}