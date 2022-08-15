import QuartzCore

public enum AnimationChanges: Equatable {
  case transform([CATransform3D])
  case opacity([Float])
}

extension CATransform3D: Equatable {}

public func ==(lhs: CATransform3D, rhs: CATransform3D) -> Bool {
  CATransform3DEqualToTransform(lhs, rhs)
}

public struct BlockAnimation: Equatable {
  public let changes: AnimationChanges
  public let keyTimes: [KeyTime]
  public let duration: Duration
  public let timingFunction: TimingFunction

  public init(
    changes: AnimationChanges,
    keyTimes: [KeyTime],
    duration: Duration,
    timingFunction: TimingFunction = .linear
  ) {
    switch changes {
    case let .transform(values):
      precondition(!values.isEmpty, "Values should not be empty")
      precondition(values.count == keyTimes.count)
    case let .opacity(values):
      precondition(!values.isEmpty, "Values should not be empty")
      precondition(values.count == keyTimes.count)
    }

    self.changes = changes
    self.keyTimes = keyTimes
    self.duration = duration
    self.timingFunction = timingFunction
  }

  public struct KeyTime: ExpressibleByFloatLiteral, ExpressibleByIntegerLiteral, Equatable {
    public let value: Double

    public init(_ value: Double) {
      precondition(value >= 0 && value <= 1.0, "KeyTime value must be in [0; 1]")
      self.value = value
    }

    public init(floatLiteral value: Double) {
      self.init(value)
    }

    public init(integerLiteral value: IntegerLiteralType) {
      self.init(Double(value))
    }
  }
}

extension BlockAnimation {
  public static func makeLoopedRotation(duration: Duration) -> BlockAnimation {
    let keyframes = [
      CGAffineTransform(rotationAngle: 0),
      CGAffineTransform(rotationAngle: CGFloat.pi / 2),
      CGAffineTransform(rotationAngle: CGFloat.pi),
      CGAffineTransform(rotationAngle: 3 * CGFloat.pi / 2),
      CGAffineTransform(rotationAngle: 2 * CGFloat.pi),
    ].map(CATransform3DMakeAffineTransform)

    return BlockAnimation(
      changes: AnimationChanges.transform(keyframes),
      keyTimes: [0, 0.25, 0.5, 0.75, 1],
      duration: duration
    )
  }

  public static var spinner: BlockAnimation {
    makeLoopedRotation(duration: 0.5)
  }
}
