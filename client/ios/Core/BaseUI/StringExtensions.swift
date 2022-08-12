
import Foundation

import BaseTiny

extension String {
  public func with(typo: Typo) -> NSAttributedString {
    #if INTERNAL_BUILD
    assert(typo.isHeightSufficient)
    #endif

    return NSAttributedString(string: self, attributes: typo.attributes)
  }

  public func mutable(withTypo typo: Typo) -> NSMutableAttributedString {
    #if INTERNAL_BUILD
    assert(typo.isHeightSufficient)
    #endif

    return NSMutableAttributedString(string: self, attributes: typo.attributes)
  }
}
