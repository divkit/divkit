import XCTest

extension RunnerStep {
  struct VerifySnapshot: Decodable {
    let name: String
    let target: RunnerTarget?
  }
}

extension RunnerExecutor {
  func execute(_ step: RunnerStep.VerifySnapshot) async throws {
    let element: any XCUIScreenshotProviding
    let cropRegion: CGRect?
    if let target = step.target {
      element = try resolveTarget(target, in: root)
      cropRegion = nil
    } else if cardRoot.exists {
      element = cardRoot
      cropRegion = nil
    } else {
      element = XCUIScreen.main
      cropRegion = try await connection.snapshotRegion()
    }
    try verifySnapshotPerformer.perform(name: step.name, on: element, croppingTo: cropRegion)
  }
}
