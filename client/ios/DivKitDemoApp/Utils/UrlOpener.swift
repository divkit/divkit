import UIKit

class DemoUrlOpener {
  var onUnhandledUrl: (URL) -> () = { _ in }
  
  func openUrl(_ url: URL) {
    guard UIApplication.shared.canOpenURL(url) else {
      onUnhandledUrl(url)
      return
    }

    UIApplication.shared.open(url, options: [:], completionHandler: nil)
  }
}
