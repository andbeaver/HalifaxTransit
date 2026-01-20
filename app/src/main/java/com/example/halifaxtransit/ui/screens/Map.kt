package com.example.halifaxtransit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.halifaxtransit.MainViewModel
import com.example.halifaxtransit.R
import com.example.halifaxtransit.ui.theme.blackberryColor
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions

@Composable
fun MapScreen(mainViewModel: MainViewModel, mapViewportState: MapViewportState, modifier: Modifier = Modifier){
    DisplayMap(mainViewModel = mainViewModel, mapViewportState = mapViewportState, modifier = modifier)
}

@Composable
fun DisplayMap(mainViewModel: MainViewModel, mapViewportState: MapViewportState, modifier: Modifier = Modifier) {

    // Get entities (bus positions) from ViewModel
    val gtfsFeed by mainViewModel.gtfs.collectAsState()
    val busPositions = gtfsFeed?.entityList


    val selectedRoutes by mainViewModel.selectedRouteIds.collectAsState(initial = emptyList<String>())

//    val mapViewportState = rememberMapViewportState {
//        // set default camera zoom on Halifax centre
//        setCameraOptions {
//            zoom(12.0)
//            center(Point.fromLngLat(-63.5826, 44.6510))
//            pitch(0.0)
//            bearing(0.0)
//        }
//    }

    MapboxMap(
        mapViewportState = mapViewportState,
    ) {
        // Map effect will take effect once permission is granted to display user's location.
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            mapViewportState.transitionToFollowPuckState()
        }

        // Display bus locations
        if (busPositions != null) {
            for(bus in busPositions){
                val routeId = bus.vehicle.trip.routeId

                // Determine if the current bus's route is in the selected list
                val isSelected = selectedRoutes.contains(routeId)

                // Choose the icon based on whether the route is selected
                val icon = if (isSelected) {
                    R.drawable.busblue // Use the blue bus icon
                } else {
                    R.drawable.bus       // Use the default bus icon
                }

                // Insert a ViewAnnotation at specific geo coordinate.
                ViewAnnotation(
                    options = viewAnnotationOptions {
                        // View annotation is placed at the specific geo coordinate
                        geometry(Point.fromLngLat(bus.vehicle.position.longitude.toDouble(),
                            bus.vehicle.position.latitude.toDouble()))
                    }
                ) {
                    // ViewAnnotation content
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = "Route $routeId",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Text(
                            text = routeId,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = blackberryColor,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
} // End DisplayMap