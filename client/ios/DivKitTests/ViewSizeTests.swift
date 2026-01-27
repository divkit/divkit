@testable import DivKit
import LayoutKit
import Testing
import UIKit
import VGSL

@Suite
struct DivViewSizeTests {
  private final class TestBlock: BlockWithTraits {
    var widthTrait: LayoutKit.LayoutTrait

    var heightTrait: LayoutKit.LayoutTrait

    var intrinsicContentWidth: CGFloat

    var debugDescription: String = ""

    init(
      widthTrait: LayoutTrait,
      heightTrait: LayoutTrait,
      intrinsicContentWidth: CGFloat
    ) {
      self.widthTrait = widthTrait
      self.heightTrait = heightTrait
      self.intrinsicContentWidth = intrinsicContentWidth
    }

    static func makeBlockView() -> LayoutKit.BlockView {
      TestView()
    }

    func configureBlockView(
      _: LayoutKit.BlockView,
      observer _: LayoutKit.ElementStateObserver?,
      overscrollDelegate _: VGSLUI.ScrollDelegate?,
      renderingDelegate _: LayoutKit.RenderingDelegate?
    ) {}

    func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
      0.0
    }

    func equals(_: LayoutKit.Block) -> Bool {
      true
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
    var block: TestBlock?

    var effectiveBackgroundColor: UIColor? { backgroundColor }

    func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}
  }

  @Test
  func sizeForParentViewSizeForWeightedBlock() {
    let weightedBlock = TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.fixed(200.0),
      intrinsicContentWidth: 500.0
    )

    let divViewSize = DivViewSize(block: weightedBlock)
    let size = divViewSize.sizeFor(parentViewSize: CGSize(width: 100.0, height: 100.0))
    #expect(size == CGSize(width: 100.0, height: 0.0))
  }

  @Test
  func sizeForParentViewSizeForFixedBlock() {
    let fixedBlock = TestBlock(
      widthTrait: LayoutTrait.fixed(200.0),
      heightTrait: LayoutTrait.fixed(200.0),
      intrinsicContentWidth: 500.0
    )

    let divViewSize = DivViewSize(block: fixedBlock)
    let size = divViewSize.sizeFor(parentViewSize: CGSize(width: 100.0, height: 100.0))
    #expect(size == CGSize(width: 500.0, height: 0.0))
  }

  @Test
  func sizeForParentViewSizeForIntrinsicBlock() {
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
    #expect(size == CGSize(width: 100.0, height: 0.0))
  }

  @Test
  func sizeForParentViewSizeForIntrinsicBlock2() {
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
    #expect(size == CGSize(width: 500.0, height: 0.0))
  }
}
