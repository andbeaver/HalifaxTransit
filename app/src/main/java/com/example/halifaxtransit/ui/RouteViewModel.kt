package com.example.halifaxtransit.ui
// New model to manage the data and interactions with the database for the Routes
import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// https://www.youtube.com/watch?v=-LNg-K7SncM
// ViewModel to manage the database operations
class RouteViewModel(private val routeDao: RouteDao) : ViewModel() {

    // Get all routes from the database
    val allRoutes: Flow<List<RouteData>> = routeDao.getAllRoutes()

    // Update a routes information in the database
    fun updateRoute(route: RouteData) {
        viewModelScope.launch {
            routeDao.updateRoute(route)
        }
    }
    // Clear all selections in the database
    fun clearAllSelections() {
        viewModelScope.launch {
            routeDao.clearAllSelections()
        }
    }
}

// Factory to create an instance of the ViewModel
class RouteViewModelFactory(private val dao: RouteDao) : ViewModelProvider.Factory {
    // Creates a new ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // If the model is of type RouteViewModel then create it and pass in the DAO
        if (modelClass.isAssignableFrom(RouteViewModel::class.java)) {
            //
            @Suppress("UNCHECKED_CAST")
            return RouteViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}