@testable import DivKit

import LayoutKit
import VGSL
import XCTest

final class DivViewSizeTests: XCTestCase {
  func test_sizeForParentViewSizeForWeightedBlock() {
    let weightedBlock = TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.fixed(200.0),
      intrinsicContentWidth: 500.0
    )

    let divViewSize = DivViewSize(block: weightedBlock)
    let size = divViewSize.sizeFor(parentViewSize: CGSize(width: 100.0, height: 100.0))
    XCTAssertTrue(size == CGSize(width: 100.0, height: 0.0))
  }

  func test_sizeForParentViewSizeForFixedBlock() {
    let fixedBlock = TestBlock(
      widthTrait: LayoutTrait.fixed(200.0),
      heightTrait: LayoutTrait.fixed(200.0),
      intrinsicContentWidth: 500.0
    )

    let divViewSize = DivViewSize(block: fixedBlock)
    let size = divViewSize.sizeFor(parentViewSize: CGSize(width: 100.0, height: 100.0))
    XCTAssertTrue(size == CGSize(width: 500.0, height: 0.0))
  }

  func test_sizeForParentViewSizeForIntrinsicBlock() {
    let intrinsicBlock = TestBlock(
      widthTrait: LayoutTrait.intrinsic(
        constrained: true,
        minSize: 0.0,
        maxSize: .infinity
      ),
      heightTrait: LayoutTrait.fixed(200.0),
      intrinsicContentWidth: 500.0
    )

    let divViewSize = DivViewSize(block: intrinsicBlock)
    let size = divViewSize.sizeFor(parentViewSize: CGSize(width: 100.0, height: 100.0))
    XCTAssertTrue(size == CGSize(width: 100.0, height: 0.0))
  }

  func test_sizeForParentViewSizeForIntrinsicBlock2() {
    let intrinsicBlock = TestBlock(
      widthTrait: LayoutTrait.intrinsic(
        constrained: true,
        minSize: 0.0,
        maxSize: .infinity
      ),
      heightTrait: LayoutTrait.fixed(200.0),
      intrinsicContentWidth: 500.0
    )

    let divViewSize = DivViewSize(block: intrinsicBlock)
    let size = divViewSize.sizeFor(parentViewSize: CGSize(width: 600.0, height: 100.0))
    XCTAssertTrue(size == CGSize(width: 500.0, height: 0.0))
  }

  private final class TestBlock: BlockWithTraits {
    init(
      widthTrait: LayoutTrait,
      heightTrait: LayoutTrait,
      intrinsicContentWidth: CGFloat
    ) {
      self.widthTrait = widthTrait
      self.heightTrait = heightTrait
      self.intrinsicContentWidth = intrinsicContentWidth
    }

    func configureBlockView(
      _: LayoutKit.BlockView,
      observer _: LayoutKit.ElementStateObserver?,
      overscrollDelegate _: VGSLUI.ScrollDelegate?,
      renderingDelegate _: LayoutKit.RenderingDelegate?
    ) {}

    var widthTrait: LayoutKit.LayoutTrait

    var heightTrait: LayoutKit.LayoutTrait

    var intrinsicContentWidth: CGFloat

    func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
      0.0
    }

    func equals(_: LayoutKit.Block) -> Bool {
      true
    }

    var debugDescription: String = ""

    static func makeBlockView() -> LayoutKit.BlockView {
      TestView()
    }

    func canConfigureBlockView(_: LayoutKit.BlockView) -> Bool { false }

    func getImageHolders() -> [VGSLUI.ImageHolder] {
      []
    }

    func laidOut(for _: CGFloat) -> LayoutKit.Block {
      self
    }

    func laidOut(for _: CGSize) -> LayoutKit.Block {
      self
    }

    func updated(withStates _: LayoutKit.BlocksState) throws -> Self {
      self
    }
  }

  private final class TestView: BlockView {
    func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}

    var block: TestBlock?
    var effectiveBackgroundColor: UIColor? { backgroundColor }
  }
}
