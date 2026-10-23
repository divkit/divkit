import XCTest

extension RunnerStep {
  struct Tap: Decodable {
    let target: RunnerTarget
    let point: Point?
  }

  // Coordinates relative to the target's frame, normalized to 0...1.
  struct Point: Decodable {
    private enum CodingKeys: String, CodingKey {
      case x
      case y
    }

    let vector: CGVector

    init(from decoder: Decoder) throws {
      let container = try decoder.container(keyedBy: CodingKeys.self)
      let x = try container.decode(CGFloat.self, forKey: .x)
      let y = try container.decode(CGFloat.self, forKey: .y)
      guard (0...1).contains(x), (0...1).contains(y) else {
        throw DecodingError.dataCorrupted(.init(
          codingPath: decoder.codingPath,
          debugDescription: "tap point must be within 0...1"
        ))
      }
      vector = CGVector(dx: x, dy: y)
    }
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.Tap) throws {
    try root.interact(with: step.target) { element in
      if let point = step.point {
        element.coordinate(withNormalizedOffset: point.vector).tap()
      } else {
        element.tap()
      }
    }
  }
}
