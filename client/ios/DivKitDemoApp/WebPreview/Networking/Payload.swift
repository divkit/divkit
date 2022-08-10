import Foundation

import CommonCore

struct ListenPayload: Encodable {
  struct Message: Encodable {
    let uuid: String
  }

  let type: String = "listen"
  let message: Message

  init(uuid: String) {
    message = Message(uuid: uuid)
  }
}

struct UIStatePayload: Encodable {
  struct Device: Encodable {
    enum Orientation: String, Encodable {
      case album
      case portrait
    }

    let client_id: String

    let app_name: String?
    let app_version: String?

    let os_name: String
    let os_version: String

    let device: String
    let orientation: Orientation

    let density: Double
    let width: Double
    let height: Double
  }

  struct Error: Encodable {
    let message: String
    let stack: [String]?
  }

  struct Message: Encodable {
    let device: Device
    let screenshot: String
    let errors: [Error]
  }

  let type: String = "ui_state"
  let message: Message

  init(
    device: UIStatePayload.Device,
    screenshot: Data,
    errors: [UIStatePayload.Error]
  ) {
    message = Message(
      device: device,
      screenshot: screenshot.base64EncodedString(),
      errors: errors
    )
  }
}
