enum UITestLaunchArgument: RawRepresentable {
  case scenarioPath(String)
  case connectionPort(UInt16)
  case snapshotTesting

  private enum Key: String {
    case scenarioPath = "--divkit-ui-scenario"
    case connectionPort = "--divkit-ui-port"
    case snapshotTesting = "SNAPSHOTS_TESTING"
  }

  var rawValue: String {
    let key = self.key.rawValue
    return switch self {
    case let .scenarioPath(path):
      "\(key)=\(path)"
    case let .connectionPort(port):
      "\(key)=\(port)"
    case .snapshotTesting:
      key
    }
  }

  private var key: Key {
    switch self {
    case .scenarioPath:
      .scenarioPath
    case .connectionPort:
      .connectionPort
    case .snapshotTesting:
      .snapshotTesting
    }
  }

  init?(rawValue: String) {
    let parts = rawValue.split(separator: "=", maxSplits: 1, omittingEmptySubsequences: false)
    guard let name = parts.first, let key = Key(rawValue: String(name)) else {
      return nil
    }
    let value = String(parts.dropFirst().first ?? "")
    switch key {
    case .scenarioPath:
      self = .scenarioPath(value)
    case .connectionPort:
      guard let port = UInt16(value) else {
        return nil
      }
      self = .connectionPort(port)
    case .snapshotTesting:
      self = .snapshotTesting
    }
  }
}
