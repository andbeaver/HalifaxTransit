package com.example.halifaxtransit.ui

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteData(
    @PrimaryKey @ColumnInfo(name = "route_id") val routeId: String,
    @ColumnInfo(name = "route_long_name") val routeName: String,

    @ColumnInfo(name = "IsSelected", defaultValue = "0")
    val isSelected: Boolean = false
)
