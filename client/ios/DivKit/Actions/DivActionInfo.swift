import Foundation

import LayoutKit

public struct DivActionInfo {
  public let cardId: DivCardID
  public let logId: String
  public let source: UserInterfaceAction.DivActionSource
  public let payload: [String: Any]?
}
