import XCTest

final class AppMainWindow {
  static let shared = AppMainWindow()

  private var cachedFrame: (frame: CGRect, orientation: UIDeviceOrientation)?

  private init() {}

  var frame: CGRect {
    let orientation = XCUIDevice.shared.orientation
    if let cachedFrame, cachedFrame.orientation == orientation {
      return cachedFrame.frame
    }

    let frame = XCUIApplication().windows.firstMatch.frame
    cachedFrame = (frame, orientation)
    return frame
  }
}
