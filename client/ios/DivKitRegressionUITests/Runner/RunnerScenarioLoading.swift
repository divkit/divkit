import Foundation

func loadAutomatedScenarios(from bundle: Bundle) throws -> [RunnerScenario] {
  guard let dataDirectoryURL = bundle.url(
    forResource: dataDirectoryName,
    withExtension: nil
  ) else {
    throw ScenarioLoadingError.dataDirectoryNotFound
  }
  let paths = try scenarioPaths(in: dataDirectoryURL)
  let scenarios = try paths.compactMap { relativePath in
    let url = dataDirectoryURL.appendingPathComponent(relativePath)
    return try decodeScenario(at: url, relativePath: relativePath)
  }

  guard !scenarios.isEmpty else {
    throw ScenarioLoadingError.noIOSScenarios
  }

  return scenarios.sorted { $0.caseID < $1.caseID }
}

private func scenarioPaths(in directoryURL: URL) throws -> [String] {
  guard let enumerator = FileManager.default.enumerator(atPath: directoryURL.path) else {
    throw ScenarioLoadingError.dataDirectoryNotReadable
  }

  return enumerator
    .compactMap { $0 as? String }
    .filter { $0.hasSuffix(".json") }
    .sorted()
}

private func decodeScenario(
  at url: URL,
  relativePath: String
) throws -> RunnerScenario? {
  do {
    let data = try Data(contentsOf: url)
    let decoder = JSONDecoder()
    let header = try decoder.decode(RunnerScenarioHeader.self, from: data)

    guard header.platforms.contains(iosPlatform) else {
      return nil
    }

    let scenario = try decoder.decode(RunnerScenario.self, from: data)
    return RunnerScenario(
      decoded: scenario,
      relativePath: "\(dataDirectoryName)/\(relativePath)"
    )
  } catch {
    throw ScenarioLoadingError.invalidScenario(
      file: relativePath,
      underlyingError: error
    )
  }
}

private let dataDirectoryName = "automated"
private let iosPlatform = "ios"

private enum ScenarioLoadingError: LocalizedError {
  case dataDirectoryNotFound
  case dataDirectoryNotReadable
  case invalidScenario(file: String, underlyingError: Error)
  case noIOSScenarios

  var errorDescription: String? {
    switch self {
    case .dataDirectoryNotFound:
      "Automated regression test data is missing from the test bundle"
    case .dataDirectoryNotReadable:
      "Automated regression test data cannot be read"
    case let .invalidScenario(file, underlyingError):
      "Failed to decode automated/\(file): \(underlyingError.localizedDescription)"
    case .noIOSScenarios:
      "No iOS automated regression scenarios found"
    }
  }
}
