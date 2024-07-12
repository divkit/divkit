import XCTest

import LayoutKit
import VGSL

final class GridBlockTests: XCTestCase {
  func test_WhenUpdatesState_SendsStatesToChildren() throws {
    let state = GalleryViewState(contentOffset: 1, itemsCount: 2)
    let states = [GalleryBlockTestModels.path: state]
    let block = try! GridBlock(
      widthTrait: .intrinsic,
      heightTrait: .intrinsic,
      contentAlignment: .default,
      items: [.init(
        weight: GridBlock.Item.Weight(),
        contents: GalleryBlockTestModels.base
      )],
      columnCount: 1
    )

    let updatedBlock = try block.updated(withStates: states)
    let updatedGalleryBlock = updatedBlock.items.first?.contents as? GalleryBlock
    XCTAssertEqual(updatedGalleryBlock?.state, state)
  }

  func test_IntrinsicHeightOfResizableGridIsZero() {
    let block = try! GridBlock(
      widthTrait: .resizable,
      heightTrait: .resizable,
      contentAlignment: .default,
      items: [.init(
        weight: GridBlock.Item.Weight(),
        contents: SeparatorBlock(direction: .vertical)
      )],
      columnCount: 1
    )
    XCTAssertEqual(block.intrinsicContentHeight(forWidth: 100), 0)
  }

  func test_KeepWeightWhenUpdateState() {
    struct TestState: ElementState {}

    var item: GridBlock.Item {
      GridBlock.Item(
        span: GridBlock.Span(),
        weight: GridBlock.Item.Weight(
          column: LayoutTrait.Weight(rawValue: 1.0)
        ),
        contents: AlwaysRecreatedTestBlock(
          widthTrait: .weighted(.default),
          heightTrait: LayoutTrait.intrinsic(
            constrained: false,
            minSize: 0.0,
            maxSize: .infinity
          ),
          state: .state1
        )
      )
    }

    let items = [item, item]

    let block = try! GridBlock(
      widthTrait: .weighted(LayoutTrait.Weight.default),
      heightTrait: .intrinsic(
        constrained: false,
        minSize: 0.0,
        maxSize: .infinity
      ),
      items: items,
      columnCount: 2
    )

    let newBlock = try! block.updated(
      withStates: [UIElementPath("test"): TestState()]
    )

    XCTAssertTrue(newBlock == block)
  }
}

private final class AlwaysRecreatedTestBlock: BlockWithTraits {
  var widthTrait: LayoutKit.LayoutTrait
  var heightTrait: LayoutKit.LayoutTrait

  let state: AlwaysRecreatedTestBlockState
  var debugDescription = ""

  init(
    widthTrait: LayoutKit.LayoutTrait,

    heightTrait: LayoutKit.LayoutTrait,
    state: AlwaysRecreatedTestBlockState
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.state = state
  }

  func configureBlockView(
    _: LayoutKit.BlockView,
    observer _: LayoutKit.ElementStateObserver? = nil,
    overscrollDelegate _: BasePublic.ScrollDelegate? = nil,
    renderingDelegate _: LayoutKit.RenderingDelegate? = nil
  ) {}

  var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      value
    case .intrinsic, .weighted:
      0
    }
  }

  func intrinsicContentHeight(
    forWidth _: CGFloat
  ) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      value
    case .intrinsic, .weighted:
      0
    }
  }

  func equals(_ other: LayoutKit.Block) -> Bool {
    guard let otherBlock = other as? AlwaysRecreatedTestBlock else {
      return false
    }

    return self.state == otherBlock.state
  }

  static func makeBlockView() -> LayoutKit.BlockView {
    AlwaysRecreatedTestBlockView()
  }

  func canConfigureBlockView(_ view: LayoutKit.BlockView) -> Bool {
    view is AlwaysRecreatedTestBlockView
  }

  func getImageHolders() -> [BasePublic.ImageHolder] { [] }

  func updated(withStates _: LayoutKit.BlocksState) throws -> Self {
    Self(
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      state: state
    )
  }

  enum AlwaysRecreatedTestBlockState: ElementState, Equatable {
    case state1
  }

  private final class AlwaysRecreatedTestBlockView: BlockView, VisibleBoundsTrackingContainer {
    var visibleBoundsTrackingSubviews: [CommonCorePublic.VisibleBoundsTrackingView] = []

    func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}

    var effectiveBackgroundColor: UIColor?
  }
}

extension AlwaysRecreatedTestBlock: LayoutCachingDefaultImpl {}
