import Foundation

import CommonCorePublic
import DivKit

struct ScreenshotInfo {
  let data: Data
  let density: Double
  let height: Double
  let width: Double
}

final class UIStatePayloadFactory {
  private let deviceInfo: DeviceInfo
  private let clientId = UUID().uuidString
  private var errors: [UIStatePayload.Error] = []

  init(
    deviceInfo: DeviceInfo
  ) {
    self.deviceInfo = deviceInfo
  }

  func addError(_ error: DivError) {
    errors.append(UIStatePayload.Error(error))
  }

  func resetErrors() {
    errors = []
  }

  func makePayload(
    screenshotInfo: ScreenshotInfo,
    renderingTime: UIStatePayload.RenderingTime
  ) -> UIStatePayload {
    let bundleInfo = Bundle.main.infoDictionary
    return UIStatePayload(
      device: UIStatePayload.Device(
        client_id: clientId,
        app_name: bundleInfo?["CFBundleDisplayName"] as? String,
        app_version: bundleInfo?["CFBundleShortVersionString"] as? String,
        os_name: deviceInfo.osName,
        os_version: deviceInfo.osVersion,
        device: deviceInfo.deviceModel,
        orientation: deviceInfo.orientation,
        density: screenshotInfo.density,
        width: screenshotInfo.width,
        height: screenshotInfo.height
      ),
      screenshot: screenshotInfo.data,
      errors: errors,
      renderingTime: renderingTime
    )
  }
}

extension UIStatePayload.Error {
  var description: String {
    message
  }

  init(_ error: DivError) {
    message = error.description
    level = .init(error.level)
  }
}

extension UIStatePayload.Error.Level {
  init(_ error: DivErrorLevel) {
    switch error {
    case .error:
      self = .error
    case .warning:
      self = .warning
    }
  }
}
