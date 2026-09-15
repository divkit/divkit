#if os(iOS)
import CoreGraphics
@testable import LayoutKit
import Testing
import UIKit
import VGSL

@MainActor
@Suite
struct GalleryViewLayoutReuseTests {
  @Test
  func stateUpdateReusesLayoutAndAppliesScrollPosition() throws {
    let child = MeasuringBlock()
    let initial = try gallery(child: child, offset: 0)
    let view = GalleryView(frame: bounds)
    initial.configureBlockView(view)
    view.layoutIfNeeded()
    let measurements = child.measurements
    #expect(measurements > 0)

    let updated = try gallery(child: child, offset: 20)
    updated.configureBlockView(view)
    view.layoutIfNeeded()

    #expect(child.measurements == measurements)
    #expect(view.state.contentPosition == updated.state.contentPosition)
    #expect(view.collectionViewForTesting.contentOffset.y.isApproximatelyEqualTo(20))
  }

  @Test
  func changedBoundsRebuildLayout() throws {
    let child = MeasuringBlock()
    let block = try gallery(child: child, offset: 0)
    let view = GalleryView(frame: bounds)
    block.configureBlockView(view)
    view.layoutIfNeeded()
    let measurements = child.measurements

    view.bounds.size.width = 160
    block.configureBlockView(view)
    view.layoutIfNeeded()

    #expect(child.measurements > measurements)
    #expect(view.collectionViewForTesting.contentSize.width.isApproximatelyEqualTo(160))
  }

  @Test
  func changedContentRebuildsLayout() throws {
    let child = MeasuringBlock()
    let view = GalleryView(frame: bounds)
    try gallery(child: child, offset: 0).configureBlockView(view)
    view.layoutIfNeeded()
    let originalHeight = view.collectionViewForTesting.contentSize.height
    let replacement = MeasuringBlock(height: 80)

    try gallery(child: replacement, offset: 0).configureBlockView(view)
    view.layoutIfNeeded()

    #expect(replacement.measurements > 0)
    #expect(view.collectionViewForTesting.contentSize.height > originalHeight)
  }

  @Test
  func customLayoutFactoryIsCalledOnEveryConfiguration() throws {
    let block = try gallery(child: MeasuringBlock(), offset: 0)
    let view = GalleryView(frame: bounds)
    var calls = 0
    let factory: GalleryView.LayoutFactory = { model, size in
      calls += 1
      return GalleryViewLayout(model: model, boundsSize: size)
    }

    for _ in 0..<2 {
      view.configure(
        model: block.model,
        state: block.state,
        layoutFactory: factory,
        observer: nil,
        overscrollDelegate: nil,
        renderingDelegate: nil
      )
    }

    #expect(calls == 2)
  }

  @Test
  func defaultLayoutFactoryDoesNotRetainView() throws {
    let block = try gallery(child: MeasuringBlock(), offset: 0)
    weak var releasedView: GalleryView?
    autoreleasepool {
      let view = GalleryView(frame: bounds)
      block.configureBlockView(view)
      releasedView = view
    }
    #expect(releasedView == nil)
  }
}

private let bounds = CGRect(x: 0, y: 0, width: 100, height: 100)

private func gallery(child: Block, offset: CGFloat) throws -> GalleryBlock {
  try GalleryBlock(
    gaps: [0, 0, 0, 0, 0, 0],
    children: Array(repeating: child, times: 5),
    path: UIElementPath("gallery"),
    direction: .vertical,
    crossAlignment: .leading,
    state: GalleryViewState(contentOffset: offset, itemsCount: 5),
    widthTrait: .resizable,
    heightTrait: .resizable
  )
}

private final class MeasuringBlock: SizeForwardingBlock,
  LayoutCachingDefaultImpl, ElementStateUpdatingDefaultImpl {
  let sizeProvider: Block
  private(set) var measurements = 0

  private let height: CGFloat

  var debugDescription: String { "MeasuringBlock" }

  init(height: CGFloat = 40) {
    self.height = height
    sizeProvider = EmptyBlock(widthTrait: .resizable, heightTrait: .fixed(height))
  }

  static func makeBlockView() -> BlockView { EmptyBlock.makeBlockView() }

  func heightOfVerticallyNonResizableBlock(forWidth _: CGFloat) -> CGFloat {
    measurements += 1
    return height
  }

  func equals(_ other: Block) -> Bool { self === other }
  func getImageHolders() -> [ImageHolder] { [] }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    sizeProvider.canConfigureBlockView(view)
  }

  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    sizeProvider.configureBlockView(
      view,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}
#endif
