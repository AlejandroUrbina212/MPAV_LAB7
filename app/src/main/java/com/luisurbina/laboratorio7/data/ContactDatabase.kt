package com.luisurbina.laboratorio7.data

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Contact::class], version = 1)
abstract class ContactDatabase: RoomDatabase() {


    abstract fun contactDao(): ContactDao


    companion object {
        //Instance of the database
        private var instance: ContactDatabase? = null

        //method to ensure the database is created only once  (Like the singleton design pattern)
        fun getInstance(context: Context): ContactDatabase?{
            if (instance == null){
                synchronized(ContactDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ContactDatabase::class.java, "contact_database"
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        .build()
                }
            }
            return instance
        }
        fun destroyInstance() {
            instance = null
        }
        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(instance)
                    .execute()
            }
        }

        //Async task to fill de contact Database (doesnt interrupt the main thread of the application)
        class PopulateDbAsyncTask(db: ContactDatabase?) : AsyncTask<Unit, Unit, Unit>(){
            private val contactDao = db?.contactDao()
            override fun doInBackground(vararg p0: Unit?) {
                //Dummy data to fill the contact database
                contactDao?.insert(Contact("Luis Urbina", "44891647", "luis212urbina@gmail.com", 1))
                contactDao?.insert(Contact("Jorge Sánchez", "22118344", "jorgeMar@gmail.com", 1))
            }


        }


    }



}