package net.schnall.compose.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(
): ViewModel() {
    private val _screenName = MutableStateFlow<String>("")
    val screenName = _screenName.asStateFlow()

    fun updateScreenName(title: String) {
        _screenName.value = title
    }
}