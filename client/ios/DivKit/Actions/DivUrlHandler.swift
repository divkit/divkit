import Foundation

import BasePublic

public protocol DivUrlHandler {
  func handle(_ url: URL, sender: AnyObject?)
}

public struct DivUrlOpenerWrapper: DivUrlHandler {
  private let urlOpener: UrlOpener

  public init(_ urlOpener: @escaping UrlOpener) {
    self.urlOpener = urlOpener
  }

  public func handle(_ url: URL, sender: AnyObject?) {
    urlOpener(url)
  }
}

public struct DivUrlHandlerDelegate: DivUrlHandler {
  private let handle: (URL, AnyObject?) -> Void

  public init(_ handle: @escaping (URL, AnyObject?) -> Void) {
    self.handle = handle
  }

  public func handle(_ url: URL, sender: AnyObject?) {
    handle(url, sender)
  }
}
