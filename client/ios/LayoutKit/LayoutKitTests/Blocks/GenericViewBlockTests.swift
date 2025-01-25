import LayoutKit
import VGSL
import XCTest

final class GenericViewBlockTests: XCTestCase {
  func test_WhenSameContentViewSetForAnotherBlock_ContentIsUpdatedForLastBlock() throws {
    let contentView = UIView()
    let block = makeBlock(.view(contentView))

    let view1 = GenericViewBlock.makeBlockView()
    let view2 = GenericViewBlock.makeBlockView()

    block.configure(view1)
    block.configure(view2)
    block.configure(view1)

    XCTAssertIdentical(contentView.superview, view1)
    XCTAssertTrue(view2.subviews.isEmpty)
  }

  func test_WhenSameContentLayerSetForAnotherBlock_ContentIsUpdatedForLastBlock() throws {
    let contentLayer = CALayer()
    let block = makeBlock(.layer(contentLayer))

    let view1 = GenericViewBlock.makeBlockView()
    let view2 = GenericViewBlock.makeBlockView()

    block.configure(view1)
    block.configure(view2)
    block.configure(view1)

    XCTAssertIdentical(contentLayer.superlayer, view1.layer)
    XCTAssertNil(view2.layer.sublayers)
  }
}

private func makeBlock(_ content: GenericViewBlock.Content) -> GenericViewBlock {
  GenericViewBlock(content: content, width: .resizable, height: .resizable)
}

extension GenericViewBlock {
  fileprivate func configure(_ view: UIView & BlockViewProtocol) {
    configureBlockView(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
  }
}
