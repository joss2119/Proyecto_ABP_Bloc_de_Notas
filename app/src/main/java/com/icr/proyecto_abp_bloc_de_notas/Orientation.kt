import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun OrientationAwareLayout() {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            // Layout apaisado
            Row(modifier = Modifier.fillMaxSize()) {
                Text("Vista horizontal")
                // Interfaz especifica de horizontal
            }
        }
        else -> {
            // Layout normal (tanto PORTRAIT como UNDEFINED)
            Column(modifier = Modifier.fillMaxSize()) {
                Text("Vista vertical")
                // Interfaz especifica de vertical
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrientationAwareLayoutPreview() {
    OrientationAwareLayout()
}
