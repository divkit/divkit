import Foundation

import CommonCore

final class UIStatePayloadFactory {
  typealias Errors = [(message: String, stack: [String]?)]

  private let deviceInfo: DeviceInfo
  private let clientId = UUID().uuidString
  @ObservableVariable
  private var errors: Errors?

  init(
    deviceInfo: DeviceInfo,
    errors: ObservableVariable<Errors?>
  ) {
    self.deviceInfo = deviceInfo
    _errors = errors
  }

  func makePayload(screenshotInfo: ScreenshotInfo) -> UIStatePayload {
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
      errors: (errors ?? []).map {
        UIStatePayload.Error(
          message: $0.message,
          stack: $0.stack
        )
      }
    )
  }
}
