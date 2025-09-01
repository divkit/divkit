import androidx.compose.ui.window.ComposeUIViewController
import com.yandex.divkit.multiplaform.sample.MainScreen
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    MainScreen()
}
