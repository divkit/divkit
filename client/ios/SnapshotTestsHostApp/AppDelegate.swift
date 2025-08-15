import UIKit

@main
final class AppDelegate: NSObject, UIApplicationDelegate {
  var window: UIWindow? = UIWindow()

  func application(
    _: UIApplication,
    didFinishLaunchingWithOptions _: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    window?.rootViewController = ViewController(nibName: nil, bundle: nil)
    window?.makeKeyAndVisible()

    return true
  }
}
