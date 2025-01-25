import DivKit
import SwiftUI

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
