import Foundation

enum Platform: String, Decodable {
  case android
  case ios
  case web
  case flutter
}

extension Platform {
  static func isSupported(by json: [String: Any]) -> Bool {
    guard let platforms = json["platforms"] as? [String] else { return true }
    return platforms.contains(Platform.ios.rawValue)
  }
}

extension Sequence<Platform> {
  var supported: Bool { contains(.ios) }
}
