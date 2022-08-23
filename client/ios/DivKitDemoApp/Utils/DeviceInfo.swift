import Foundation
import UIKit

struct DeviceInfo {
  var osName: String {
    UIDevice.current.systemName
  }

  var osVersion: String {
    UIDevice.current.systemVersion
  }

  var deviceModel: String {
    let systemModelName = UIDevice.current.systemModelName
    switch systemModelName {
    case "iPhone8,1": return "iPhone 6s"
    case "iPhone8,2": return "iPhone 6s Plus"
    case "iPhone8,4": return "iPhone SE"
    case "iPhone9,1", "iPhone9,3": return "iPhone 7"
    case "iPhone9,2", "iPhone9,4": return "iPhone 7 Plus"
    case "iPhone10,1", "iPhone10,4": return "iPhone 8"
    case "iPhone10,2", "iPhone10,5": return "iPhone 8 Plus"
    case "iPhone10,3", "iPhone10,6": return "iPhone X"
    case "iPhone11,2": return "iPhone XS"
    case "iPhone11,4", "iPhone11,6": return "iPhone XS Max"
    case "iPhone11,8": return "iPhone XR"
    case "iPhone12,1": return "iPhone 11"
    case "iPhone12,3": return "iPhone 11 Pro"
    case "iPhone12,5": return "iPhone 11 Pro Max"
    case "iPhone12,8": return "iPhone SE (2nd gen)"
    case "iPhone13,1": return "iPhone 12 Mini"
    case "iPhone13,2": return "iPhone 12"
    case "iPhone13,3": return "iPhone 12 Pro"
    case "iPhone13,4": return "iPhone 12 Pro Max"
    case "iPhone14,2": return "iPhone 13 Pro"
    case "iPhone14,3": return "iPhone 13 Pro Max"
    case "iPhone14,4": return "iPhone 13 Mini"
    case "iPhone14,5": return "iPhone 13"
    case "iPhone14,6": return "iPhone SE (3rd gen)"
    case "i386", "x86_64": return "Simulator"
    default: return systemModelName
    }
  }

  var orientation: UIStatePayload.Device.Orientation {
    switch UIDevice.current.orientation {
    case .portrait, .portraitUpsideDown, .faceUp, .faceDown, .unknown:
      return .portrait
    case .landscapeLeft, .landscapeRight:
      return .album
    @unknown default:
      return .portrait
    }
  }
}
