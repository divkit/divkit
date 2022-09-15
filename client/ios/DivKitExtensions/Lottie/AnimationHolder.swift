import UIKit
import Base

public protocol AnimatableViewFactory: AnyObject {
  func createAnimatableView(withMode mode: AnimationRepeatMode, repeatCount count: Float) -> AnimatableView
}

public protocol AnimatableView: UIView {
  func play()	
  func setSource(_ source: AnimationSourceType)
}

public enum AnimationRepeatMode {
  case restart
  case reverse
}

public enum AnimationSourceType: Equatable {
  case json([String: Any])
  case data(Data)
  
  public static func == (lhs: AnimationSourceType, rhs: AnimationSourceType) -> Bool {
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

protocol AnimationHolder: AnyObject, CustomDebugStringConvertible {
  var animation: AnimationSourceType? { get }
  @discardableResult
  func requestAnimationWithCompletion(_ completion: @escaping (AnimationSourceType?) -> Void) -> Cancellable?
  func equals(_ other: AnimationHolder) -> Bool
}

func ==(lhs: AnimationHolder, rhs: AnimationHolder) -> Bool {
  if lhs === rhs {
    return true
  }

  return lhs.equals(rhs)
}

func ==(lhs: AnimationHolder?, rhs: AnimationHolder?) -> Bool {
  switch (lhs, rhs) {
  case (.none, .none):
    return true
  case let (.some(value1), .some(value2)):
    return value1 == value2
  default:
    return false
  }
}

func !=(lhs: AnimationHolder?, rhs: AnimationHolder?) -> Bool {
  !(lhs == rhs)
}
