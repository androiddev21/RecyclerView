package com.example.recyclerview.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recyclerview.R
import com.example.recyclerview.model.entity.User
import com.example.recyclerview.model.service.UsersListener
import com.example.recyclerview.model.service.UsersService
import com.example.recyclerview.view.UserActionListener
import com.example.recyclerview.view.base.BaseViewModel
import com.example.recyclerview.view.base.Event
import com.example.recyclerview.view.tasks.*

data class UsersListItem(val user: User, val isInProgress: Boolean)

class UsersListViewModel(private val usersService: UsersService) : BaseViewModel(),
    UserActionListener {

    private val _users = MutableLiveData<Result<List<UsersListItem>>>()
    val users: LiveData<Result<List<UsersListItem>>> = _users

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val usersListener: UsersListener = { users ->
        usersResult = if (users.isEmpty()) EmptyResult() else SuccessResult(users)
    }

    //view model state
    private val usersIdsInProgress = mutableSetOf<Long>()
    private var usersResult: Result<List<User>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    init {
        usersService.addListener(usersListener)
        loadUsers()
    }

    override fun onCleared() {
        super.onCleared()
        usersService.removeListener(usersListener)
    }

    fun loadUsers() {
        usersResult = PendingResult()
        usersService.loadUsers()
            .onError {
                usersResult = ErrorResult(it)
            }.autoCancel()
    }

    override fun onUserMove(user: User, moveBy: Int) {
        if (isInProgress(user)) return
        addProgressTo(user)
        usersService.moveUser(user, moveBy)
            .onSuccess {
                removeProgressFrom(user)
            }
            .onError {
                removeProgressFrom(user)
                _actionShowToast.value = Event(R.string.cant_move_user)
            }.autoCancel()
    }

    override fun onUserDelete(user: User) {
        if (isInProgress(user)) return
        addProgressTo(user)
        usersService.deleteUser(user)
            .onSuccess {
                removeProgressFrom(user)
            }
            .onError {
                removeProgressFrom(user)
                _actionShowToast.value = Event(R.string.cant_delete_user)
            }.autoCancel()
    }

    override fun onUserFire(user: User) {
        if (isInProgress(user)) return
        addProgressTo(user)
        usersService.fireUser(user)
            .onSuccess {
                removeProgressFrom(user)
            }
            .onError {
                removeProgressFrom(user)
                _actionShowToast.value = Event(R.string.cant_fire_user)
            }.autoCancel()
    }

    private fun addProgressTo(user: User) {
        usersIdsInProgress.add(user.id)
        notifyUpdates()
    }

    private fun removeProgressFrom(user: User) {
        usersIdsInProgress.remove(user.id)
        notifyUpdates()
    }

    private fun isInProgress(user: User): Boolean {
        return usersIdsInProgress.contains(user.id)
    }

    private fun notifyUpdates() {
        //postValue - для случаев, когда результат получаем в другом потоке, но с
        //live data адо работать в главном потоке
        _users.postValue(usersResult.map { users ->
            users.map { user ->
                UsersListItem(
                    user = user,
                    isInProgress = isInProgress(user)
                )
            }
        })
    }
}