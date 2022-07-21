package  com.anetos.parkme.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


//@Database(entities = [/*Location::class, SafeRoute::class*/], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun routeDao(): RouteDao

    companion object {
        private const val DB_NAME = "Bangalore-Weather1"
        private var appDb: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (null == appDb) {
                appDb = buildDatabaseInstance(context)
            }
            return appDb as AppDatabase
        }

        private fun buildDatabaseInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}