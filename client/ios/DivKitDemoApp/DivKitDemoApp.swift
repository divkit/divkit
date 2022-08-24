import SwiftUI

import BaseUI

@main
struct DivKitDemoApp: App {
  init() {
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
