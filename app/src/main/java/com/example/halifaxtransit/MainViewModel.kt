package com.example.halifaxtransit

import android.util.Log
//import androidx.compose.ui.test.filter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.halifaxtransit.ui.RouteDao
import com.google.transit.realtime.GtfsRealtime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainViewModel(private val routeDao: RouteDao): ViewModel() {
    private val _gtfs = MutableStateFlow<GtfsRealtime.FeedMessage?>(null)
    val gtfs = _gtfs.asStateFlow()

    // This Flow provides the list of selected route IDs to the Map screen
    val selectedRouteIds: Flow<List<String>> = routeDao.getAllRoutes()
        .map { routes ->
            // This transforms the list of RouteData objects into a simple list of strings
            routes.filter { it.isSelected }.map { it.routeId }
        }

    // Get the Halifax transit bus positions
    fun loadGtfsBusPositions() {
        viewModelScope.launch {
            try {
                val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")

                // Run blocking network code on a background thread
                val feed = withContext(Dispatchers.IO) {
                    GtfsRealtime.FeedMessage.parseFrom(url.openStream())
                }
                _gtfs.value = feed
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading GTFS data: ${e.message}")
            }
        }
    }
}

// Factory to create an instance of the ViewModel that requires the dao
class MainViewModelFactory(private val dao: RouteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the modelClass is assignable from MainViewModel
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}