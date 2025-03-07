package me.sosedik.habitrack.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun SimpleTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    isError: Boolean = false,
    errorMessage: String? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth(),
        state = state,
        lineLimits = lineLimits,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        cursorBrush = SolidColor(if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        decorator = { innerTextField ->
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .border(1.dp,
                            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    innerTextField()
                }
                errorMessage?.let {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .padding(start = 12.dp),
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}
