extension RunnerStep {
  struct VerifySnapshot: Decodable {
    let name: String
    let target: RunnerTarget?
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.VerifySnapshot) throws {
    let element = try step.target.map { try resolveTarget($0, in: root) } ?? root
    try verifySnapshotPerformer.perform(name: step.name, on: element)
  }
}
