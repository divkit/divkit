import DivKit
import SwiftUI

@main
struct DivKitPlaygroundApp: App {
  var body: some Scene {
    WindowGroup {
      MainView()
    }
  }

  init() {
    DivKitLogger.isEnabled = true
  }
}
