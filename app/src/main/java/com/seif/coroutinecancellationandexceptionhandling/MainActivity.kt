package com.seif.coroutinecancellationandexceptionhandling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // coroutineExceptionHandler()
        // coroutineScopeExample()
        coroutineSuperVisorScopeExample()


    }

    private fun coroutineScopeExample() {
        val handler = CoroutineExceptionHandler { _, throwable ->
            println("Caught Exception: $throwable")
        }
        CoroutineScope(Dispatchers.Main + handler).launch {
            launch {
                delay(300L)
                throw Exception("Coroutine 1 failed")
            }
            launch {
                delay(400L)
                println("Coroutine 2 finished")
            }
        }
    }

    private fun coroutineSuperVisorScopeExample() {
        val handler = CoroutineExceptionHandler { _, throwable ->
            println("Caught Exception: $throwable")
        }
        CoroutineScope(Dispatchers.Main + handler).launch {
            supervisorScope {
                launch {
                    delay(300L)
                    throw Exception("Coroutine 1 failed")
                }
                launch {
                    delay(400L)
                    println("Coroutine 2 finished")
                }
            }
        }
    }

    private fun coroutineExceptionHandler() {
        // coroutineContext: used to give us information about the specific coroutine that through that exception
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("Caught Exception: $throwable")
        } // if we cancelled a coroutine this block will not be fired bec it only be handled for uncaught exceptions
        lifecycleScope.launch(handler) {
            throw Exception("Error")
        }
    }
}
// when we throw an exception in a child coroutine then this exception propagates up until it reached the root
// cancellation exceptions are handled by default

// coroutineScope: (Used to create a custom Scope) As soon as one coroutine fails (throw an exception) no matter if you handle that exception or not
// it will cancel all it's child coroutines and then the whole scope

// superVisorScope: if one coroutine in that scope is failed (throw an exception) that won't have any effect on the other coroutines

// another way to launch a coroutine is by using async{} we use it when we interested with a return value as async{} returns this value wrapped in a deffered