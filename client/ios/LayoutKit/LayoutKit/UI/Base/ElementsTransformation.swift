import Foundation

public struct ElementsTransformation: Equatable {
  public enum Style {
    case slide
    case overlap
  }

  public let nextElementAlpa: CGFloat
  public let previousElementAlpha: CGFloat
  public let nextElementScale: CGFloat
  public let previousElementScale: CGFloat
  public let style: Style
  public let scrollDirection: ScrollDirection

  public init(
    nextElementAlpha: CGFloat,
    previousElementAlpha: CGFloat,
    nextElementScale: CGFloat,
    previousElementScale: CGFloat,
    style: Style,
    scrollDirection: ScrollDirection
  ) {
    self.nextElementAlpa = nextElementAlpha
    self.previousElementAlpha = previousElementAlpha
    self.nextElementScale = nextElementScale
    self.previousElementScale = previousElementScale
    self.style = style
    self.scrollDirection = scrollDirection
  }
}
