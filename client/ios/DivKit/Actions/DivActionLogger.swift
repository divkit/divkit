import Foundation

/// Deprecated. Use ``DivReporter`` instead.
public protocol DivActionLogger {
  func log(url: URL, referer: URL?, payload: [String: Any]?)
}

struct EmptyDivActionLogger: DivActionLogger {
  func log(url _: URL, referer _: URL?, payload _: [String: Any]?) {}
}
