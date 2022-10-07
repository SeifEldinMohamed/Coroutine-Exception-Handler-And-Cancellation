package com.seif.coroutinecancellationandexceptionhandling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import java.net.HttpRetryException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // coroutineExceptionHandler()
        // coroutineScopeExample()
        // coroutineSuperVisorScopeExample()
        // commonMistake()
        // firstSolution()
        secondSolution()


    }

    private fun secondSolution() {
        lifecycleScope.launch {
            val job = launch {
                try {
                    delay(500L)
                } catch (e: Exception) {
                    if (e is CancellationException) {
                        throw e
                    }
                    e.printStackTrace() // t's a method on Exception instances that prints the stack trace of the instance to System.err. It's a very simple, but very useful tool for diagnosing an exceptions. It tells you what happened and where in the code this happened.
                }
                println("Coroutine 1 Finished")
            }
            delay(300L)
            job.cancel()
        }
        /** Solution 2 **/
        // we check if this exception is CancellationException then we rethrow it so it propagate up as usual
    }

    private fun firstSolution() {
        lifecycleScope.launch {
            val job = launch {
                try {
                    delay(500L)
                } catch (e: HttpRetryException) {
                    e.printStackTrace() // t's a method on Exception instances that prints the stack trace of the instance to System.err. It's a very simple, but very useful tool for diagnosing an exceptions. It tells you what happened and where in the code this happened.
                }
                println("Coroutine 1 Finished")
            }
            delay(300L)
            job.cancel()
        }
        /** Solution 1 **/
        // we only catch the HTTP Exception so the Cancellation Exception will propagate up as usual
    }

    private fun commonMistake() {
        lifecycleScope.launch {
            val job = launch {
                try {
                    delay(500L)
                } catch (e: Exception) {
                    e.printStackTrace() // t's a method on Exception instances that prints the stack trace of the instance to System.err. It's a very simple, but very useful tool for diagnosing an exceptions. It tells you what happened and where in the code this happened.
                }
                println("Coroutine 1 Finished")
            }
            delay(300L)
            job.cancel()
        }
        /** Problem **/
        // why we still see "Coroutine 1 Finished" even when we cancel this coroutine
        // because when this coroutine cancelled the current function
        // that is suspending tha specific coroutine (in this case is delay)
        // will throw a Cancellation Exception but since that delay executed
        // in a try and catch block that cancellation exception would be eaten up
        // by that try and catch block so it's not propagate up any more so
        // this outer coroutine scope doesn't know that this child coroutine was cancelled
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