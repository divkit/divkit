import androidx.compose.ui.window.ComposeUIViewController
import com.yandex.divkit.compose.MainScreen
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    MainScreen()
}
