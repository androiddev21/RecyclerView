package com.example.recyclerview.view

import com.example.recyclerview.model.entity.User

interface UserActionListener {
    fun onUserMove(user: User, moveBy: Int)
    fun onUserDelete(user: User)
    fun onUserFire(user: User)
}