import UIKit
import PlaygroundSupport
import LayoutKit
import VGSL
import LayoutKitPlayground

private func createBlock() throws -> Block {
  try ContainerBlock(
    layoutDirection: .vertical,
    heightTrait: .resizable,
    horizontalChildrenAlignment: .center,
    verticalChildrenAlignment: .center,
    children: [
      TextBlock(
        widthTrait: .fixed(100.0),
        text: "Lorem ipsum dolor sit amet".with(typo: Typo.init(size: 16.0, weight: .regular)),
        maxIntrinsicNumberOfLines: 1
      ),
      ContainerBlock(
        layoutDirection: .vertical,
        heightTrait: .intrinsic,
        horizontalChildrenAlignment: .center,
        verticalChildrenAlignment: .center,
        children: [
          TextBlock(
            widthTrait: .intrinsic,
            text: "Lorem ipsum".with(typo: Typo.init(size: 26.0, weight: .medium).with(color: .red))
          ),
          TextBlock(
            widthTrait: .intrinsic,
            text: "dolor sit".with(typo: Typo.init(size: 16.0, weight: .regular).with(color: .green))
          ),
          TextBlock(
            widthTrait: .intrinsic,
            text: "amet".with(typo: Typo.init(size: 26.0, weight: .medium).with(color: .purple))
          )
        ]
      ).addingVerticalGaps(16.0),
    ]
  )
}

PlaygroundPage.current.liveView = LayoutKitPlaygroundViewController(blockProvider: createBlock)
