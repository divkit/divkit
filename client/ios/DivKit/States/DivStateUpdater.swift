import Foundation
import BasePublic

public protocol DivStateUpdater: AnyObject {
  func set(
    path: DivStatePath,
    cardId: DivCardID,
    lifetime: DivStateLifetime
  )
}
