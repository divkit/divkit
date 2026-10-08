import Foundation

extension RunnerStep {
  struct VerifyLog: Decodable {
    let source: UITestLogSource
    let id: String
    let count: Int?

    private enum CodingKeys: String, CodingKey {
      case source
      case id
      case count
    }

    init(from decoder: Decoder) throws {
      let container = try decoder.container(keyedBy: CodingKeys.self)
      source = try container.decode(UITestLogSource.self, forKey: .source)
      id = try container.decode(String.self, forKey: .id)
      count = try container.decodeIfPresent(Int.self, forKey: .count)
      if let count, count < 0 {
        throw DecodingError.dataCorruptedError(
          forKey: .count,
          in: container,
          debugDescription: "verify_log count must be nonnegative"
        )
      }
    }
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.VerifyLog) async throws {
    if step.source.hasDuration {
      let snapshot = try await connection.logs()
      guard let duration = snapshot.durations
        .filter({ $0.source == step.source && $0.id == step.id })
        .map(\.durationMilliseconds)
        .max()
      else {
        throw VerifyLogError.missingDuration(source: step.source, id: step.id)
      }
      if duration > 0 {
        try await Task.sleep(for: .milliseconds(duration) + .milliseconds(200))
      }
    }
    let snapshot = try await connection.logs()
    let actual = snapshot.events.filter { $0.source == step.source && $0.id == step.id }.count
    guard step.count.map({ actual == $0 }) ?? (actual > 0) else {
      throw VerifyLogError.countMismatch(
        source: step.source,
        id: step.id,
        expected: step.count,
        actual: actual
      )
    }
  }
}

private enum VerifyLogError: LocalizedError {
  case missingDuration(source: UITestLogSource, id: String)
  case countMismatch(source: UITestLogSource, id: String, expected: Int?, actual: Int)

  var errorDescription: String? {
    switch self {
    case let .missingDuration(source, id):
      "verify_log has no action duration for source=\(source.rawValue), id=\(id)"
    case let .countMismatch(source, id, expected, actual):
      "verify_log expected \(expected.map { "exactly \($0)" } ?? "at least one") event(s) "
        + "with source=\(source.rawValue), id=\(id); observed \(actual)"
    }
  }
}
