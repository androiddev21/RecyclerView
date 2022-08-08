package com.example.recyclerview.model.service

import com.example.recyclerview.model.entity.User
import com.example.recyclerview.utils.indexOfUser
import com.example.recyclerview.view.tasks.SimpleTask
import com.example.recyclerview.view.tasks.Task
import com.github.javafaker.Faker
import java.util.*
import java.util.concurrent.Callable
import kotlin.random.Random

typealias UsersListener = (users: List<User>) -> Unit

class UsersService {

    private var users = mutableListOf<User>()
    private var usersLoaded = false

    private val listeners = mutableSetOf<UsersListener>()

    fun loadUsers(): Task<Unit> = SimpleTask<Unit>(
        Callable {
            Thread.sleep(2000)
            val faker = Faker.instance()
            users = (1..100).map {
                User(
                    id = it.toLong(),
                    name = faker.name().name(),
                    company = faker.company().name(),
                    photo = IMAGES[Random.nextInt(0, IMAGES.size)]
                )
            }.toMutableList()
            usersLoaded = true
            notifyChanges()
        }
    )

    fun deleteUser(user: User): Task<Unit> = SimpleTask<Unit>(
        Callable {
            val indexToRemove = users.indexOfUser(user)
            if (indexToRemove == -1) return@Callable
            users.removeAt(indexToRemove)
            notifyChanges()
        })

    fun moveUser(user: User, moveBy: Int): Task<Unit> = SimpleTask<Unit>(
        Callable {
            val userCurrentIndex = users.indexOfUser(user)
            if (userCurrentIndex == -1) return@Callable
            val userNewIndex = userCurrentIndex + moveBy
            if (userNewIndex < 0 || userCurrentIndex >= users.size) return@Callable
            Collections.swap(users, userCurrentIndex, userNewIndex)
            notifyChanges()
        })

    fun fireUser(user: User): Task<Unit> = SimpleTask<Unit>(
        Callable {
            val index = users.indexOfUser(user)
            if (index == -1) return@Callable
            val updatedUser = users[index].copy(
                company = ""
            )
            users[index] = updatedUser
            notifyChanges()
        })

    fun addListener(usersListener: UsersListener) {
        listeners.add(usersListener)
        if (usersLoaded) {
            usersListener.invoke(users)
        }
    }

    fun removeListener(usersListener: UsersListener) {
        listeners.remove(usersListener)
    }

    private fun notifyChanges() {
        if (!usersLoaded) return
        listeners.forEach {
            it.invoke(users)
        }
    }

    companion object {
        private val IMAGES = mutableListOf(
            "https://images.unsplash.com/photo-1600267185393-e158a98703de?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NjQ0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1579710039144-85d6bdffddc9?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0Njk1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODE0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1620252655460-080dbec533ca?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzQ1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1613679074971-91fc27180061?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzUz&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1485795959911-ea5ebf41b6ae?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzU4&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1545996124-0501ebae84d0?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzY1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/flagged/photo-1568225061049-70fb3006b5be?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0Nzcy&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1567186937675-a5131c8a89ea?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODYx&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1546456073-92b9f0a8d413?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODY1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"
        )
    }
}