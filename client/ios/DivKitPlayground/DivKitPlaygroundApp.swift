import SwiftUI

import DivKit

@main
struct DivKitPlaygroundApp: App {
  init() {
    DivKitLogger.isEnabled = true
  }

  var body: some Scene {
    WindowGroup {
      MainView()
    }
  }
}
