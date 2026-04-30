import Foundation
import LayoutKit

public protocol Clearable {
  func clear()
  func clear(cardId: DivCardID)
}
