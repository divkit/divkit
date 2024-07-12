import Foundation

import LayoutKit
import VGSL

public protocol AnimationHolder: AnyObject, CustomDebugStringConvertible {
  var animation: AnimationSourceType? { get }
  @discardableResult
  func requestAnimationWithCompletion(_ completion: @escaping (AnimationSourceType?) -> Void)
    -> Cancellable?
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
    true
  case let (.some(value1), .some(value2)):
    value1 == value2
  default:
    false
  }
}

func !=(lhs: AnimationHolder?, rhs: AnimationHolder?) -> Bool {
  !(lhs == rhs)
}
