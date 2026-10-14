import Foundation

public enum EnvironmentVars {
  static let isUITesting = ProcessInfo.processInfo.arguments.contains("UI_TESTING")
  static let isSnapshotTesting = ProcessInfo.processInfo.arguments.contains("SNAPSHOTS_TESTING")
  static let isSnapshotsUpdateMode = ProcessInfo.processInfo.arguments.contains("UPDATE_SNAPSHOTS")
}
