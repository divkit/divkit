import VGSL

extension RunnerStep {
  struct DivAction: Decodable {
    let action: JSONDictionary
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.DivAction) async throws {
    try await connection.perform(.divAction(step.action))
  }
}
