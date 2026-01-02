package dev.stetsiuk.compose.snackbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import snackbarjetpackcompose.composeapp.generated.resources.Res
import snackbarjetpackcompose.composeapp.generated.resources.outline_close_24
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun SnackBarHostState.show(
    text: String,
) {
    val state = SnackBarState()
    val data = SnackBarData(state) {
        DraggableSnackBar(text) { state.hide() }
    }
    show(data)
}

@Composable
fun DraggableSnackBar(
    text: String,
    onClose: () -> Unit,
) {
    BasicDraggableSnackBar(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Color.Gray,
        onDismissed = onClose,
        content = {
            Row(
                modifier = Modifier.padding(
                    PaddingValues(16.dp, 4.dp)
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = text
                )
                IconButton(
                    onClick = onClose,
                    content = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.outline_close_24),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )
            }
        }
    )
}

@Preview
@Composable
internal fun SnackBarPreview() {
    MaterialTheme {
        ProvideSnackBarHost {
            DraggableSnackBar(
                text = "Request timeout",
                onClose = {}
            )
        }
    }
}