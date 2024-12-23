package net.schnall.compose.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import net.schnall.compose.repo.WeatherRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schnall.compose.data.Location
import net.schnall.compose.data.Weather

class MapViewModel(private val weatherRepo: WeatherRepo) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState>
        get() = _uiState

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            weatherRepo.listLocations()
                .catch { exception ->
                    _uiState.update {
                        it.copy(errorMessage = exception.message, isLoading = false)
                    }
                }
                .collect { locations ->
                    _uiState.update {
                        it.copy(locations = locations.sortedBy { it.name }, isLoading = false, errorMessage = null)
                    }
                }
        }
    }

    fun addLocation(point: Point) {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                currentLocation = null,
                weatherList = emptyList()
            )
        }

        viewModelScope.launch {
            weatherRepo.fetchLocation(lat = point.latitude(), lon = point.longitude())
                .catch { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = exception.message)
                    }
                }
                .collect { location ->
                    _uiState.update {
                        it.copy(currentLocation = location)
                    }
                    location?.let {
                        updateForecast(it)
                    } ?: run {
                        // Failed find location to reverse geocode to
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "Nearby location not found")
                        }
                    }
                }
        }
    }

    fun updateLocation(location: Location?) {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                currentLocation = location,
                weatherList = emptyList()
            )
        }

        location?.let {
            viewModelScope.launch {
                updateForecast(location)
            }
        } ?: run {
            _uiState.update {
                it.copy(
                    currentLocation = null,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    private suspend fun updateForecast(location: Location) {
        weatherRepo.fetchForecast(location.name)
            .catch { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }
            .collect { weatherList ->
                _uiState.update {
                    it.copy(
                        weatherList = weatherList,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
    }
}

data class MapUiState (
    val locations: List<Location> = emptyList(),
    val currentLocation: Location? = null,
    val weatherList: List<Weather> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)