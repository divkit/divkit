import Foundation

@frozen
public enum Expression<T> {
  case value(T)
  case link(ExpressionLink<T>)
}

extension Expression: Equatable where T: Equatable {
  public static func ==(lhs: Self, rhs: Self) -> Bool {
    switch (lhs, rhs) {
    case let (.value(lValue), .value(rValue)):
      return lValue == rValue
    case let (.link(lValue), .link(rValue)):
      return lValue == rValue
    case (.value, _), (.link, _):
      return false
    }
  }
}
