package com.example.halifaxtransit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.halifaxtransit.ui.RouteData
import com.example.halifaxtransit.ui.RouteDatabase
import com.example.halifaxtransit.ui.RouteViewModel
import com.example.halifaxtransit.ui.RouteViewModelFactory

@Composable
fun RouteScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val routeViewModel: RouteViewModel = viewModel(
        factory = RouteViewModelFactory(RouteDatabase.getDatabase(context).routeDao())
    )

    val routes by routeViewModel.allRoutes.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Routes around Halifax",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "See route to get to your destination!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Clear selection button
        item {
            OutlinedButton(onClick = { routeViewModel.clearAllSelections() }) {
                Text("Clear Selections")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display routes in a list
        items(routes) { route ->
            RouteCard(routeData = route,
                onCheckedChange = {
                    // Update the route's selection state
                    val updatedRoute = route.copy(isSelected = it)
                    routeViewModel.updateRoute(updatedRoute)
                })
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun RouteCard(routeData: RouteData, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    //var checked by remember { mutableStateOf(false) }
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            //containerColor = MaterialTheme.colorScheme.surfaceVariant,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,

        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Route: ${routeData.routeId}",
                    style = MaterialTheme.typography.titleLarge,
                    //color = MaterialTheme.colorScheme.onSurfaceVariant
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Checkbox(
                    checked = routeData.isSelected,
                    onCheckedChange = onCheckedChange)
                    }
            }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            Text(
                text = "Destination: ${routeData.routeName}",
                style = MaterialTheme.typography.bodyMedium,
                //color = MaterialTheme.colorScheme.onSurfaceVariant
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        }
    }
