package com.example.recyclerview.view.tasks

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

private val executorService = Executors.newCachedThreadPool()
private val handler = Handler(Looper.getMainLooper())

/**
 * Executing code of [callable] in a separate thread.
 * Callable results are delivered to callbacks assigned via [onSuccess] and [onError]
 */
class SimpleTask<T>(callable: Callable<T>): Task<T> {

    private val future: Future<*>

    //Star-projections
    //Sometimes you want to say that you know nothing about the type argument, but you still want to use it in a safe way
    //Additionally List<*> can contain objects of any type, but only that type, so it can contain Strings (but only Strings),
    // while List<Any> can contain Strings and Integers and whatnot, all in the same list.
    private var result: Result<T> = PendingResult()

    init {
        future = executorService.submit {
            result = try {
                SuccessResult(callable.call())
            } catch (e: Exception){
                ErrorResult(e)
            }
        }
    }

    private var valueCallback: Callback<T>? = null
    private var errorCallback: Callback<Throwable>? = null

    override fun onSuccess(callback: Callback<T>): Task<T> {
        this.valueCallback = callback
        notifyListeners()
        return this
    }

    override fun onError(callback: Callback<Throwable>): Task<T> {
        this.errorCallback = callback
        notifyListeners()
        return this
    }

    override fun cancel() {
        clear()
        future.cancel(true)
    }

    override fun await(): T {
        future.get()
        val result = this.result
        if (result is SuccessResult) return result.data
        else throw (result as ErrorResult).error
    }

    private fun notifyListeners() {
        handler.post {
            val result = this.result
            val callback = this.valueCallback
            val errorCallback = this.errorCallback
            if (result is SuccessResult && callback != null) {
                callback(result.data)
                clear()
            } else if (result is ErrorResult && errorCallback != null) {
                errorCallback.invoke(result.error)
                clear()
            }
        }
    }

    private fun clear() {
        valueCallback = null
        errorCallback = null
    }
}