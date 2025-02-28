package me.sosedik.habitrack

import androidx.compose.ui.window.ComposeUIViewController
import me.sosedik.habitrack.app.App
import me.sosedik.habitrack.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }
