import UIKit
import PlaygroundSupport
import LayoutKit
import VGSL
import LayoutKitPlayground

let randomColor = Color(
  red: Double.random(in: 0.0...1.0),
  green: Double.random(in: 0.0...1.0),
  blue: Double.random(in: 0.0...1.0),
  alpha: 1.0
)

private func createTextBlock(_ text: String, padding: CGFloat) throws -> Block {
  try ContainerBlock(
    layoutDirection: .vertical,
    heightTrait: .intrinsic,
    horizontalChildrenAlignment: .center,
    verticalChildrenAlignment: .center,
    children: [
      TextBlock(
        widthTrait: .intrinsic,
        text: text.with(typo: Typo.init(size: 16.0, weight: .regular))
      )
    ]
  )
  .addingDecorations(backgroundColor: randomColor)
  .addingVerticalGaps(top: padding, bottom: padding)
  .addingHorizontalGaps(left: padding, right: padding)
}

private func createBlock() throws -> Block {
  try ContainerBlock(
    layoutDirection: .vertical,
    heightTrait: .resizable,
    horizontalChildrenAlignment: .center,
    verticalChildrenAlignment: .center,
    children: [
      createTextBlock("Welcome to LayoutKit!", padding: 25.0),
      createTextBlock("This is a simple example.", padding: 50.0),
      createTextBlock("Enjoy building layouts!", padding: 100.0)
    ]
  )
}

PlaygroundPage.current.liveView = LayoutKitPlaygroundViewController(blockProvider: createBlock)
