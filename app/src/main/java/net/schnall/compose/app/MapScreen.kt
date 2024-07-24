package net.schnall.compose.app

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import net.schnall.compose.R
import net.schnall.compose.app.theme.ComposeStarterTheme
import net.schnall.compose.data.Location
import net.schnall.compose.data.Weather
import java.time.format.DateTimeFormatter

@OptIn(MapboxExperimental::class)
@Composable
fun MapScreen(
    uiState: MapUiState,
    showSnackbar: (String, SnackbarDuration) -> Unit,
    onMapClick: (Point) -> Boolean,
    onLocationChange: (Location?) -> Unit
) {
    val mapViewportState = rememberMapViewportState {
        // Set the initial camera position
        setCameraOptions {
            center(Point.fromLngLat(0.0, 0.0))
            zoom(2.0)
            pitch(0.0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.BottomStart
    ) {
        MyMap(
            currentLocation = uiState.currentLocation,
            mapViewportState = mapViewportState,
            onMapClick = onMapClick
        )
        LocationMenuBox(
            currentLocation = uiState.currentLocation,
            locations = uiState.locations,
            onLocationChange = onLocationChange,
            mapViewportState = mapViewportState
        )
        if (uiState.isLoading) {
            MyProgress(null)
        }

    }
    uiState.currentLocation?.let { location ->
        if (uiState.weatherList.isNotEmpty()) {
            Forecast(
                location.name,
                uiState.weatherList,
                onLocationChange
            )
        }
    }

    if (uiState.errorMessage != null) {
        showSnackbar(uiState.errorMessage, SnackbarDuration.Short)
    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun MyMap(
    currentLocation: Location?,
    mapViewportState: MapViewportState,
    onMapClick: (Point) -> Boolean
) {
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        onMapClickListener = onMapClick
    ) {
        currentLocation?.let { location ->
            Marker(Point.fromLngLat(location.lon, location.lat))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Forecast(
    locationName: String,
    weatherList: List<Weather>,
    onLocationChange: (Location?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            onLocationChange(null)
        },
        sheetState = sheetState
    ) {
        Text(
            text = locationName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        LazyRow {
            items(items = weatherList) { weather ->
                ForecastItem(
                    weather = weather
                )
            }
        }
    }
}

@Composable
fun ForecastItem(weather: Weather) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dateToDay(weather.dateTime),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = dateToTime(weather.dateTime),
            style = MaterialTheme.typography.titleSmall
        )
        AsyncImage(
            modifier = Modifier
                .height(64.dp)
                .width(64.dp),
            model = weatherIconToUrl(weather.icon),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "${weather.tempMax}° / ${weather.tempMin}°",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = weather.description,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.wind),
                contentDescription = null,
                modifier = Modifier.size(width = 16.dp, height = 16.dp)
            )
            Text(
                text = "${weather.windSpeed.toInt()} mph from ${windAngleToStr(weather.windDirection)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun LocationMenuBox(
    currentLocation: Location?,
    locations: List<Location>,
    onLocationChange: (Location?) -> Unit,
    mapViewportState: MapViewportState? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(currentLocation?.name ?: "") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text(stringResource(R.string.location)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            locations.forEach { location ->
                DropdownMenuItem(
                    text = { Text(text = location.name) },
                    onClick = {
                        selectedOptionText = location.name
                        expanded = false
                        onLocationChange(location)
                        mapViewportState?.flyTo(
                            cameraOptions = cameraOptions {
                                center(Point.fromLngLat(location.lon,  location.lat))
                            },
                            MapAnimationOptions.mapAnimationOptions { duration(2000) }
                        )
                    }
                )
            }
        }
    }
}

fun dateToDay(dateTime: LocalDateTime): String {
    if (dateTime.date == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) {
        return "Today"
    }
    return dateTime.dayOfWeek.name.lowercase().replaceFirstChar { c -> c.uppercase() }
}

fun dateToTime(dateTime: LocalDateTime): String {
    return DateTimeFormatter.ofPattern("h:mm a").format(dateTime.toJavaLocalDateTime())
}

fun windAngleToStr(degrees: Int): String {
    val sector = degrees / 45

    return when(sector) {
        1 -> "NE"
        2 -> "E"
        3 -> "SE"
        4 -> "S"
        5 -> "SW"
        6 -> "W"
        7 -> "NW"
        else -> "N"
    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun Marker(point: Point) {
    val context = LocalContext.current
    val marker = remember {
        AppCompatResources.getDrawable(context, R.drawable.marker)!!.toBitmap()
    }

    PointAnnotation(
        iconImageBitmap = marker,
        iconSize = 1.5,
        point = point
    )
}

// TODO extract to commonUi
@Composable
fun MyProgress(text: String? = stringResource(R.string.please_wait)) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp)
                .height(64.dp),
            color = MaterialTheme.colorScheme.primary
        )
        text?.let {
            Text(
                modifier = Modifier.padding(16.dp),
                text = it
            )
        }
    }
}

fun weatherIconToUrl(icon: String): String {
    return "https://openweathermap.org/img/wn/$icon@2x.png"
}

@OptIn(MapboxExperimental::class)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun LocationMenuBoxPreview() {
    val ny = Location("New York, NY, USA", 0.0, 0.0, forecastUpdated = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    val sf = Location("San Francisco, CA, USA", 0.0, 0.0, forecastUpdated = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    val la = Location("Los Angeles, CA, USA", 0.0, 0.0, forecastUpdated = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))

    ComposeStarterTheme {
        LocationMenuBox (
            currentLocation = la,
            locations = listOf(ny, sf, la),
            onLocationChange = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun ForecastItemPreview() {
    ComposeStarterTheme {
        ForecastItem(
            Weather(
                location = "Los Angeles, CA, USA",
                dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                description = "Clear Sky",
                icon = "01d",
                windDirection = 0,
                windSpeed = 1.0,
                tempMin = 64,
                tempMax = 76
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun ForecastPreview() {
    ComposeStarterTheme {
        Forecast(
            locationName = "Los Angeles, CA, USA",
            weatherList = listOf(
                Weather(
                    "Los Angeles, CA, USA",
                    dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    description = "Clear Sky",
                    icon = "01d",
                    windDirection = 0,
                    windSpeed = 1.0,
                    tempMin = 69,
                    tempMax = 80
                ),
                Weather(
                    "Los Angeles, CA, USA",
                    dateTime = Clock.System.now().plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault()),
                    description = "Few Clouds",
                    icon = "02d",
                    windDirection = 0,
                    windSpeed = 1.0,
                    tempMin = 64,
                    tempMax = 76
                ),
                Weather(
                    "Los Angeles, CA, USA",
                    dateTime = Clock.System.now().plus(2, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault()),
                    description = "Light Rain",
                    icon = "10d",
                    windDirection = 0,
                    windSpeed = 1.0,
                    tempMin = 60,
                    tempMax = 72
                )
            ),
            onLocationChange = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun MapScreenPreviewLoading() {
    ComposeStarterTheme {
       MapScreen(
           uiState = MapUiState(isLoading = true),
           showSnackbar = { _, _ -> },
           onMapClick = { _ -> true },
           onLocationChange = { _ -> }
       )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun MapScreenPreviewForecast() {
    val ny = Location("New York, NY, USA", 0.0, 0.0, forecastUpdated = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    val sf = Location("San Francisco, CA, USA", 0.0, 0.0, forecastUpdated = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    val la = Location("Los Angeles, CA, USA", 0.0, 0.0, forecastUpdated = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))

    ComposeStarterTheme {
        MapScreen(
            uiState = MapUiState(
                locations = listOf(ny, sf, la),
                currentLocation = la,
                weatherList = listOf(
                    Weather(
                        location = la.name,
                        dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                        description = "Clear Sky",
                        icon = "01d",
                        windDirection = 0,
                        windSpeed = 1.0,
                        tempMin = 69,
                        tempMax = 80
                    )
                )
            ),
            showSnackbar = { _, _ -> },
            onMapClick = { _ -> true },
            onLocationChange = { _ -> }
        )
    }
}