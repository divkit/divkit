import CoreGraphics

import CommonCorePublic

public struct PagerViewState: ElementState, Equatable {
  public let numberOfPages: Int
  public let currentPage: CGFloat

  public init(numberOfPages: Int, currentPage: Int) {
    self.numberOfPages = numberOfPages
    self.currentPage = CGFloat(currentPage)
  }

  // init for testing
  init(numberOfPages: Int, floatCurrentPage: CGFloat) {
    self.numberOfPages = numberOfPages
    self.currentPage = floatCurrentPage
  }

  public static let `default` = Self(
    numberOfPages: 0,
    currentPage: 0
  )
}

extension PagerViewState {
  public func synchronized(with model: GalleryViewModel) -> PagerViewState {
    PagerViewState(
      numberOfPages: model.items.count,
      currentPage: clamp(Int(currentPage), min: 0, max: model.items.count)
    )
  }
}
