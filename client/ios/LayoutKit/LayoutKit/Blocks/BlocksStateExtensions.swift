import Foundation

import CommonCorePublic
import LayoutKitInterface

extension BlocksState {
  public func pagerViewState(for pagerPath: PagerPath?) -> PagerViewState? {
    pagerPath.flatMap { pagerPath in
      first(where: { pagerPath.matches($0.key) })?.value as? PagerViewState
    }
  }
}

public struct PagerPath: Equatable {
  let cardId: String
  let pagerId: String

  public init(cardId: String, pagerId: String) {
    self.cardId = cardId
    self.pagerId = pagerId
  }
}

extension PagerPath {
  fileprivate func matches(_ path: UIElementPath) -> Bool {
    if path.root != cardId {
      return false
    }
    return path.leaf == pagerId
  }
}
