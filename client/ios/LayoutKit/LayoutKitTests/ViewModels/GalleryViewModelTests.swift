@testable import LayoutKit
import VGSL
import XCTest

final class GalleryViewModelTests: XCTestCase {
  func test_ItemsFilteredItemsList() {
    let model = GalleryViewModel(
      blocks: [
        TextBlock(
          widthTrait: .resizable,
          text: NSAttributedString(string: "Item 1")
        ),
        EmptyBlock.zeroSized,
        TextBlock(
          widthTrait: .resizable,
          text: NSAttributedString(string: "Item 3")
        ),
      ],
      metrics: GalleryViewMetrics(gaps: [10, 10]),
      path: "model",
      direction: .vertical
    )

    let expected = [
      GalleryViewModel.Item(
        crossAlignment: .leading,
        content: TextBlock(
          widthTrait: .resizable,
          text: NSAttributedString(string: "Item 1")
        )
      ),
      GalleryViewModel.Item(
        crossAlignment: .leading,
        content: TextBlock(
          widthTrait: .resizable,
          text: NSAttributedString(string: "Item 3")
        )
      ),
    ]

    XCTAssertEqual(model.items, expected)
  }

  func test_RemovedItems() {
    let model = GalleryViewModel(
      blocks: [
        EmptyBlock.zeroSized,
        TextBlock(
          widthTrait: .resizable,
          text: NSAttributedString(string: "Item 1")
        ),
        TextBlock(
          widthTrait: .resizable,
          text: NSAttributedString(string: "Item 3")
        ),
        EmptyBlock.zeroSized,
      ],
      metrics: GalleryViewMetrics(gaps: [10, 10]),
      path: UIElementPath("model"),
      direction: .vertical
    )

    XCTAssertEqual(model.removedItemsIndices, Set([0, 3]))
  }
}
