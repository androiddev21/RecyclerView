package com.example.recyclerview.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recyclerview.App
import com.example.recyclerview.view.navigator.Navigator
import com.example.recyclerview.view.viewmodel.UsersListViewModel
import java.lang.IllegalStateException

//only need when view model has params in constructor
class ViewModelFactory(private val app: App): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when(modelClass){
            UsersListViewModel::class.java -> {
                UsersListViewModel(app.usersService)
            }
            else -> {
                throw IllegalStateException("Unknown view model!")
            }
        }
        return viewModel as T
    }
}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)

fun Fragment.navigator() = requireActivity() as Navigator