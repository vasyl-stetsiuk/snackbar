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
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun SnackBarHostState.show(
    text: String,
    id: String = Uuid.random().toString(),
    timeoutMs: SnackBarData.Duration = SnackBarData.Duration.Long,
) {
    val data = SnackBarData(id, timeoutMs) {
        DraggableSnackBar(text) { hide(id) }
    }
    show(data)
}

@Composable
fun DraggableSnackBar(
    text: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp, 4.dp),
    onClose: () -> Unit,
) {
    BasicDraggableSnackBar(
        modifier = modifier.padding(horizontal = 16.dp),
        color = Color.Gray,
        onDismissed = onClose,
        content = {
            Row(
                modifier = modifier.padding(contentPadding),
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