import Foundation

public struct ElementsTransformation: Equatable {
  public let nextElementAlpa: CGFloat
  public let previousElementAlpha: CGFloat
  public let nextElementScale: CGFloat
  public let previousElementScale: CGFloat
  public let scrollDirection: ScrollDirection

  public init(
    nextElementAlpha: CGFloat,
    previousElementAlpha: CGFloat,
    nextElementScale: CGFloat,
    previousElementScale: CGFloat,
    scrollDirection: ScrollDirection
  ) {
    self.nextElementAlpa = nextElementAlpha
    self.previousElementAlpha = previousElementAlpha
    self.nextElementScale = nextElementScale
    self.previousElementScale = previousElementScale
    self.scrollDirection = scrollDirection
  }
}
