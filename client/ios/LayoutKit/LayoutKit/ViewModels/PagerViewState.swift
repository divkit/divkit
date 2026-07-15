import CoreGraphics
import VGSL

public enum ScrollNavigationDirection: Equatable, Sendable {
  case forward
  case backward
  case none
}

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
  public let navigationDirection: ScrollNavigationDirection

  public init(
    numberOfPages: Int,
    currentPage: Int,
    animated: Bool,
    isInfiniteScrollable: Bool = false,
    navigationDirection: ScrollNavigationDirection = .none
  ) {
    self.numberOfPages = numberOfPages
    self.currentPage = CGFloat(currentPage)
    self.animated = animated
    self.isInfiniteScrollable = isInfiniteScrollable
    self.navigationDirection = navigationDirection
  }

  #if DEBUG
  init(numberOfPages: Int, floatCurrentPage: CGFloat) {
    self.numberOfPages = numberOfPages
    self.currentPage = floatCurrentPage
    self.animated = true
    self.isInfiniteScrollable = false
    self.navigationDirection = .none
  }
  #endif

  private init(
    numberOfPages: Int,
    currentPageValue: CGFloat,
    animated: Bool,
    isInfiniteScrollable: Bool,
    navigationDirection: ScrollNavigationDirection
  ) {
    self.numberOfPages = numberOfPages
    self.currentPage = currentPageValue
    self.animated = animated
    self.isInfiniteScrollable = isInfiniteScrollable
    self.navigationDirection = navigationDirection
  }

  public static func ==(lhs: PagerViewState, rhs: PagerViewState) -> Bool {
    lhs.numberOfPages == rhs.numberOfPages
      && lhs.currentPage == rhs.currentPage
      && lhs.animated == rhs.animated
      && lhs.isInfiniteScrollable == rhs.isInfiniteScrollable
  }

  public func withNavigationDirection(_ direction: ScrollNavigationDirection) -> PagerViewState {
    PagerViewState(
      numberOfPages: numberOfPages,
      currentPageValue: currentPage,
      animated: animated,
      isInfiniteScrollable: isInfiniteScrollable,
      navigationDirection: direction
    )
  }
}

extension PagerViewState {
  public func synchronized(with model: GalleryViewModel) -> PagerViewState {
    let pageCount = model.itemsCountWithoutInfinite

    if isInfiniteScrollable, numberOfPages == pageCount {
      return self
    }

    let currentPageValue = isInfiniteScrollable
      ? currentPage
      : CGFloat(clamp(Int(currentPage), min: 0, max: max(0, pageCount - 1)))

    return PagerViewState(
      numberOfPages: pageCount,
      currentPageValue: currentPageValue,
      animated: animated,
      isInfiniteScrollable: isInfiniteScrollable,
      navigationDirection: navigationDirection
    )
  }
}
