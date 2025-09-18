import LayoutKit
import VGSL
import XCTest

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

  func test_IntrinsicTraitsWithDefaultConstraints() {
    let contentSize = CGSize(width: 50, height: 75)

    let grid = makeGrid(
      contentSize: contentSize,
      widthTrait: .intrinsic(constrained: false, minSize: 0, maxSize: .infinity),
      heightTrait: .intrinsic(constrained: false, minSize: 0, maxSize: .infinity)
    )

    assertIntrinsicContentSize(ofBlock: grid, equals: contentSize)
  }

  func test_IntrinsicTraitsWithMaxConstraints() {
    let contentSize = CGSize(width: 50, height: 75)
    let maxSize = CGSize(width: contentSize.width / 2, height: contentSize.height / 2)

    let grid = makeGrid(
      contentSize: contentSize,
      widthTrait: .intrinsic(constrained: false, minSize: 0, maxSize: maxSize.width),
      heightTrait: .intrinsic(constrained: false, minSize: 0, maxSize: maxSize.height)
    )

    assertIntrinsicContentSize(ofBlock: grid, equals: maxSize)
  }

  func test_IntrinsicTraitsWithMinConstraints() {
    let contentSize = CGSize(width: 50, height: 75)
    let minSize = CGSize(width: contentSize.width * 2, height: contentSize.height * 2)

    let grid = makeGrid(
      contentSize: contentSize,
      widthTrait: .intrinsic(
        constrained: false,
        minSize: minSize.width,
        maxSize: .infinity
      ),
      heightTrait: .intrinsic(
        constrained: false,
        minSize: minSize.height,
        maxSize: .infinity
      )
    )

    assertIntrinsicContentSize(ofBlock: grid, equals: minSize)
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

  private func makeGrid(
    contentSize: CGSize,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait
  ) -> GridBlock {
    let itemBlock = EmptyBlock(
      widthTrait: .fixed(contentSize.width),
      heightTrait: .fixed(contentSize.height)
    )

    return try! GridBlock(
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      contentAlignment: .default,
      items: [
        .init(
          weight: GridBlock.Item.Weight(),
          contents: itemBlock
        ),
      ],
      columnCount: 1
    )
  }

  private func assertIntrinsicContentSize(
    ofBlock block: GridBlock,
    equals: CGSize
  ) {
    let width = block.intrinsicContentWidth
    let height = block.intrinsicContentHeight(forWidth: width)

    XCTAssertEqual(
      CGSize(width: width, height: height),
      equals
    )
  }
}

private final class AlwaysRecreatedTestBlock: BlockWithTraits {
  enum AlwaysRecreatedTestBlockState: ElementState, Equatable {
    case state1
  }

  private final class AlwaysRecreatedTestBlockView: BlockView, VisibleBoundsTrackingContainer {
    var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] = []

    var effectiveBackgroundColor: UIColor?

    func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}
  }

  var widthTrait: LayoutTrait
  var heightTrait: LayoutTrait

  let state: AlwaysRecreatedTestBlockState
  var debugDescription = ""

  var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      value
    case .intrinsic, .weighted:
      0
    }
  }

  init(
    widthTrait: LayoutTrait,

    heightTrait: LayoutTrait,
    state: AlwaysRecreatedTestBlockState
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.state = state
  }

  static func makeBlockView() -> BlockView {
    AlwaysRecreatedTestBlockView()
  }

  func configureBlockView(
    _: BlockView,
    observer _: ElementStateObserver? = nil,
    overscrollDelegate _: ScrollDelegate? = nil,
    renderingDelegate _: RenderingDelegate? = nil
  ) {}

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

  func equals(_ other: Block) -> Bool {
    guard let otherBlock = other as? AlwaysRecreatedTestBlock else {
      return false
    }

    return self.state == otherBlock.state
  }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is AlwaysRecreatedTestBlockView
  }

  func getImageHolders() -> [ImageHolder] { [] }

  func updated(withStates _: BlocksState) throws -> Self {
    Self(
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      state: state
    )
  }
}

extension AlwaysRecreatedTestBlock: LayoutCachingDefaultImpl {}
