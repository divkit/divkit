import CoreText
import UIKit

extension UIFont {
  func withFontFeatureSettings(_ value: String) -> UIFont {
    var featureSettings: [[UIFontDescriptor.FeatureKey: Int]] = featureSettings ?? [[:]]
    value.split(separator: ",")
      .compactMap { Feature($0.trimmingCharacters(in: [" "])) }
      .forEach { featureSettings.setFeature($0) }

    var attributes = fontDescriptor.fontAttributes
    attributes.removeValue(forKey: .featureSettings)
    let descriptor = UIFontDescriptor(fontAttributes: attributes)
      .addingAttributes([.featureSettings: featureSettings])
    return UIFont(descriptor: descriptor, size: 0.0)
  }

  var featureSettings: [[UIFontDescriptor.FeatureKey: Int]]? {
    fontDescriptor.fontAttributes
      .first { $0.key == .featureSettings }?
      .value as? [[UIFontDescriptor.FeatureKey: Int]]
  }
}

extension [[UIFontDescriptor.FeatureKey: Int]] {
  fileprivate mutating func setFeature(_ feature: Feature) {
    let featureDict = [
      UIFontDescriptor.FeatureKey.featureIdentifier: feature.type,
      UIFontDescriptor.FeatureKey.typeIdentifier: feature.selector,
    ]
    if feature.isEnabled {
      append(featureDict)
    } else {
      removeAll { $0 == featureDict }
    }
  }
}

private struct Feature {
  let type: Int
  let selector: Int
  let isEnabled: Bool

  init?(_ value: String) {
    let items = value.split(separator: " ")
    let count = items.count
    if count == 0 || count > 2 {
      DivKitLogger.error("Invalid font feature: \(value)")
      return nil
    }

    let rawName = items[0]
    guard rawName.starts(with: "'") || rawName.starts(with: "\"") else {
      DivKitLogger.error("Invalid font feature: \(value)")
      return nil
    }
    let name = rawName.trimmingCharacters(in: ["'", "\""])
    guard let params = featureParams[name] else {
      DivKitLogger.error("Unknown font feature: \(name)")
      return nil
    }

    type = params.0
    selector = params.1

    if count == 1 {
      isEnabled = true
    } else {
      let state = items[1].lowercased()
      isEnabled = state == "on" || state == "1"
    }
  }
}

private let featureParams: [String: (Int, Int)] = [
  "c2sc": (kUpperCaseType, kUpperCaseSmallCapsSelector),
  "frac": (kFractionsType, kDiagonalFractionsSelector),
  "ordn": (kVerticalPositionType, kOrdinalsSelector),
  "pnum": (kNumberSpacingType, kProportionalNumbersSelector),
  "smcp": (kLowerCaseType, kLowerCaseSmallCapsSelector),
  "subs": (kVerticalPositionType, kInferiorsSelector),
  "sups": (kVerticalPositionType, kSuperiorsSelector),
  "tnum": (kNumberSpacingType, kMonospacedNumbersSelector),
]
