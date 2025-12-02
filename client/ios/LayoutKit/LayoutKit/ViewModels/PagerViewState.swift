import CoreGraphics
import VGSL

public struct PagerViewState: ElementState, Equatable, Sendable {
  public static let `default` = Self(
    numberOfPages: 0,
    currentPage: 0,
    animated: true,
    isInfiniteScrollable: false
  )

  public let numberOfPages: Int
  public let currentPage: CGFloat
  public let animated: Bool
  public let isInfiniteScrollable: Bool

  public init(
    numberOfPages: Int,
    currentPage: Int,
    animated: Bool,
    isInfiniteScrollable: Bool = false
  ) {
    self.numberOfPages = numberOfPages
    self.currentPage = CGFloat(currentPage)
    self.animated = animated
    self.isInfiniteScrollable = isInfiniteScrollable
  }

  #if DEBUG
  init(numberOfPages: Int, floatCurrentPage: CGFloat) {
    self.numberOfPages = numberOfPages
    self.currentPage = floatCurrentPage
    self.animated = true
    self.isInfiniteScrollable = false
  }
  #endif
}

extension PagerViewState {
  public func synchronized(with model: GalleryViewModel) -> PagerViewState {
    guard !isInfiniteScrollable else { return self }

    let clampedCurrentPage = clamp(
      Int(currentPage),
      min: 0,
      max: max(0, model.itemsCountWithoutInfinite - 1)
    )
    return PagerViewState(
      numberOfPages: model.itemsCountWithoutInfinite,
      currentPage: clampedCurrentPage,
      animated: animated,
      isInfiniteScrollable: false
    )
  }
}
