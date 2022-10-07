package com.seif.coroutinecancellationandexceptionhandling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
