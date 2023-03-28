import Foundation

import CommonCorePublic

extension TextBlock {
  public convenience init?(copyingAttributesFrom block: TextBlock, text: String) {
    let mutableAttributedText = block.text.mutableCopy()
    (mutableAttributedText as AnyObject).mutableString.setString(text)

    guard
      let attributedString = mutableAttributedText as? NSAttributedString
    else {
      return nil
    }

    self.init(
      widthTrait: block.widthTrait,
      heightTrait: block.heightTrait,
      text: attributedString,
      verticalAlignment: block.verticalAlignment,
      maxIntrinsicNumberOfLines: block.maxIntrinsicNumberOfLines,
      minNumberOfHiddenLines: block.minNumberOfHiddenLines,
      images: block.images,
      accessibilityElement: nil,
      truncationToken: nil,
      canSelect: block.canSelect
    )
  }
}
