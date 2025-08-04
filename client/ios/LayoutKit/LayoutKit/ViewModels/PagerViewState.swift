import CoreGraphics
import VGSL

public struct PagerViewState: ElementState, Equatable {
  public static let `default` = Self(
    numberOfPages: 0,
    currentPage: 0,
    animated: true
  )

  public let numberOfPages: Int
  public let currentPage: CGFloat
  public let animated: Bool

  public init(numberOfPages: Int, currentPage: Int, animated: Bool) {
    self.numberOfPages = numberOfPages
    self.currentPage = CGFloat(currentPage)
    self.animated = animated
  }

  // init for testing
  init(numberOfPages: Int, floatCurrentPage: CGFloat) {
    self.numberOfPages = numberOfPages
    self.currentPage = floatCurrentPage
    self.animated = true
  }

}

extension PagerViewState {
  public func synchronized(with model: GalleryViewModel) -> PagerViewState {
    let clampedCurrentPage = clamp(
      Int(currentPage),
      min: 0,
      max: max(0, model.itemsCountWithoutInfinite - 1)
    )
    return PagerViewState(
      numberOfPages: model.itemsCountWithoutInfinite,
      currentPage: clampedCurrentPage,
      animated: animated
    )
  }
}
