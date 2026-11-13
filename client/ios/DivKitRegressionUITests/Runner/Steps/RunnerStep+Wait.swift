import Foundation

extension RunnerStep {
  struct Wait: Decodable {
    private enum CodingKeys: String, CodingKey {
      case durationMilliseconds = "duration_ms"
    }

    let durationMilliseconds: Int64

    init(from decoder: Decoder) throws {
      let container = try decoder.container(keyedBy: CodingKeys.self)
      durationMilliseconds = try container.decode(Int64.self, forKey: .durationMilliseconds)
      guard durationMilliseconds > 0 else {
        throw DecodingError.dataCorruptedError(
          forKey: .durationMilliseconds,
          in: container,
          debugDescription: "wait duration_ms must be positive"
        )
      }
    }
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.Wait) async throws {
    try await Task.sleep(for: .milliseconds(step.durationMilliseconds))
  }
}
