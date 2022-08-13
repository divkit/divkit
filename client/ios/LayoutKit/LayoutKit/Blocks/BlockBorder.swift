// Copyright 2017 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import CommonCore

public struct BlockBorder: Equatable {
  public let color: Color
  public let width: CGFloat

  public init(
    color: Color,
    width: CGFloat = 1
  ) {
    self.color = color
    self.width = width
  }
}
