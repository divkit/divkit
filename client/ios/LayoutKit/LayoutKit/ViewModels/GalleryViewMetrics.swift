import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic

public struct GalleryViewMetrics: Equatable {
  public let axialInsetMode: InsetMode
  public let crossInsetMode: InsetMode
  public let spacings: [CGFloat]
  public let crossSpacing: CGFloat

  public init(
    axialInsetMode: InsetMode,
    crossInsetMode: InsetMode = .fixed(values: .zero),
    spacings: [CGFloat],
    crossSpacing: CGFloat
  ) {
    self.axialInsetMode = axialInsetMode
    self.crossInsetMode = crossInsetMode
    self.spacings = spacings
    self.crossSpacing = crossSpacing
  }

  public init(gaps: [CGFloat]) {
    precondition(gaps.count >= 2)
    axialInsetMode = .fixed(values: SideInsets(leading: gaps.first!, trailing: gaps.last!))
    crossInsetMode = .fixed(values: .zero)
    spacings = Array(gaps.dropFirst().dropLast())
    crossSpacing = spacings.first ?? 0
  }

  public func gaps(forSize size: CGFloat) -> [CGFloat] {
    let insets = axialInsetMode.insets(forSize: size)
    return [insets.leading] + spacings + [insets.trailing]
  }
}
