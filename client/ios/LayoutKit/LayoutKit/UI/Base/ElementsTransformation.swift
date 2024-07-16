import Foundation

public struct ElementsTransformation: Equatable {
  public let nextElementAlpa: CGFloat
  public let previousElementAlpha: CGFloat
  public let nextElementScale: CGFloat
  public let previousElementScale: CGFloat

  public init(
    nextElementAlpha: CGFloat,
    previousElementAlpha: CGFloat,
    nextElementScale: CGFloat,
    previousElementScale: CGFloat
  ) {
    self.nextElementAlpa = nextElementAlpha
    self.previousElementAlpha = previousElementAlpha
    self.nextElementScale = nextElementScale
    self.previousElementScale = previousElementScale
  }
}
