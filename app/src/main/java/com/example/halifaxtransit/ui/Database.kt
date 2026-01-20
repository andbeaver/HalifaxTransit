package com.example.halifaxtransit.ui

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.activity.result.launch
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// https://www.youtube.com/watch?v=-LNg-K7SncM
// Declare the database
@Database(entities = [RouteData::class], version = 1, exportSchema = false)
abstract class RouteDatabase : RoomDatabase() {
    // Connects the database to the DAO to give access to the queries
    abstract fun routeDao(): RouteDao

    //https://www.geeksforgeeks.org/kotlin/how-to-use-singleton-pattern-for-room-database-in-android/
    // Creates instance of the database
    companion object {
        @Volatile
        private var INSTANCE: RouteDatabase? = null
        // Returns the singleton instance of the database
        fun getDatabase(context: Context): RouteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RouteDatabase::class.java,
                    "routes_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()

                INSTANCE = instance
                instance
            }
        }
    } // End of Companion Object

    // Populate the database
    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        // Called when the database is created for the first time
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Populate the database with data from the CSV file
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabaseFromCsv(context, database.routeDao())
                }
            }
        }

        // Populate the database with data from the CSV file
        private suspend fun populateDatabaseFromCsv(context: Context, routeDao: RouteDao) {
            val inputStream = context.assets.open("routes.csv")
            val reader = inputStream.bufferedReader()

            reader.readLine() // Skip header

            // Loop to read each line of the CSV file
            reader.forEachLine { line ->
                val tokens = line.split(",")
                if (tokens.size >= 2) {
                    val route = RouteData(
                        routeId = tokens[0].trim(),
                        routeName = tokens[1].trim(),
                        isSelected = false
                    )
                    // Insert the route into the database
                    CoroutineScope(Dispatchers.IO).launch {
                        routeDao.insertRoute(route)
                    }
                }
            }
        }
    }
}