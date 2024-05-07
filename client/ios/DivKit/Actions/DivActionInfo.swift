import Foundation

import LayoutKit

public struct DivActionInfo {
  public let cardId: DivCardID
  public let logId: String
  public let url: URL?
  public let logUrl: URL?
  public let referer: URL?
  public let source: UserInterfaceAction.DivActionSource
  public let payload: [String: Any]?
}
