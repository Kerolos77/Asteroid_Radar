package com.udacity.asteroidradar.common

import com.udacity.asteroidradar.api.NasaApiFilter

object Constants {
    const val API_QUERY_DATE_FORMAT = "YYYY-MM-dd"
    const val DEFAULT_END_DATE_DAYS = 7
    const val BASE_URL = "https://api/nasa/gov/"
    var filter = NasaApiFilter.SHOW_WEEK
}