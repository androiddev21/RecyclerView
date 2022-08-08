package com.example.recyclerview.utils

import com.example.recyclerview.model.entity.User

fun List<User>.indexOfUser(user: User) = indexOfFirst {
    it.id == user.id
}