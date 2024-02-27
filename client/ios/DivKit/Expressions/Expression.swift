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
      lValue == rValue
    case let (.link(lValue), .link(rValue)):
      lValue == rValue
    case (.value, _), (.link, _):
      false
    }
  }
}
