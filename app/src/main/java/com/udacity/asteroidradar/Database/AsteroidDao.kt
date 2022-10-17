package com.udacity.asteroidradar.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDao {
    @Query("select * from databaseAsteroid")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("select * from databasePictureOfDay where date = :date")
    fun getPictureOfTheDay(date:String): LiveData<DatabasePictureOfDay>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfTheDay(pictureOfDay: DatabasePictureOfDay)

    @Query("delete from databaseAsteroid")
    fun deleteAllAsteroids()

}