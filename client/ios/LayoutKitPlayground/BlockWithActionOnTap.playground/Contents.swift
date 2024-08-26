import UIKit
import PlaygroundSupport
import LayoutKit
import VGSL
import LayoutKitPlayground

private var counter = 0
private let buttonActionURL = URL(string: "button_action")!

private func createBlock() throws -> Block {
  try ContainerBlock(
    layoutDirection: .vertical,
    heightTrait: .resizable,
    horizontalChildrenAlignment: .center,
    verticalChildrenAlignment: .center,
    children: [
      TextBlock(
        widthTrait: .intrinsic,
        text: "Tap count: \(counter)".with(typo: Typo.init(size: 26.0, weight: .medium))
      ),
      TextBlock(
        widthTrait: .intrinsic,
        text: "+1".with(typo: Typo.init(size: 16.0, weight: .regular))
      )
      .addingEdgeGaps(32.0)
      .addingDecorations(
        boundary: .clipCorner(radius: 16.0),
        backgroundColor: .gray
      )
      .addingDecorations(
        action: UserInterfaceAction(
          payload: .url(buttonActionURL),
          path: "button"
        )
      )
    ])
}

PlaygroundPage.current.liveView = LayoutKitPlaygroundViewController(
  blockProvider: createBlock,
  actionHandler: { counter += 1 },
  buttonActionURL: buttonActionURL
)
