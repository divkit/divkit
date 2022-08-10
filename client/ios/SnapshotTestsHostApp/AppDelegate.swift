import UIKit

import BaseUI

@UIApplicationMain
final class AppDelegate: NSObject, UIApplicationDelegate {
  var window: UIWindow? = UIWindow()

  func application(
    _: UIApplication,
    didFinishLaunchingWithOptions _: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    let provider = FontProvider()
    fontSpecifiers = FontSpecifiers(text: provider, display: provider)

    window?.rootViewController = ViewController(nibName: nil, bundle: nil)
    window?.makeKeyAndVisible()

    return true
  }
}
