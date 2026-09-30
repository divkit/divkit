import Foundation

func loadScenarios(from directoryURL: URL) throws -> [RunnerScenario] {
  let paths = try scenarioPaths(in: directoryURL)
  let scenarios = try paths.compactMap { relativePath in
    let url = directoryURL.appendingPathComponent(relativePath)
    return try decodeScenario(at: url, relativePath: relativePath)
  }

  guard !scenarios.isEmpty else {
    throw ScenarioLoadingError.noIOSScenarios
  }

  return scenarios
}

private func scenarioPaths(in directoryURL: URL) throws -> [String] {
  guard let enumerator = FileManager.default.enumerator(atPath: directoryURL.path) else {
    throw ScenarioLoadingError.dataDirectoryNotReadable(for: directoryURL)
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

    return try RunnerScenario(
      data: data,
      relativePath: relativePath
    )
  } catch {
    throw ScenarioLoadingError.invalidScenario(
      file: url.path,
      underlyingError: error
    )
  }
}

private let iosPlatform = "ios"

private enum ScenarioLoadingError: LocalizedError {
  case dataDirectoryNotReadable(for: URL)
  case invalidScenario(file: String, underlyingError: Error)
  case noIOSScenarios

  var errorDescription: String? {
    switch self {
    case let .dataDirectoryNotReadable(url):
      "\(url.path) cannot be read"
    case let .invalidScenario(file, underlyingError):
      "Failed to decode \(file): \(underlyingError.localizedDescription)"
    case .noIOSScenarios:
      "No iOS scenarios found"
    }
  }
}
