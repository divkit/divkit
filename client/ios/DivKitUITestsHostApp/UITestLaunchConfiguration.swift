import Foundation

struct UITestLaunchConfiguration {
  let scenarioPath: String
  let connectionPort: UInt16

  init(arguments: [String]) throws {
    var scenarioPath: String?
    var connectionPort: UInt16?
    for argument in arguments.compactMap(UITestLaunchArgument.init(rawValue:)) {
      switch argument {
      case let .scenarioPath(path):
        scenarioPath = path
      case let .connectionPort(port):
        connectionPort = port
      }
    }
    if let scenarioPath, !scenarioPath.isEmpty {
      self.scenarioPath = scenarioPath
    } else {
      throw LaunchConfigurationError.missingScenarioPath
    }
    if let connectionPort, connectionPort > 0 {
      self.connectionPort = connectionPort
    } else {
      throw LaunchConfigurationError.invalidConnectionPort
    }
  }
}

private enum LaunchConfigurationError: LocalizedError {
  case missingScenarioPath
  case invalidConnectionPort

  var errorDescription: String? {
    switch self {
    case .missingScenarioPath:
      "UI test scenario path is missing from launch arguments"
    case .invalidConnectionPort:
      "UI test connection port is missing or invalid"
    }
  }
}
