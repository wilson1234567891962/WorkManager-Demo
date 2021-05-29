package com.tutorials.eu.workmanagersample

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*

// TODO Step 3: Create a worker class for Periodic Work Request.
// START
class PeriodicRequestWorker(context: Context, params: WorkerParameters) : Worker(context, params) {


    override fun doWork(): Result {

        // TODO Step 6: Print the date in log when the function is called.
        // START
        val date = getDate(System.currentTimeMillis())
        Log.i("Periodic WorkRequest", "doWork Execution DateTime: $date")

        return Result.success()
        // END
    }

    // TODO Step 4: Create a function to get the date from the Milliseconds.
    // START
    /**
     * A function to get the date from the Milliseconds.
     *
     * @param milliSeconds
     */
    private fun getDate(milliSeconds: Long): String {

        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS", Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
    // END
}
// END