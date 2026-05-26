import Foundation

public protocol RemoteURLContaining {
  func getRemoteURLs() -> [URL]
}

extension RemoteURLContaining {
  public func getRemoteURLs() -> [URL] { [] }
}
