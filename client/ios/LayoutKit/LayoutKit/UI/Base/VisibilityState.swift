import CoreGraphics
import Foundation

public struct VisibilityState: Equatable {
  public var maximumVisibleSize: CGSize
  public var visibleFrame: CGRect

  public init(visibleFrame: CGRect) {
    self.visibleFrame = visibleFrame
    maximumVisibleSize = visibleFrame.size
  }

  public init(maximumVisibleSize: CGSize, visibleFrame: CGRect) {
    self.visibleFrame = visibleFrame
    self.maximumVisibleSize = maximumVisibleSize
  }

  public mutating func formIntersection(with visibleBounds: CGRect) {
    maximumVisibleSize.width = min(maximumVisibleSize.width, visibleBounds.width)
    maximumVisibleSize.height = min(maximumVisibleSize.height, visibleBounds.height)
    visibleFrame = visibleFrame.intersection(visibleBounds)
  }

  public func intersected(with visibleBounds: CGRect) -> VisibilityState {
    var visibilityState = self
    visibilityState.formIntersection(with: visibleBounds)
    return visibilityState
  }

  public var visibility: CGFloat {
    let visibleArea = visibleFrame.size.area
    let maximumVisibleArea = maximumVisibleSize.area
    return (maximumVisibleArea > 0) ? (visibleArea / maximumVisibleArea) : 0
  }
}

extension CGSize {
  fileprivate var area: CGFloat {
    width * height
  }
}
