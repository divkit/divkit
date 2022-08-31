import Foundation

import DivKit
import LayoutKit

struct DemoDivCustomBlockFactory: DivCustomBlockFactory {
  public func makeBlock(
    data: DivCustomData,
    context _: DivBlockModelingContext
  ) -> Block {
    let name = data.name
    switch name {
    case "generic":
      let view = TextBlock(
        widthTrait: .resizable,
        text: NSAttributedString(string: "Generic")
      ).makeBlockView()
      return GenericViewBlock(view: view, height: 100)
    default:
      return TextBlock(
        widthTrait: .resizable,
        text: NSAttributedString(string: "DivCustom(custom_id = \(name))")
      )
    }
  }
}
