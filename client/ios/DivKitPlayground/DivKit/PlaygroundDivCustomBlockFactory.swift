import Foundation

import BasePublic
import BaseUIPublic
import DivKit
import LayoutKit

struct PlaygroundDivCustomBlockFactory: DivCustomBlockFactory {
  private let requester: URLResourceRequesting

  init(requester: URLResourceRequesting) {
    self.requester = requester
  }

  public func makeBlock(
    data: DivCustomData,
    context: DivBlockModelingContext
  ) -> Block {
    if data.name == RiveDivCustomData.divCustomType {
      return RiveAnimationFactory(requester: requester).makeBlock(data: data, context: context)
    } else {
      return TextBlock(
        widthTrait: .intrinsic,
        text: NSAttributedString(string: "DivCustom(custom_type = \(data.name))")
          .with(typo: Typo(size: 16, weight: .regular))
      )
    }
  }
}
