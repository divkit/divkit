import Foundation
import LayoutKit
import VGSL

#if os(iOS)
import UIKit
#endif

protocol Animator {
  var id: String { get }
  func start(
    startValue: DivVariableValue?,
    endValue: DivVariableValue?,
    duration: Int?,
    startDelay: Int?,
    direction: AnimationDirection?,
    progressInterpolator: ProgressInterpolator?,
    repeatCount: RepeatCount?
  )
  func stop()
}

enum RepeatCount: Equatable {
  case fixed(Int)
  case infinity
}

enum AnimationDirection {
  case alternate
  case normal
  case alternateReverse
  case reverse
}

final class ValueAnimator<I: ValueInterpolator>: Animator {
  typealias AnimatedType = I.ValueType

  struct Configuration {
    let startValue: AnimatedType
    let endValue: AnimatedType
    let duration: Int
    let startDelay: Int?
    let direction: AnimationDirection
    let progressInterpolator: ProgressInterpolator
    let repeatCount: RepeatCount
  }

  let id: String

  private var configuration: Configuration
  private let animationBlock: (AnimatedType) -> Void
  private let valueInterpolator: I
  private let cancelAction: Action
  private let endAction: Action

  private weak var displayLink: DisplayLink? {
    didSet {
      if displayLink !== oldValue {
        oldValue?.invalidate()
      }
    }
  }

  private var startTime: Double?
  private var currentCount: Int = 0
  private var isAnimationForward: Bool = true
  private var item: DispatchWorkItem?

  init(
    id: String,
    valueInterpolator: I,
    configuration: Configuration,
    cancelAction: @escaping Action,
    endAction: @escaping Action,
    animationBlock: @escaping (AnimatedType) -> Void
  ) {
    self.id = id
    self.valueInterpolator = valueInterpolator
    self.configuration = configuration
    self.cancelAction = cancelAction
    self.endAction = endAction
    self.animationBlock = animationBlock
  }

  func start(
    startValue: DivVariableValue?,
    endValue: DivVariableValue?,
    duration: Int?,
    startDelay: Int?,
    direction: AnimationDirection?,
    progressInterpolator: ProgressInterpolator?,
    repeatCount: RepeatCount?
  ) {
    let startValueResolved = if let startValue {
      AnimatedType.makeValueType(from: startValue) ?? configuration.startValue
    } else {
      configuration.startValue
    }
    let endValueResolved = if let endValue {
      AnimatedType.makeValueType(from: endValue) ?? configuration.endValue
    } else {
      configuration.endValue
    }
    let duration = duration ?? configuration.duration
    let progressInterpolator = progressInterpolator ?? configuration.progressInterpolator
    let repeatCount = repeatCount ?? configuration.repeatCount
    let startDelay = startDelay ?? configuration.startDelay
    let direction = direction ?? configuration.direction

    self.configuration = Configuration(
      startValue: startValueResolved,
      endValue: endValueResolved,
      duration: duration,
      startDelay: startDelay,
      direction: direction,
      progressInterpolator: progressInterpolator,
      repeatCount: repeatCount
    )
    let item = DispatchWorkItem { [weak self] in
      guard let self else { return }
      self.startTime = currentMediaTime()
      isAnimationForward = direction == .normal || direction == .alternate
      let displayLink = DisplayLink(target: self, selector: #selector(update))
      self.displayLink = displayLink
      displayLink.addToCurrentRunLoop()
    }
    self.item = item

    if let startDelay, startDelay > 0 {
      DispatchQueue.main.asyncAfter(
        deadline: .now() + Double(startDelay) / 1000,
        execute: item
      )
    } else {
      item.perform()
    }
  }

  func stop() {
    item?.cancel()
    item = nil
    displayLink?.invalidate()
    displayLink = nil
    cancelAction()
  }

  @objc private func update() {
    guard let startTime, displayLink != nil else { return }

    let duration = Double(configuration.duration) / 1000
    let progressInterpolator = configuration.progressInterpolator
    let startValue = configuration.startValue
    let endValue = configuration.endValue
    let repeatCount = configuration.repeatCount

    let currentTime = currentMediaTime()
    let elapsedTime = currentTime - startTime
    let rawProgress = CGFloat(elapsedTime / duration)
    let progress = min(rawProgress, 1.0)
    let interpolatedProgress = progressInterpolator.interpolate(progress: progress)

    let interpolatedValue = valueInterpolator.interpolate(
      from: startValue,
      to: endValue,
      progress: isAnimationForward ? interpolatedProgress : 1 - interpolatedProgress
    )
    animationBlock(interpolatedValue)

    if progress >= 1.0 {
      var needEnd = true
      switch repeatCount {
      case let .fixed(count):
        currentCount += 1
        needEnd = currentCount >= count
      case .infinity:
        self.startTime = currentMediaTime()
        needEnd = false
      }
      if needEnd {
        displayLink?.invalidate()
        displayLink = nil
        endAction()
      } else {
        self.startTime = currentMediaTime()
        if configuration.direction == .alternate || configuration.direction == .alternateReverse {
          isAnimationForward.toggle()
        }
      }
    }
  }
}

#if os(iOS)
private let currentMediaTime = CACurrentMediaTime
typealias DisplayLink = CADisplayLink
extension CADisplayLink {
  fileprivate func addToCurrentRunLoop() {
    add(to: .current, forMode: .common)
  }
}
#else
private let currentMediaTime = { Double(0) }
private class DisplayLink {
  init(target _: Any, selector _: Selector) {}

  func invalidate() {}
  func addToCurrentRunLoop() {}
}
#endif
