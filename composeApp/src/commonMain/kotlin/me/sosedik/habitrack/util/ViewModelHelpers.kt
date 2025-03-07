package me.sosedik.habitrack.util

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
fun <T> TextFieldState.observeMutableField(
    scope: CoroutineScope,
    defaultValue: T,
    validator: suspend (String) -> T
): MutableStateFlow<T> {
    val mutableStateFlow = MutableStateFlow(defaultValue)

    snapshotFlow { this.text }
        .debounce(500)
        .mapLatest { validator(it.toString()) }
        .onEach { mutableStateFlow.value = it }
        .launchIn(scope)

    return mutableStateFlow
}
