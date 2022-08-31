import SwiftUI

import BaseUI

@main
struct DivKitPlaygroundApp: App {
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
