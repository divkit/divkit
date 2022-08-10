// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation
import QuartzCore

extension CAAnimation {
  public func onDidStop(_ handler: @escaping (_ finished: Bool) -> Void) {
    assert(delegateAdapter.stopHandler == nil)
    delegateAdapter.stopHandler = handler
  }

  public func onDidStop(_ handler: @escaping () -> Void) {
    assert(delegateAdapter.stopHandler == nil)
    delegateAdapter.stopHandler = { _ in handler() }
  }

  public func onDidStart(_ handler: @escaping () -> Void) {
    assert(delegateAdapter.startHandler == nil)
    delegateAdapter.startHandler = handler
  }

  public static func makePathAnimation(
    withStates states: [CGPath],
    duration: TimeInterval
  ) -> CAAnimation {
    precondition(states.count > 1)
    let totalDuration = duration
    let pathAnimation = CABasicAnimation(keyPath: "path")
    pathAnimation.toValue = states[1]
    pathAnimation.duration = totalDuration

    return pathAnimation
  }

  private var delegateAdapter: DelegateAdapter {
    guard let delegate = self.delegate as? DelegateAdapter else {
      assert(self.delegate == nil)
      let adapter = DelegateAdapter()
      self.delegate = adapter
      return adapter
    }
    return delegate
  }
}

private class DelegateAdapter: NSObject, CAAnimationDelegate {
  var startHandler: (() -> Void)?
  var stopHandler: ((Bool) -> Void)?

  func animationDidStart(_: CAAnimation) {
    startHandler?()
  }

  func animationDidStop(_: CAAnimation, finished: Bool) {
    stopHandler?(finished)
  }
}
