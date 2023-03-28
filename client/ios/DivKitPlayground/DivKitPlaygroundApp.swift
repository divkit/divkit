import SwiftUI

import BaseUIPublic
import DivKit

@main
struct DivKitPlaygroundApp: App {
  init() {
    DivKitLogger.isEnabled = true

    let fontProvider = YSFontProvider()
    fontSpecifiers = FontSpecifiers(
      text: fontProvider,
      display: fontProvider
    )
  }

  var body: some Scene {
    WindowGroup {
      MainView()
    }
  }
}
