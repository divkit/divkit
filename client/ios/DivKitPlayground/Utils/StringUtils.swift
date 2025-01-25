import CoreGraphics
import DivKit
import Foundation
import VGSL

extension String {
  func withTypo(size: CGFloat, weight: DivFontWeight = .regular) -> NSAttributedString {
    let typo = Typo(
      font: AppComponents.fontProvider.font(weight: weight, size: size)
    )
    return NSAttributedString(string: self).with(typo: typo)
  }
}
