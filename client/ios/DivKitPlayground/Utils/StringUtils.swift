import Foundation
import CoreGraphics

import BaseUIPublic
import DivKit

extension String {
  func withTypo(size: CGFloat, weight: DivFontWeight = .regular) -> NSAttributedString {
    let typo = Typo(
      font: AppComponents.fontProvider.font(weight: weight, size: size)
    )
    return NSAttributedString(string: self).with(typo: typo)
  }
}
