// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

public enum FontFamily: Hashable {
  case YSText
  case YSDisplay
}

@frozen
public enum FontWeight: Hashable {
  case light
  case regular
  case medium
  case semibold
  case bold
}

public struct FontSize: RawRepresentable {
  public static let caption = FontSize(rawValue: 11)
  public static let capsS = FontSize(rawValue: 12)
  public static let textXS = FontSize(rawValue: 12)
  public static let textS = FontSize(rawValue: 13)
  public static let textM = FontSize(rawValue: 14)
  public static let buttonL = FontSize(rawValue: 15)
  public static let textL = FontSize(rawValue: 16)
  public static let textXL = FontSize(rawValue: 17)
  public static let title = FontSize(rawValue: 20)
  public static let titleL = FontSize(rawValue: 24)

  public private(set) var rawValue: CGFloat

  public init(rawValue: CGFloat) {
    self.rawValue = rawValue
  }
}

public struct FontLineHeight: RawRepresentable {
  public static let unspecified = FontLineHeight(rawValue: 0) // not constrained
  public static let textXS = FontLineHeight(rawValue: 14)
  public static let caption = FontLineHeight(rawValue: 16)
  public static let capsS = FontLineHeight(rawValue: 16)
  public static let textS = FontLineHeight(rawValue: 16)
  public static let subTitle = FontLineHeight(rawValue: 18)
  public static let textM = FontLineHeight(rawValue: 20)
  public static let buttonL = FontLineHeight(rawValue: 20)
  public static let textL = FontLineHeight(rawValue: 20)
  public static let textXL = FontLineHeight(rawValue: 20)
  public static let title = FontLineHeight(rawValue: 24)
  public static let titleL = FontLineHeight(rawValue: 28)

  public private(set) var rawValue: CGFloat

  public init(rawValue: CGFloat) {
    self.rawValue = rawValue
  }

  public init(fontSize: FontSize) {
    self.rawValue = fontSize.rawValue
  }
}

public struct Kern: RawRepresentable {
  public static let caption = Kern(rawValue: 0.75)
  public static let capsS = Kern(rawValue: 0.75)
  public static let textS = capsS
  public static let textM = Kern(rawValue: -0.2)
  public static let textL = Kern(rawValue: -0.4)
  public static let title = Kern(rawValue: -0.1)

  public private(set) var rawValue: CGFloat

  public init(rawValue: CGFloat) {
    self.rawValue = rawValue
  }
}
