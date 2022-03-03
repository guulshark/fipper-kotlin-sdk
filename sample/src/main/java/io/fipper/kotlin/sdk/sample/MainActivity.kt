package io.fipper.kotlin.sdk.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import io.fipper.kotlin.sdk.*

class MainActivity : AppCompatActivity() {

    private val fipper: Fipper by lazy(LazyThreadSafetyMode.NONE) {
        Fipper(
            Rate.NORMAL,
            token = getString(R.string.fipper_token),
            environment = getString(R.string.fipper_environment),
            projectId = resources.getInteger(R.integer.fipper_projectId)
        )
    }

    private var configJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_suspend).setOnClickListener {
            if (configJob?.isActive == true) {
                Toast.makeText(it.context, "Retrieving config...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            configJob = lifecycleScope.launch {
                try {
                    fipper.getConfig().also {
                        Log.d("Fipper", "Suspend config: $it")
                    }
                    Toast.makeText(it.context, "Config retrieved ", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    Toast.makeText(it.context, "Config retrieve FAILED", Toast.LENGTH_SHORT).show()
                    ex.printStackTrace()
                }
            }
        }

        findViewById<Button>(R.id.btn_callback).setOnClickListener { view ->
            fipper.getConfig(
                object : FipperCallback {
                    override fun onSuccess(flags: List<Flag>) {
                        Log.d("Fipper", "Callback config: $flags")
                        Toast.makeText(view.context, "Config retrieved ", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(failure: FipperFailure) {
                        failure.printStackTrace()
                        Toast.makeText(view.context, "Config retrieve FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //fipper.release()
    }
}