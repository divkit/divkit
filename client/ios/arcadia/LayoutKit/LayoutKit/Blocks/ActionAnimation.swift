// Copyright 2021 Yandex LLC. All rights reserved.

public struct ActionAnimation: Equatable {
  public let touchDown: [TransitioningAnimation]
  public let touchUp: [TransitioningAnimation]

  public static let empty = Self(touchDown: [.empty], touchUp: [.empty])

  public init(
    touchDown: [TransitioningAnimation],
    touchUp: [TransitioningAnimation]
  ) {
    self.touchDown = touchDown
    self.touchUp = touchUp
  }
}
