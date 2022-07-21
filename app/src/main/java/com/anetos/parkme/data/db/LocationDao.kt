package  com.anetos.parkme.data.db

import androidx.room.*

@Dao
interface LocationDao {

    /*@Query("Select * from Location")
    fun getAll(): List<Location>

    @Query("Select * from Location where latitude = :lat and longitude =:lon")
    fun getByLatLon(lat: String, lon: String): Location

    @Query("Select * from Location where placeName = :placename")
    fun getByPlaceName(placename: String): Location

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(location: List<Location>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Location)

    @Delete
    fun delete(location: Location)*/
}