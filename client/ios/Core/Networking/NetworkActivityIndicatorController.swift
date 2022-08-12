// Copyright 2015 Yandex LLC. All rights reserved.

import Foundation

public protocol NetworkActivityIndicatorUI: AnyObject {
  var isNetworkActivityIndicatorVisible: Bool { get set }
}

public final class NetworkActivityIndicatorController {
  private let UI: NetworkActivityIndicatorUI
  private var numberOfOperations = 0

  public init(UI: NetworkActivityIndicatorUI) {
    self.UI = UI
  }

  public func incrementNumberOfOperations() {
    precondition(Thread.isMainThread)
    numberOfOperations += 1
    UI.isNetworkActivityIndicatorVisible = true
  }

  public func decrementNumberOfOperations() {
    precondition(Thread.isMainThread)
    precondition(numberOfOperations > 0)
    numberOfOperations -= 1
    if numberOfOperations == 0 {
      UI.isNetworkActivityIndicatorVisible = false
    }
  }
}
