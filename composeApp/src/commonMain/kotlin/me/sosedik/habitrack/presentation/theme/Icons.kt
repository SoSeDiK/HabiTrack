package me.sosedik.habitrack.presentation.theme

import androidx.compose.ui.text.font.FontFamily
import habitrack.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
suspend fun loadIcons(mappingsFile: String): Map<String, String> {
    return withContext(Dispatchers.IO) {
        val jsonBytes = Res.readBytes("files/${mappingsFile}.json")
        val jsonString = jsonBytes.decodeToString()
        Json.decodeFromString<Map<String, String>>(jsonString)
    }
}

class IconCache(
    val mappings: Map<String, String>,
    val fontFamily: FontFamily?
) {
    val defaultIconKey: String = mappings.keys.first()
    private val defaultIcon: String = mappings[defaultIconKey]!!

    fun getById(id: String?): String {
        if (id == null) return defaultIcon
        return mappings[id] ?: defaultIcon
    }

}
