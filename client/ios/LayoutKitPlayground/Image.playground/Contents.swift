import LayoutKit
import LayoutKitPlayground
import PlaygroundSupport
import UIKit
import VGSL

private func createBlock() throws -> Block {
  try ContainerBlock(
    layoutDirection: .vertical,
    horizontalChildrenAlignment: .center,
    verticalChildrenAlignment: .center,
    children: [
      ImageBlock(
        imageHolder: UIImage(named: "ferrari_F40_front.jpg")!,
        widthTrait: .fixed(300.0),
        heightTrait: .fixed(200.0)
      ),
    ]
  )
}

PlaygroundPage.current.liveView = LayoutKitPlaygroundViewController(blockProvider: createBlock)
