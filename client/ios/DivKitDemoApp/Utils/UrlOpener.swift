import UIKit

enum DemoUrlOpener {
  static func openUrl(_ url: URL) {
    guard UIApplication.shared.canOpenURL(url) else {
      DemoAppLogger.error("Unhandled URL: \(url)")
      return
    }

    UIApplication.shared.open(url, options: [:], completionHandler: nil)
  }
}
