import QuartzCore

extension BlockAnimation {
  public var keyFrameAnimation: CAKeyframeAnimation {
    let result = CAKeyframeAnimation()
    result.keyPath = changes.keyPath
    result.values = changes.values
    result.keyTimes = keyTimes.map { $0.value as NSNumber }
    result.duration = duration.value
    result.isAdditive = changes.isAdditive
    result.repeatCount = .infinity
    result.timingFunction = .from(blockTimingFunction: timingFunction)

    return result
  }
}

extension AnimationChanges {
  public var keyPath: String {
    switch self {
    case .transform:
      "transform"
    case .opacity:
      "opacity"
    }
  }
}

extension AnimationChanges {
  public var values: [NSValue] {
    switch self {
    case let .transform(values):
      values.map { NSValue(caTransform3D: $0) }
    case let .opacity(values):
      values.map { NSNumber(value: $0 as Float) }
    }
  }
}

extension AnimationChanges {
  fileprivate var isAdditive: Bool {
    switch self {
    case .opacity:
      false
    case .transform:
      true
    }
  }
}

extension CAMediaTimingFunction {
  fileprivate static func from(blockTimingFunction: TimingFunction) -> CAMediaTimingFunction {
    switch blockTimingFunction {
    case .linear:
      .linear
    case .easeIn:
      .easeIn
    case .easeOut:
      .easeOut
    case .easeInEaseOut:
      .easeInEaseOut
    }
  }
}
