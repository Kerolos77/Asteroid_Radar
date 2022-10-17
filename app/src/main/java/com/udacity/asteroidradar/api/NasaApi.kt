package com.udacity.asteroidradar.api
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.models.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.nasa.gov/"

private val moshi =Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val httpClient = OkHttpClient.Builder()
    .addInterceptor( HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    ).build()

private val retrofitMoshi= Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
//    .client(httpClient)
    .build()

private val retrofitScalars= Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
//    .client(httpClient)
    .build()

enum class NasaApiFilter(val value: String) { SHOW_TODAY("today"), SHOW_WEEK("week"), SHOW_SAVE("save") }

interface NasaApiService{
    @GET("neo/rest/v1/feed")
    suspend fun get7DayesAsteroids(
        @Query("start_date") start_date:String,
        @Query("api_key") api_key:String
        ): String

    @GET("neo/rest/v1/feed")
    suspend fun getTodayAsteroids(
        @Query("start_date") start_date:String,
        @Query("end_date") end_date:String,
        @Query("api_key") api_key:String
    ): String


    @GET("planetary/apod")
    suspend fun getPictureOfDay(
        @Query("api_key") api_key:String
    ): PictureOfDay
}

object NasaApi {
    val retrofitMoshiServer :NasaApiService by lazy {
        retrofitMoshi.create(NasaApiService::class.java)
    }
    val retrofitScalarsServer :NasaApiService by lazy {
        retrofitScalars.create(NasaApiService::class.java)
    }
}