import Foundation

public enum EnvironmentVars {
  static var isSnapshotTesting: Bool {
    ProcessInfo.processInfo.arguments.contains("SNAPSHOTS_TESTING")
  }

  static var isSnapshotsUpdateMode: Bool {
    ProcessInfo.processInfo.arguments.contains("UPDATE_SNAPSHOTS")
  }
}
