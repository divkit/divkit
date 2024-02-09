import Foundation

/// The ``DivUrlHandler`` protocol allows you to implement the processing of custom actions that
/// `DivKit` cannot handle.
///
/// Conforming to this protocol enables your class to define custom behaviors for handling URLs.
///
/// The protocol requires the implementation of the `handle(_:sender:)` method, which receives a URL
/// to be handled and an optional `sender` object.
public protocol DivUrlHandler {
  /// Handles the specified URL.
  ///
  /// - Parameters:
  ///   - url: The URL to be processed and handled.
  ///   - sender: An optional sender object that can provide additional context for the URL handling
  /// process.
  func handle(_ url: URL, sender: AnyObject?)

  /// Handles the specified URL.
  ///
  /// - Parameters:
  ///   - url: The URL to be processed and handled.
  ///   - info: Additional information about associated `action`.
  ///   - sender: An optional sender object that can provide additional context for the URL handling
  /// process.
  func handle(_ url: URL, info: DivActionInfo, sender: AnyObject?)
}

extension DivUrlHandler {
  public func handle(_: URL, sender _: AnyObject?) {}

  public func handle(_ url: URL, info _: DivActionInfo, sender: AnyObject?) {
    handle(url, sender: sender)
  }
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
