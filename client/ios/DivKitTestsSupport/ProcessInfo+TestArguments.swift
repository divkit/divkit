import Foundation

public extension ProcessInfo {
  func testArgument(_ name: String) -> String? {
    let prefix = "--\(name)="
    return arguments
      .first { $0.hasPrefix(prefix) }
      .map { String($0.dropFirst(prefix.count)) }
  }
}
