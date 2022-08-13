// Copyright 2017 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import BaseUI
import CommonCore

public struct GalleryViewMetrics: Equatable {
  public let axialInsetMode: InsetMode
  public let crossInsetMode: InsetMode
  public let spacings: [CGFloat]

  public init(
    axialInsetMode: InsetMode,
    crossInsetMode: InsetMode = .fixed(values: .zero),
    spacings: [CGFloat]
  ) {
    self.axialInsetMode = axialInsetMode
    self.crossInsetMode = crossInsetMode
    self.spacings = spacings
  }

  public init(gaps: [CGFloat]) {
    precondition(gaps.count >= 2)
    axialInsetMode = .fixed(values: SideInsets(leading: gaps.first!, trailing: gaps.last!))
    crossInsetMode = .fixed(values: .zero)
    spacings = Array(gaps.dropFirst().dropLast())
  }

  public func gaps(forSize size: CGFloat) -> [CGFloat] {
    let insets = axialInsetMode.insets(forSize: size)
    return [insets.leading] + spacings + [insets.trailing]
  }
}
