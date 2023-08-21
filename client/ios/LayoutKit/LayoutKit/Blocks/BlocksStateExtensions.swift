import Foundation

import CommonCorePublic
import LayoutKitInterface

public struct PagerPath: Equatable {
  public let cardId: String
  public let pagerId: String

  public init(cardId: String, pagerId: String) {
    self.cardId = cardId
    self.pagerId = pagerId
  }
}
