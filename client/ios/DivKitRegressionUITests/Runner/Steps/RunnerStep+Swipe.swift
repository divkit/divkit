import XCTest

extension RunnerStep {
  struct Swipe: Decodable {
    private enum CodingKeys: String, CodingKey {
      case target
      case direction
      case from
      case to
    }

    private enum Direction: String, Decodable {
      case up
      case down
      case left
      case right

      var points: (from: CGVector, to: CGVector) {
        switch self {
        case .up:
          (.init(dx: 0.5, dy: 0.75), .init(dx: 0.5, dy: 0.25))
        case .down:
          (.init(dx: 0.5, dy: 0.25), .init(dx: 0.5, dy: 0.75))
        case .left:
          (.init(dx: 0.75, dy: 0.5), .init(dx: 0.25, dy: 0.5))
        case .right:
          (.init(dx: 0.25, dy: 0.5), .init(dx: 0.75, dy: 0.5))
        }
      }
    }

    private struct Point: Decodable {
      let x: CGFloat
      let y: CGFloat

      var vector: CGVector { CGVector(dx: x, dy: y) }
    }

    let target: RunnerTarget
    let from: CGVector
    let to: CGVector

    init(from decoder: Decoder) throws {
      let container = try decoder.container(keyedBy: CodingKeys.self)
      target = try container.decode(RunnerTarget.self, forKey: .target)

      switch (container.contains(.direction), container.contains(.from), container.contains(.to)) {
      case (true, false, false):
        let direction = try container.decode(Direction.self, forKey: .direction)
        let points = direction.points
        from = points.from
        to = points.to
      case (false, true, true):
        from = try container.decode(Point.self, forKey: .from).vector
        to = try container.decode(Point.self, forKey: .to).vector
      default:
        throw DecodingError.dataCorrupted(.init(
          codingPath: decoder.codingPath,
          debugDescription: "swipe requires either direction or both from and to"
        ))
      }

      guard [from.dx, from.dy, to.dx, to.dy].allSatisfy({ (0...1).contains($0) }), from != to else {
        throw DecodingError.dataCorrupted(.init(
          codingPath: decoder.codingPath,
          debugDescription: "swipe points must be different and within 0...1"
        ))
      }
    }
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.Swipe) throws {
    try root.interact(with: step.target) { element in
      let start = element.coordinate(withNormalizedOffset: step.from)
      let end = element.coordinate(withNormalizedOffset: step.to)
      start.press(forDuration: swipePressDuration, thenDragTo: end)
    }
  }
}

private let swipePressDuration: TimeInterval = 0.2
