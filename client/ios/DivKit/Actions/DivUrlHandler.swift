import Foundation

public protocol DivUrlHandler {
  func handle(_ url: URL, sender: AnyObject?)
}

public struct DivUrlHandlerDelegate: DivUrlHandler {
  private let handle: (URL, AnyObject?) -> Void

  public init(_ handle: @escaping (URL) -> Void) {
    self.handle = { url, _ in handle(url) }
  }

  public init(_ handle: @escaping (URL, AnyObject?) -> Void) {
    self.handle = handle
  }

  public func handle(_ url: URL, sender: AnyObject?) {
    handle(url, sender)
  }
}
