package com.tutorials.eu.workmanagersample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnOneTimeRequest = findViewById<Button>(R.id.btnOneTimeRequest)
        val tvOneTimeRequest = findViewById<TextView>(R.id.tvOneTimeRequest)

        // TODO Step 2: Create an instance of Periodic Button
        // START
        val btnPeriodicRequest = findViewById<Button>(R.id.btnPeriodicRequest)
        // END

        btnOneTimeRequest.setOnClickListener {

            /**
             * A specification of the requirements that need to be met before a WorkRequest can run.  By
             * default, WorkRequests do not have any requirements and can run immediately.  By adding
             * requirements, you can make sure that work only runs in certain situations - for example, when you
             * have an unmetered network and are charging.
             */
            val oneTimeRequestConstraints = Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // Define the input data for work manager
            // A persistable set of key/value pairs which are used as inputs and outputs for ListenableWorkers.
            val data = Data.Builder()
            data.putString("inputKey", "Input Value")

            // Create an one time work request
            /**
             * A WorkRequest for non-repeating work.
             * OneTimeWorkRequests can be put in simple or complex graphs of work by using methods.
             */
            val sampleWork = OneTimeWorkRequest
                .Builder(OneTimeRequestWorker::class.java)
                /**
                 * Adds input Data to the work.  If a worker has prerequisites in its chain, this
                 * Data will be merged with the outputs of the prerequisites using an InputMerger.
                 *
                 * @param inputData key/value pairs that will be provided to the worker
                 */
                .setInputData(data.build())
                /**
                 * Adds constraints to the WorkRequest.
                 */
                .setConstraints(oneTimeRequestConstraints)
                // Builds a {@link WorkRequest} based on this {@link Builder}
                .build()

            // Retrieves the default singleton instance of WorkManager and Enqueues one item for background processing.
            WorkManager.getInstance(this@MainActivity).enqueue((sampleWork))

            // Gets a LiveData of the WorkInfo for a given work id.
            WorkManager.getInstance(this@MainActivity).getWorkInfoByIdLiveData(sampleWork.id)
                .observe(this, Observer { workInfo ->
                    OneTimeRequestWorker.Companion.logger(workInfo.state.name)
                    if (workInfo != null) {
                        when (workInfo.state) {
                            WorkInfo.State.ENQUEUED -> {
                                // Show the work state in text view
                                tvOneTimeRequest.text = "Task enqueued."
                            }
                            WorkInfo.State.BLOCKED -> {
                                tvOneTimeRequest.text = "Task blocked."
                            }
                            WorkInfo.State.RUNNING -> {
                                tvOneTimeRequest.text = "Task running."
                            }
                            else -> {
                                tvOneTimeRequest.text = "Task state else part."
                            }
                        }
                    }

                    // When work finished
                    if (workInfo != null && workInfo.state.isFinished) {
                        when (workInfo.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                tvOneTimeRequest.text = "Task successful."

                                // Get the output data
                                val successOutputData = workInfo.outputData
                                val outputText = successOutputData.getString("outputKey")
                                Log.i("Worker Output", "$outputText")
                            }
                            WorkInfo.State.FAILED -> {
                                tvOneTimeRequest.text = "Task Failed."
                            }
                            WorkInfo.State.CANCELLED -> {
                                tvOneTimeRequest.text = "Task cancelled."
                            }
                            else -> {
                                tvOneTimeRequest.text = "Task state isFinished else part."
                            }
                        }
                    }
                })
        }

        // TODO Step 5: Assign the click even for Periodic Work Request. And Enqueue the worker class.,
        btnPeriodicRequest.setOnClickListener {

            /**
             * Constraints ensure that work is deferred until optimal conditions are met.
             *
             * A specification of the requirements that need to be met before a WorkRequest can run.
             * By default, WorkRequests do not have any requirements and can run immediately.
             * By adding requirements, you can make sure that work only runs in certain situations
             * - for example, when you have an unmetered network and are charging.
             */
            // For more details visit the link https://medium.com/androiddevelopers/introducing-workmanager-2083bcfc4712
            val periodicRequestConstraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            // Create an Periodic Work Request
            /**
             * You can use any of the work request builder that are available to use.
             * We will you the PeriodicWorkRequestBuilder as we want to execute the code periodically.
             *
             * The minimum time you can set is 15 minutes. You can check the same on the below link.
             * https://developer.android.com/reference/androidx/work/PeriodicWorkRequest
             *
             * You can also set the TimeUnit as per your requirement. for example SECONDS, MINUTES, or HOURS.
             */
            // setting period to 15 Minutes
            val periodicWorkRequest =
                PeriodicWorkRequest.Builder(PeriodicRequestWorker::class.java, 15, TimeUnit.MINUTES)
                    .setConstraints(periodicRequestConstraints)
                    .build()

            /* Enqueue a work, ExistingPeriodicWorkPolicy.KEEP means that if this work already exists, it will be kept
        if the value is ExistingPeriodicWorkPolicy.REPLACE, then the work will be replaced */
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "Periodic Work Request",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
        }
    }
}