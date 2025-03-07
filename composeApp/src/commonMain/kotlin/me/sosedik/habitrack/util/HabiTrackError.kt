package me.sosedik.habitrack.util

import androidx.compose.runtime.Composable
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.error_can_not_be_empty
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

interface HabiTrackError {
    val errorMessageResId: StringResource
}

open class GenericError(
    override val errorMessageResId: StringResource
): HabiTrackError

data object CanNotBeEmptyError : GenericError(Res.string.error_can_not_be_empty)

@Composable
fun HabiTrackError.message(): String {
    return stringResource(this.errorMessageResId)
}
