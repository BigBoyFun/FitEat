package com.policinski.dev.fiteat

import android.app.*
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDate
import java.util.*
import kotlin.math.abs

private val MAIN_PREF = "MAIN_PREF"
private val PREF_CURRENTLY_VIEWED_LIST = "PREF_CURRENTLY_VIEWED_LIST" //created for deleting product from specific meal(not from DB)

class MainActivity : AppCompatActivity() {

    var x1: Float = 0.0F
    var x2: Float = 0.0F
    var distance: Float = 150.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!File("${this.applicationInfo.dataDir}/databases/PRODUCTS_SQLite_DB").exists()) copyDataBaseFromFile()

        val readDatabase = MyDatabaseHelper(this)
        readDatabase.readAllData()

        //create and setup bottom nav controller
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)

        bottomNavigationView.setupWithNavController(navController)

        val mealArray = arrayOf("Breakfast","Second breakfast","Dinner","Dessert","Tea","Supper","Snacks","Training" )

        for (name in mealArray){
            createNotificationChannel(name)
        }

        getSharedPreferences(MAIN_PREF,0).edit().putString(PREF_CURRENTLY_VIEWED_LIST,"All").apply()

    }

    fun copyDataBaseFromFile(){

        val appDataPath = this.applicationInfo.dataDir

        val dbFolder = File("$appDataPath/databases") //Make sure the /databases folder exists

        dbFolder.mkdir() //This can be called multiple times.

        val dbFilePath = File("$appDataPath/databases/PRODUCTS_SQLite_DB")

        try {
            val inputStream = this.assets.open("PRODUCTS_SQLite_DB")
            val outputStream: OutputStream = FileOutputStream(dbFilePath)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            //handle
        }
    }

    private fun createNotificationChannel(mealName: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(mealName, "${mealName + "mealChanel"}", importance).apply {
                description = descriptionText
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
