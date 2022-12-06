import Foundation

public protocol AnimationSourceType {}

@frozen
public enum LottieAnimationSourceType: AnimationSourceType, Equatable {
  case json([String: Any])
  case data(Data)

  public static func == (lhs: LottieAnimationSourceType, rhs: LottieAnimationSourceType) -> Bool {
    switch (lhs, rhs) {
    case (.data(let lhs), .data(let rhs)):
      return lhs == rhs
    case (.json(let lhs), .json(let rhs)):
      return NSDictionary(dictionary: lhs).isEqual(to: rhs)
    case (.data, _), (.json, _):
      return false
    }
  }
}

@frozen
public enum RiveAnimationSourceType: AnimationSourceType, Equatable {
  case data(Data)

  public static func == (lhs: RiveAnimationSourceType, rhs: RiveAnimationSourceType) -> Bool {
    switch (lhs, rhs) {
    case (.data(let lhs), .data(let rhs)):
      return lhs == rhs
    }
  }
}
