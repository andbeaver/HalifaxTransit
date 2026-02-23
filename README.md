# Halifax Transit App

An Android application for Halifax Transit users that provides real-time bus tracking and route information on an interactive map.

## Features

*   **Real-time Bus Tracking:** View the live location of buses on an interactive map.
*   **Route Information:** Browse and select different bus routes to see their paths.
*   **User Location:** See your own location on the map to easily find nearby bus stops.
*   **Light and Dark Themes:** Switch between light and dark themes for optimal viewing comfort.
*   **Recenter Map:** Easily recenter the map on your current location.

## Technologies Used

*   [Kotlin](https://kotlinlang.org/): The primary programming language for the application.
*   [Jetpack Compose](https://developer.android.com/jetpack/compose): Android's modern toolkit for building native UI.
*   [Mapbox Maps SDK for Android](https://docs.mapbox.com/android/maps/): Used for displaying the interactive map and bus locations.
*   [GTFS (General Transit Feed Specification)](https://gtfs.org/): Used for real-time bus data.

## Getting Started

1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Create a `mapbox_access_token.xml` file in `app/src/main/res/values/` with your Mapbox access token:
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="mapbox_access_token" translatable="false">YOUR_MAPBOX_ACCESS_TOKEN</string>
    </resources>
    ```
4.  Build and run the application.
