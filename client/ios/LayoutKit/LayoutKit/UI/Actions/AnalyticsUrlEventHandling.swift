import UIKit

public protocol AnalyticsUrlEventHandling {
  func handle(analyticsUrlEvent: AnalyticsUrlEvent, from sender: AnyObject)
}

public final class AnalyticsUrlEvent: AppActionEventProtocol {
  public let analyticsUrl: URL

  public init(analyticsUrl: URL) {
    self.analyticsUrl = analyticsUrl
  }

  public func makeHandler(responder: UIResponder) -> Handler? {
    (responder as? AnalyticsUrlEventHandling)?.handle(analyticsUrlEvent:from:)
  }
}
