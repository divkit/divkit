import Foundation

struct UITestConfiguration {
  private struct PlistContents: Decodable {
    enum CodingKeys: String, CodingKey {
      case referenceSnapshotsPath = "REFERENCE_SNAPSHOTS_PATH"
    }

    let referenceSnapshotsPath: String
  }

  let scenariosResourceDirectory: String
  let scenariosDirectoryURL: URL
  let referenceSnapshotsDirectoryURL: URL
  let scenarioFilter: String?

  init() throws {
    let bundle = Bundle(for: DivKitRegressionUITests.self)
    let scenariosResourceDirectory = "interactive_snapshot_test_data"
    guard let scenariosDirectoryURL = bundle.url(
      forResource: scenariosResourceDirectory,
      withExtension: nil
    ) else {
      throw ConfigurationError.scenariosDirectoryNotFound(scenariosResourceDirectory)
    }
    guard let plistURL = bundle.url(forResource: "Info", withExtension: "plist") else {
      throw ConfigurationError.missingInfoPlist
    }
    let plistContents = try PropertyListDecoder().decode(
      PlistContents.self,
      from: Data(contentsOf: plistURL)
    )
    self.scenariosResourceDirectory = scenariosResourceDirectory
    self.scenariosDirectoryURL = scenariosDirectoryURL
    referenceSnapshotsDirectoryURL = URL(
      fileURLWithPath: plistContents.referenceSnapshotsPath,
      isDirectory: true
    )

    if let filter = ProcessInfo.processInfo.testArgument("divkit-test-filter"),
       !filter.isEmpty {
      scenarioFilter = filter
    } else {
      scenarioFilter = nil
    }
  }

  func scenarioResourcePath(for relativePath: String) -> String {
    "\(scenariosResourceDirectory)/\(relativePath)"
  }
}

private enum ConfigurationError: LocalizedError {
  case missingInfoPlist
  case scenariosDirectoryNotFound(String)

  var errorDescription: String? {
    switch self {
    case .missingInfoPlist:
      "Info.plist is missing from the UI test bundle"
    case let .scenariosDirectoryNotFound(directory):
      "\(directory) is missing from the test bundle"
    }
  }
}
