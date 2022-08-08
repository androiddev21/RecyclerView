package com.example.recyclerview.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.recyclerview.model.entity.User

class UsersDiffCallback(private val oldList: List<User>, private val newList: List<User>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}