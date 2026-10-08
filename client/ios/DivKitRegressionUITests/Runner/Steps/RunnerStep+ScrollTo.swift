import XCTest

extension RunnerStep {
  struct ScrollTo: Decodable {
    private enum CodingKeys: String, CodingKey {
      case container
      case destination
      case scrollDirection = "scroll_direction"
    }

    enum ScrollDirection: String, Decodable {
      case up
      case down
      case left
      case right
    }

    let container: RunnerTarget
    let destination: RunnerTarget
    // Direction in which to search the scrollable content.
    let scrollDirection: ScrollDirection
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.ScrollTo) throws {
    let container = try resolveTarget(step.container, in: root)
    guard container.waitUntilVisible(timeout: runnerDefaultTimeout) else {
      throw ScrollToError.containerNotVisible(target: step.container)
    }
    let destinationID: String = switch step.destination {
    case let .divID(id): id
    }

    for attempt in 0...maxSwipes {
      let query = container.descendants(matching: .any).matching(identifier: destinationID)
      let count = query.count
      guard count <= 1 else {
        throw ScrollToError.ambiguousDestination(target: step.destination, matchCount: count)
      }
      let destination = query.firstMatch
      let frame = count == 1 ? destination.frame : nil
      let visible = count == 1 && destination.isVisible
        && frame.map { container.frame.contains(CGPoint(x: $0.midX, y: $0.midY)) } == true
      if visible { return }
      guard attempt < maxSwipes else { break }

      let points: (from: CGVector, to: CGVector) = switch step.scrollDirection {
      case .left:
        (.init(dx: 0.5, dy: 0.5), .init(dx: 0.75, dy: 0.5))
      case .right:
        (.init(dx: 0.5, dy: 0.5), .init(dx: 0.25, dy: 0.5))
      case .up:
        (.init(dx: 0.5, dy: 0.5), .init(dx: 0.5, dy: 0.75))
      case .down:
        (.init(dx: 0.5, dy: 0.5), .init(dx: 0.5, dy: 0.25))
      }
      let start = container.coordinate(withNormalizedOffset: points.from)
      let end = container.coordinate(withNormalizedOffset: points.to)
      start.press(forDuration: 0.2, thenDragTo: end)
    }
    throw ScrollToError.destinationNotReached(target: step.destination)
  }
}

private enum ScrollToError: LocalizedError {
  case containerNotVisible(target: RunnerTarget)
  case ambiguousDestination(target: RunnerTarget, matchCount: Int)
  case destinationNotReached(target: RunnerTarget)

  var errorDescription: String? {
    switch self {
    case let .containerNotVisible(target):
      "Scroll container is not visible: \(target)"
    case let .ambiguousDestination(target, matchCount):
      "Expected one destination, found \(matchCount): \(target)"
    case let .destinationNotReached(target):
      "Destination did not become visible after \(maxSwipes) swipes: \(target)"
    }
  }
}

private let maxSwipes = 10
