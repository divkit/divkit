import Foundation

import BaseUI
import DivKit
import LayoutKit

struct DemoDivCustomBlockFactory: DivCustomBlockFactory {
  public func makeBlock(
    data: DivCustomData,
    context _: DivBlockModelingContext
  ) -> Block {
    TextBlock(
      widthTrait: .intrinsic,
      text: NSAttributedString(string: "DivCustom(custom_type = \(data.name))")
        .with(typo: Typo(size: 16, weight: .regular))
    )
  }
}
