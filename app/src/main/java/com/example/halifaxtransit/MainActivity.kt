package com.example.halifaxtransit

import android.Manifest
import android.app.Notification
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.halifaxtransit.ui.RouteDatabase
import com.example.halifaxtransit.ui.screens.MapScreen
import com.example.halifaxtransit.ui.screens.RouteScreen
import com.example.halifaxtransit.ui.theme.HalifaxTransitTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import kotlin.getValue


class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(RouteDatabase.getDatabase(this).routeDao())
    }

    // Request permission to get location. Register for the 'activity result'. This handles the permission request and its result.
    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            if (isGranted) {
                Log.i("TESTING", "New permission granted by user, proceed...")
            } else {
                Log.i("TESTING", "Permission DENIED by user! Display toast...")

                Toast.makeText(
                    this,
                    "Please enable location permission in Settings to use this feature.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // load bus positions from GTFS
        mainViewModel.loadGtfsBusPositions()

        setContent {
            val context = LocalContext.current

            // Check if permission granted
            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("TESTING", "Permission previously granted, proceed...")
                } else {
                    Log.i("TESTING", "Permission not yet granted, launching permission request...")
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            HalifaxTransitTheme (
                darkTheme = isDarkTheme,
                dynamicColor = false
            ){

                val navController = rememberNavController()
                var selectedIndex by remember { mutableIntStateOf(0) }
                val mapViewportState =
                    rememberMapViewportState {
                    // set default camera zoom on Halifax centre
                    setCameraOptions {
                        zoom(12.0)
                        center(Point.fromLngLat(-63.5826, 44.6510))
                        pitch(0.0)
                        bearing(0.0)
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Halifax Transit") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                //https://www.brandcolorcode.com/halifax
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            // Added recenter button
                            // https://docs.mapbox.com/android/maps/api/11.7.2/mapbox-maps-android/com.mapbox.maps.extension.compose.animation.viewport/-map-viewport-state/transition-to-follow-puck-state.html
                            // https://docs.mapbox.com/android/maps/guides/user-location/location-on-map/
                            actions = {
                                IconButton(onClick = { mapViewportState.transitionToFollowPuckState() }) {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_center_focus_strong_24),
                                        contentDescription = "Recenter Map",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                IconButton(onClick = { isDarkTheme = !isDarkTheme }){
                                    Icon(
                                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "Toggle Theme",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }

                            )

                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_action_map),
                                        contentDescription = "Map"
                                    )
                                },
                                label = { Text("Map") },
                                selected = selectedIndex == 0,
                                onClick = {
                                    selectedIndex = 0
                                    navController.navigate("Map")
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    indicatorColor = Color(0xFF3E4E32)
                                )
                            )
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_action_route),
                                        contentDescription = "Route"
                                    )
                                },
                                label = { Text("Routes") },
                                selected = selectedIndex == 1,
                                onClick = {
                                    selectedIndex = 1
                                    navController.navigate("Route")
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
//                                    unselectedTextColor = Color.LightGray,
//                                    unselectedIconColor = Color.LightGray,
//                                    selectedTextColor = Color.White,
//                                    selectedIconColor = Color.White,
                                    indicatorColor = Color(0xFF3E4E32)
                                )
                            )

                        }
                    }

                )
                { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "Map",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("Map") {
                            MapScreen(
                                mainViewModel = mainViewModel,
                                // Pass the map viewport state down to the MapScreen
                                mapViewportState = mapViewportState
                            )
                        }
                        composable("Route") {
                            RouteScreen()
                        }
                    }
                }
            }
        }
    } // End onCreate
} // End MainActivity

