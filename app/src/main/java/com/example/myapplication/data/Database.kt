package com.example.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [PostEntity::class, ItemEntity::class, LogisticsEntity::class, PeopleEntity::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun itemDao(): ItemDao
    abstract fun logisticsDao(): LogisticsDao
    abstract fun peopleDao(): PeopleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null


        fun getDatabase(context: Context): AppDatabase {
            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {

                    db.execSQL("""
            CREATE TABLE IF NOT EXISTS people (
                peopleId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                postOwnerId INTEGER NOT NULL,
                description TEXT NOT NULL,
                amount TEXT NOT NULL,
                weight TEXT NOT NULL,
                peopleAmount TEXT NOT NULL,
                arrivePay TEXT NOT NULL,
                departDate TEXT,
                arriveDate TEXT,
                departPeople TEXT NOT NULL,
                arrivePeople TEXT NOT NULL,
                FOREIGN KEY(postOwnerId) REFERENCES posts(postId) ON DELETE CASCADE
            )
        """)

                    db.execSQL("CREATE INDEX IF NOT EXISTS index_people_postOwnerId ON people(postOwnerId)")
                }
            }


            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "post_database"
                ).addMigrations(MIGRATION_1_2)
                    .build().also { INSTANCE = it }
            }
        }
    }
}
