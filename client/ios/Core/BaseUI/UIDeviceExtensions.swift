// Copyright 2015 Yandex LLC. All rights reserved.

import UIKit

extension UIDevice {
  // https://www.theiphonewiki.com/wiki/Models#iPhone
  public enum Model {
    public static let iPhone4S = "iPhone4,1"
    public static let iPhone5 = "iPhone5,1"
    public static let iPhone5_China = "iPhone5,2"
    public static let iPhone5C = "iPhone5,3"
    public static let iPhone5C_China = "iPhone5,4"
    public static let iPhone5S = "iPhone6,1"
    public static let iPhone5S_China = "iPhone6,2"
    public static let iPhone6 = "iPhone7,2"
    public static let iPhone6Plus = "iPhone7,1"
    public static let iPhone6S = "iPhone8,1"
    public static let iPhone6SPlus = "iPhone8,2"
    public static let iPhone8 = "iPhone10,1"
    public static let iPhone8_China = "iPhone10,4"
    public static let iPhone8Plus = "iPhone10,2"
    public static let iPhone8Plus_China = "iPhone10,5"
    public static let iPhoneSE = "iPhone8,4"
    public static let iPhoneXR = "iPhone11,8"
    public static let iPhoneXS = "iPhone11,2"
    public static let iPhoneXSMax = "iPhone11,6"
    public static let iPhone11 = "iPhone12,1"
    public static let iPhone11Pro = "iPhone12,3"
    public static let iPhone11ProMax = "iPhone12,5"
    public static let iPhone12 = "iPhone13,2"
    public static let iPhone12mini = "iPhone13,1"
    public static let iPhone12Pro = "iPhone13,3"
    public static let iPhone12ProMax = "iPhone13,4"

    public static let iPadAir_5gen = "iPad13,17"
  }

  public var systemModelName: String {
    #if !targetEnvironment(simulator)
    var systemInfo = utsname()
    uname(&systemInfo)
    let identifier = withUnsafePointer(to: &systemInfo.machine) {
      $0.withMemoryRebound(to: CChar.self, capacity: 1) { ptr in
        String(validatingUTF8: ptr)
      }
    }
    #else
    let identifier = ProcessInfo().environment["SIMULATOR_MODEL_IDENTIFIER"]
    #endif
    assert(identifier != nil)
    return identifier ?? "undefined_model"
  }
}
