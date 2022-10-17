package com.udacity.asteroidradar.repository

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Database.AsteroidDatabase
import com.udacity.asteroidradar.Database.DatabasePictureOfDay
import com.udacity.asteroidradar.Database.asDomainModel
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.NasaApiFilter
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.common.Constants
import com.udacity.asteroidradar.common.Constants.filter
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {
    private val calendar = Calendar.getInstance()
    private val currentTime = calendar.time
    private val dateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    } else {
        TODO("VERSION.SDK_INT < N")
    }
    private val today = dateFormat.format(currentTime)

    val asteroids:LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()){
        it.asDomainModel()
    }

    val pictureOfDay:LiveData<PictureOfDay> = Transformations.map(database.asteroidDao.getPictureOfTheDay(today)){
        it?.asDomainModel() ?: DatabasePictureOfDay("","","","").asDomainModel()
    }


    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroidList = if (filter == NasaApiFilter.SHOW_WEEK) {
                parseAsteroidsJsonResult(JSONObject(NasaApi.retrofitScalarsServer.get7DayesAsteroids(today, BuildConfig.API_KEY)))
            }else{
                parseAsteroidsJsonResult(JSONObject(NasaApi.retrofitScalarsServer.getTodayAsteroids(today,today,BuildConfig.API_KEY)))
            }
                database.asteroidDao.insertAll(*asteroidList.asDatabaseModel().toTypedArray())
        }
    }

    suspend fun deleteOldAsteroids(){
        withContext(Dispatchers.IO){
            database.asteroidDao.deleteAllAsteroids()
        }
    }

    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            val pictureOfDay = NasaApi.retrofitMoshiServer.getPictureOfDay(BuildConfig.API_KEY)
            database.asteroidDao.insertPictureOfTheDay(pictureOfDay.asDatabaseModel(today))
        }
    }
}