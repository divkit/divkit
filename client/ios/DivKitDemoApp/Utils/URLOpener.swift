import UIKit

func tryOpenURL(_ url: URL) -> Bool {
  guard UIApplication.shared.canOpenURL(url) else {
    return false
  }

  UIApplication.shared.open(url, options: [:], completionHandler: nil)

  return true
}
