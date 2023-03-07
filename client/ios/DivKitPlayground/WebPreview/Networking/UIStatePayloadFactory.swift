import Foundation

import CommonCore

struct ScreenshotInfo {
  let data: Data
  let density: Double
  let height: Double
  let width: Double
}

final class UIStatePayloadFactory {
  private let deviceInfo: DeviceInfo
  private let clientId = UUID().uuidString
  @Variable
  private var errors: [UIStatePayload.Error]

  init(
    deviceInfo: DeviceInfo,
    errors: Variable<[UIStatePayload.Error]>
  ) {
    self.deviceInfo = deviceInfo
    _errors = errors
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
