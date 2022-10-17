package com.udacity.asteroidradar.ui.main

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Database.getDatabase
import com.udacity.asteroidradar.api.NasaApiFilter
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val databaseAsteroids = getDatabase(application)
    private val asteroidsRepository = AsteroidRepository(databaseAsteroids)
    val asteroids = asteroidsRepository.asteroids
    val pictureOfDay = asteroidsRepository.pictureOfDay

    private val _navigateToSelectedAsteroid= MutableLiveData<Asteroid?>()
    val navigateToSelectedAsteroid: LiveData<Asteroid?>

        get() = _navigateToSelectedAsteroid
    init {
        viewModelScope.launch {
           try {
               if (asteroidsRepository.asteroids.value.isNullOrEmpty()) {
                   asteroidsRepository.refreshAsteroids()
               }
               asteroidsRepository.refreshPictureOfDay()
           }catch (e: Exception){
               Log.i("MainViewModel", "Error: ${e.message}")
           }
        }
    }


    fun updateFilter() {
        viewModelScope.launch {
            try {
                asteroidsRepository.deleteOldAsteroids()
                asteroidsRepository.refreshAsteroids()
            }catch (e: Exception){
                Log.i("MainViewModel", "Error: ${e.message}")
            }
        }
    }
    fun displayAsteroidDetails(asteroid:Asteroid){
        _navigateToSelectedAsteroid.value=asteroid
    }
    fun displayAsteroidDetailsComplete(){
        _navigateToSelectedAsteroid.value=null
    }

}