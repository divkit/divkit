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

    var intrinsicContentHeight: CGFloat

    var debugDescription: String = ""

    private let heightForWidth: ((CGFloat) -> CGFloat)?

    init(
      widthTrait: LayoutTrait,
      heightTrait: LayoutTrait,
      intrinsicContentWidth: CGFloat,
      intrinsicContentHeight: CGFloat = 0,
      heightForWidth: ((CGFloat) -> CGFloat)? = nil
    ) {
      self.widthTrait = widthTrait
      self.heightTrait = heightTrait
      self.intrinsicContentWidth = intrinsicContentWidth
      self.intrinsicContentHeight = intrinsicContentHeight
      self.heightForWidth = heightForWidth
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

    func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
      heightForWidth?(width) ?? self.intrinsicContentHeight
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

  @Test
  func sizeFor_unboundedHeight_weightedBlock_returnsContentHeight() {
    let weightedBlock = TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    )

    let divViewSize = DivViewSize(block: weightedBlock)
    let size = divViewSize.sizeFor(
      parentViewSize: CGSize(width: 100.0, height: .greatestFiniteMagnitude)
    )
    #expect(size.height == 120.0)
  }

  @Test
  func sizeFor_infiniteHeight_weightedBlock_returnsContentHeight() {
    let weightedBlock = TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    )

    let divViewSize = DivViewSize(block: weightedBlock)
    let size = divViewSize.sizeFor(parentViewSize: CGSize(width: 100.0, height: .infinity))
    #expect(size.height == 120.0)
  }

  @Test
  func sizeFor_unboundedHeight_weightedBlockWithMaxSize_clampsContentHeight() {
    let weightedBlock = TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default, minSize: 0.0, maxSize: 50.0),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    )

    let divViewSize = DivViewSize(block: weightedBlock)
    let size = divViewSize.sizeFor(
      parentViewSize: CGSize(width: 100.0, height: .greatestFiniteMagnitude)
    )
    #expect(size.height == 50.0)
  }

  @Test
  func sizeFor_boundedHeight_weightedBlock_fillsParent() {
    let weightedBlock = TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    )

    let divViewSize = DivViewSize(block: weightedBlock)
    let size = divViewSize.sizeFor(parentViewSize: CGSize(width: 100.0, height: 300.0))
    #expect(size.height == 300.0)
  }

  @Test
  func sizeFor_unboundedWidth_weightedBlock_returnsIntrinsicWidth() {
    let weightedBlock = TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.fixed(200.0),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 200.0
    )

    let divViewSize = DivViewSize(block: weightedBlock)
    let size = divViewSize.sizeFor(
      parentViewSize: CGSize(width: .greatestFiniteMagnitude, height: 100.0)
    )
    #expect(size == CGSize(width: 500.0, height: 200.0))
  }

  @Test
  func sizeFor_unboundedHeight_weightedBlockWithoutIntrinsicHeight_returnsMinHeight() {
    let weightedBlock = TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default, minSize: 30.0),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 0.0
    )

    let divViewSize = DivViewSize(block: weightedBlock)
    let size = divViewSize.sizeFor(
      parentViewSize: CGSize(width: 100.0, height: .greatestFiniteMagnitude)
    )
    #expect(size.height == 30.0)
  }

  @Test
  func equality_ignoresContentWidth() {
    let divViewSize = DivViewSize(block: TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default),
      intrinsicContentWidth: 100.0,
      intrinsicContentHeight: 120.0
    ))
    let otherDivViewSize = DivViewSize(block: TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    ))

    #expect(divViewSize == otherDivViewSize)
  }

  @Test
  func fittingSize_infiniteProposal_matchParentCard_returnsInfinity() {
    let cardSize = DivViewSize(block: TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    ))

    let size = DivHostingView.fittingSize(
      cardSize: cardSize,
      proposal: (width: .infinity, height: .infinity)
    )

    #expect(size == CGSize(width: CGFloat.infinity, height: CGFloat.infinity))
  }

  @Test
  func fittingSize_infiniteProposal_matchParentCardWithMaxSize_returnsMaxSize() {
    let cardSize = DivViewSize(block: TestBlock(
      widthTrait: LayoutTrait.weighted(.default, minSize: 0.0, maxSize: 300.0),
      heightTrait: LayoutTrait.weighted(.default, minSize: 0.0, maxSize: 300.0),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    ))

    let size = DivHostingView.fittingSize(
      cardSize: cardSize,
      proposal: (width: .infinity, height: .infinity)
    )

    #expect(size == CGSize(width: 300.0, height: 300.0))
  }

  @Test
  func fittingSize_infiniteHeightProposal_matchParentCard_returnsInfiniteHeight() {
    let cardSize = DivViewSize(block: TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    ))

    let size = DivHostingView.fittingSize(
      cardSize: cardSize,
      proposal: (width: 100.0, height: .infinity)
    )

    #expect(size == CGSize(width: 100.0, height: CGFloat.infinity))
  }

  @Test
  func fittingSize_infiniteProposal_fixedCard_returnsContentSize() {
    let cardSize = DivViewSize(block: TestBlock(
      widthTrait: LayoutTrait.fixed(200.0),
      heightTrait: LayoutTrait.fixed(300.0),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    ))

    let size = DivHostingView.fittingSize(
      cardSize: cardSize,
      proposal: (width: .infinity, height: .infinity)
    )

    #expect(size == CGSize(width: 500.0, height: 120.0))
  }

  @Test
  func fittingSize_finiteProposal_matchParentCard_returnsProposedSize() {
    let cardSize = DivViewSize(block: TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    ))

    let size = DivHostingView.fittingSize(
      cardSize: cardSize,
      proposal: (width: 100.0, height: 200.0)
    )

    #expect(size == CGSize(width: 100.0, height: 200.0))
  }

  @Test
  func fittingSize_infiniteProposal_matchParentWidthWithMaxSize_computesHeightForMaxWidth() {
    let cardSize = DivViewSize(block: aspectRatioBlock(maxWidth: 300.0))

    let size = DivHostingView.fittingSize(
      cardSize: cardSize,
      proposal: (width: .infinity, height: .infinity)
    )

    #expect(size == CGSize(width: 300.0, height: 150.0))
  }

  @Test
  func fittingSize_infiniteWidthProposal_matchParentWidthWithMaxSize_computesHeightForMaxWidth() {
    let cardSize = DivViewSize(block: aspectRatioBlock(maxWidth: 300.0))

    let size = DivHostingView.fittingSize(
      cardSize: cardSize,
      proposal: (width: .infinity, height: 400.0)
    )

    #expect(size == CGSize(width: 300.0, height: 150.0))
  }

  @Test
  func fittingSize_finiteWidthProposal_matchParentWidthWithMaxSize_computesHeightForWidth() {
    let cardSize = DivViewSize(block: aspectRatioBlock(maxWidth: 300.0))

    let size = DivHostingView.fittingSize(
      cardSize: cardSize,
      proposal: (width: 300.0, height: .infinity)
    )

    #expect(size == CGSize(width: 300.0, height: 150.0))
  }

  @Test
  func fittingSize_infiniteProposal_matchParentWidthWithoutMaxSize_returnsInfiniteHeight() {
    let cardSize = DivViewSize(block: aspectRatioBlock(maxWidth: .infinity))

    let size = DivHostingView.fittingSize(
      cardSize: cardSize,
      proposal: (width: .infinity, height: .infinity)
    )

    #expect(size == CGSize(width: CGFloat.infinity, height: CGFloat.infinity))
  }

  @Test
  func fittingSize_proposalWithoutHeight_returnsNil() {
    let cardSize = DivViewSize(block: TestBlock(
      widthTrait: LayoutTrait.weighted(.default),
      heightTrait: LayoutTrait.weighted(.default),
      intrinsicContentWidth: 500.0,
      intrinsicContentHeight: 120.0
    ))

    let size = DivHostingView.fittingSize(
      cardSize: cardSize,
      proposal: (width: 100.0, height: nil)
    )

    #expect(size == nil)
  }

  /// A `match_parent` width block with `min_size: 0` whose height is half of its width, like an
  /// image with `aspect.ratio: 2`.
  private func aspectRatioBlock(maxWidth: CGFloat) -> TestBlock {
    TestBlock(
      widthTrait: LayoutTrait.weighted(.default, minSize: 0.0, maxSize: maxWidth),
      heightTrait: LayoutTrait.intrinsic,
      intrinsicContentWidth: 0.0,
      heightForWidth: { $0 / 2 }
    )
  }
}
