import CoreText
import Foundation
import VGSLUI

extension Font {
  func withVariationSettings(axisTagToValue: [String: NSNumber]?) -> Font {
    guard let axisTagToValue, !axisTagToValue.isEmpty else { return self }
    let ctfont = CTFontCreateWithName(fontName as CFString, pointSize, nil)
    guard let fontAxesRaw = CTFontCopyVariationAxes(ctfont) as? [Any] else { return self }

    let axisIdToValue = axisTagToValue.map(
      key: { tag -> UInt32 in
        tag.utf16.reduce(0) { ($0 << 8) | UInt32($1) }
      },
      value: { $0 }
    )

    let axisIdKey = "NSCTVariationAxisIdentifier"
    let axisDefaultValueKey = "NSCTVariationAxisDefaultValue"

    let axisIdToValueFull = fontAxesRaw.compactMap { axis in
      guard let axisInfo = axis as? [String: Any],
            let axisId = axisInfo[axisIdKey] as? UInt32,
            let defaultValue = axisInfo[axisDefaultValueKey] as? NSNumber
      else { return nil }

      return (axisId, defaultValue)
    }.toDictionary(
      keyMapper: { (axis: (UInt32, NSNumber)) -> UInt32 in
        axis.0
      },
      valueMapper: { (axis: (UInt32, NSNumber)) -> NSNumber in
        axisIdToValue[axis.0] ?? axis.1
      }
    )

    let descriptor = fontDescriptor.addingAttributes([
      kCTFontVariationAttribute as FontDescriptor.AttributeName: axisIdToValueFull,
    ])

    return makeFont(descriptor: descriptor, size: pointSize)
  }
}

#if os(iOS) || os(tvOS)
import UIKit
typealias FontDescriptor = UIFontDescriptor
private func makeFont(descriptor: FontDescriptor, size: CGFloat) -> Font {
  Font(descriptor: descriptor, size: size)
}
#else
import AppKit
typealias FontDescriptor = NSFontDescriptor
private func makeFont(descriptor: FontDescriptor, size: CGFloat) -> Font {
  Font(descriptor: descriptor, size: size)!
}
#endif
