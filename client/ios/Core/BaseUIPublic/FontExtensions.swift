// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import CoreText
import Foundation

import BaseTinyPublic

extension Font {
  public class func with(
    family: FontFamily = .YSText,
    weight: FontWeight,
    size: FontSize,
    isMonospacedDigits: Bool = false
  ) -> Font {
    with(
      family: family,
      weight: weight,
      size: size.rawValue,
      isMonospacedDigits: isMonospacedDigits
    )
  }

  public class func with(
    family: FontFamily = .YSText,
    weight: FontWeight,
    size: CGFloat,
    isMonospacedDigits: Bool = false
  ) -> Font {
    let result = fontSpecifiers.font(family: family, weight: weight, size: size)
    return isMonospacedDigits ? result.withMonospacedDigits() : result
  }
}

extension Font {
  public func withMonospacedDigits() -> Font {
    let features = [
      [
        FontFeatureTypeIdentifierKey: kNumberSpacingType,
        FontFeatureSelectorIdentifierKey: kMonospacedNumbersSelector,
      ],
    ]

    let descriptor = fontDescriptor.addingAttributes([.featureSettings: features])

    #if os(OSX)
    return Font(descriptor: descriptor, size: 0)!
    #else
    return Font(descriptor: descriptor, size: 0) // zero forces to use size from descriptor
    #endif
  }

  private static let fontUsageAttributeName =
    FontDescriptorAttributeName(rawValue: "NSCTFontUIUsageAttribute")

  public func with(fontUsage: Any) -> Font {
    let descriptor = fontDescriptor.addingAttributes([Font.fontUsageAttributeName: fontUsage])
    #if os(OSX)
    return Font(descriptor: descriptor, size: 0)!
    #else
    return Font(descriptor: descriptor, size: 0) // zero forces to use size from descriptor
    #endif
  }

  public func scaled(by scale: CGFloat) -> Font {
    withSize(floor(pointSize * scale))
  }
}

#if os(iOS) || os(tvOS)
import UIKit
private let FontFeatureTypeIdentifierKey = UIFontDescriptor.FeatureKey.featureIdentifier
private let FontFeatureSelectorIdentifierKey = UIFontDescriptor.FeatureKey.typeIdentifier
private typealias FontDescriptorAttributeName = UIFontDescriptor.AttributeName
#elseif os(macOS)
import AppKit
let FontFeatureTypeIdentifierKey = NSFontDescriptor.FeatureKey.typeIdentifier
let FontFeatureSelectorIdentifierKey = NSFontDescriptor.FeatureKey.selectorIdentifier
typealias FontDescriptorAttributeName = NSFontDescriptor.AttributeName
#else
#error("Unsupported OS")
#endif
