enum UITestLaunchArgument: RawRepresentable {
  case scenarioPath(String)
  case connectionPort(UInt16)

  var rawValue: String {
    switch self {
    case let .scenarioPath(path):
      "--divkit-ui-scenario=\(path)"
    case let .connectionPort(port):
      "--divkit-ui-port=\(port)"
    }
  }

  init?(rawValue: String) {
    let parts = rawValue.split(separator: "=", maxSplits: 1, omittingEmptySubsequences: false)
    guard parts.count == 2 else {
      return nil
    }
    switch parts[0] {
    case "--divkit-ui-scenario":
      self = .scenarioPath(String(parts[1]))
    case "--divkit-ui-port":
      guard let port = UInt16(parts[1]) else {
        return nil
      }
      self = .connectionPort(port)
    default:
      return nil
    }
  }
}
