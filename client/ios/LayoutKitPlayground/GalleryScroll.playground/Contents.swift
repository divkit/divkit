import UIKit
import PlaygroundSupport
import LayoutKit
import VGSL
import LayoutKitPlayground

private func makeTextBlock(_ number: Int) -> Block {
  TextBlock(widthTrait: .intrinsic, text: NSAttributedString(string: "Item \(number)"))
    .addingVerticalGaps(20)
    .addingHorizontalGaps(20)
    .addingDecorations(backgroundColor: .gray)
}

private func createBlock() throws -> Block {
  let gallery = try GalleryBlock(
    model: GalleryViewModel(
      blocks: (0..<7).map { makeTextBlock($0) },
      metrics: GalleryViewMetrics(
        gaps: [0.0] + Array(repeating: 10.0, count: 7)
      ),
      path: "model"
    ),
    state: GalleryViewState(contentOffset: 0, itemsCount: 7),
    widthTrait: .fixed(200),
    heightTrait: .fixed(200)
  ).addingDecorations(backgroundColor: .darkGray)
  
  return try ContainerBlock(
    layoutDirection: .horizontal,
    axialAlignment: .center,
    crossAlignment: .center,
    children: [
      ContainerBlock.Child.init(content: gallery)
    ]
  )
}

PlaygroundPage.current.liveView = LayoutKitPlaygroundViewController(blockProvider: createBlock)
