// Copyright 2019 Yandex LLC. All rights reserved.

public struct ScrollEdge: RawRepresentable, OptionSet {
  public let rawValue: Int
  public init(rawValue: Int) {
    self.rawValue = rawValue
  }

  public static let left = ScrollEdge(rawValue: 0b0001)
  public static let right = ScrollEdge(rawValue: 0b0010)
  public static let top = ScrollEdge(rawValue: 0b0100)
  public static let bottom = ScrollEdge(rawValue: 0b1000)
}
