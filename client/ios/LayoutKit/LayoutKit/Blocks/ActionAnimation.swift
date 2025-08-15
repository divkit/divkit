public struct ActionAnimation: Equatable {
  public static let empty = Self(touchDown: [.empty], touchUp: [.empty])

  public let touchDown: [TransitioningAnimation]
  public let touchUp: [TransitioningAnimation]

  public init(
    touchDown: [TransitioningAnimation],
    touchUp: [TransitioningAnimation]
  ) {
    self.touchDown = touchDown
    self.touchUp = touchUp
  }
}
