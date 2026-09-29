extension RunnerStep {
  struct VerifySnapshot: Decodable {
    let name: String
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.VerifySnapshot) throws {
    try verifySnapshotPerformer.perform(name: step.name, on: root)
  }
}
