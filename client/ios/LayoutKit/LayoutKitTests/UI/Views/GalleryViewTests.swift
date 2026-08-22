#if os(iOS)
import CoreGraphics
@_spi(Performance) @testable import LayoutKit
import Testing
import UIKit
import VGSL

@MainActor
@Suite
struct GalleryViewTests {
  @Test
  func layoutIsDeferredWhileBuildingCell() throws {
    let delegate = RenderingDelegateSpy()
    let view = try makeGalleryWithNestedGallery(renderingDelegate: delegate)
    let collectionView = view.collectionViewForTesting

    var hadPendingLayout: [Bool] = []
    delegate.onBlockWillConfigure = {
      collectionView.setNeedsLayout()
      view.updateOffsetToPosition(.paging(index: 0), animated: true)
      hadPendingLayout.append(collectionView.layer.needsLayout())
    }
    buildFirstCell(of: collectionView)

    #expect(hadPendingLayout.contains(true))
    #expect(!hadPendingLayout.contains(false))
  }

  @Test
  func layoutIsSynchronousOutsideCellConfiguration() {
    let view = makeGallery()
    let collectionView = view.collectionViewForTesting

    collectionView.setNeedsLayout()
    view.updateOffsetToPosition(.paging(index: 1), animated: true)

    #expect(!collectionView.layer.needsLayout())
  }

  @Test
  func contentOffsetIsAppliedWhileBuildingCell() throws {
    let delegate = RenderingDelegateSpy()
    let view = try makeGalleryWithNestedGallery(renderingDelegate: delegate)
    let collectionView = view.collectionViewForTesting

    delegate.onBlockWillConfigure = { view.updateOffsetToPosition(.offset(30), animated: true) }
    buildFirstCell(of: collectionView)

    #expect(collectionView.contentOffset.x.isApproximatelyEqualTo(30))
  }

  @Test
  func buildingCellDoesNotLeaveGalleryInDeferredState() throws {
    let delegate = RenderingDelegateSpy()
    let view = try makeGalleryWithNestedGallery(renderingDelegate: delegate)
    let collectionView = view.collectionViewForTesting

    buildFirstCell(of: collectionView)
    collectionView.setNeedsLayout()
    view.updateOffsetToPosition(.paging(index: 0), animated: true)

    #expect(!collectionView.layer.needsLayout())
  }
}

@MainActor
private func buildFirstCell(of collectionView: UICollectionView) {
  _ = collectionView.dataSource!.collectionView(
    collectionView,
    cellForItemAt: IndexPath(item: 0, section: 0)
  )
}

private final class RenderingDelegateSpy: RenderingDelegate {
  var onBlockWillConfigure: () -> Void = {}

  func mapView(_: BlockView, to _: BlockViewID) {}
  func tooltipAnchorViewAdded(anchorView _: TooltipAnchorView) {}
  func tooltipAnchorViewRemoved(anchorView _: TooltipAnchorView) {}
  func reportRenderingError(message _: String, isWarning _: Bool, path _: UIElementPath) {}
  func reportViewWasCreated() {}
  func reportBlockDidConfigure(path _: UIElementPath) {}
  func reportViewWillLayout(path _: UIElementPath) {}
  func reportViewDidLayout(path _: UIElementPath) {}

  func reportBlockWillConfigure(path _: UIElementPath) {
    onBlockWillConfigure()
  }
}

@MainActor
private func makeGallery() -> GalleryView {
  let view = GalleryView(frame: bounds)
  view.configure(
    model: model(items: (0..<5).map { _ in item(block) }),
    state: GalleryViewState(contentOffset: 0, itemsCount: 5),
    observer: nil,
    overscrollDelegate: nil,
    renderingDelegate: nil
  )
  view.layoutIfNeeded()
  return view
}

@MainActor
private func makeGalleryWithNestedGallery(
  renderingDelegate: RenderingDelegate
) throws -> GalleryView {
  let nested = try GalleryBlock(
    gaps: [0, 0],
    children: [block],
    path: UIElementPath("nested"),
    direction: .horizontal,
    crossAlignment: .leading,
    widthTrait: .fixed(100),
    heightTrait: .fixed(50)
  )
  let view = GalleryView(frame: bounds)
  view.configure(
    model: model(items: [item(nested)]),
    state: GalleryViewState(contentOffset: 0, itemsCount: 1),
    observer: nil,
    overscrollDelegate: nil,
    renderingDelegate: renderingDelegate
  )
  view.layoutIfNeeded()
  return view
}

extension GalleryView {
  fileprivate var collectionViewForTesting: UICollectionView {
    subviews.compactMap { $0 as? UICollectionView }.first!
  }
}

private let bounds = CGRect(x: 0, y: 0, width: 100, height: 100)
private let metrics = GalleryViewMetrics(
  axialInsetMode: .fixed(values: .zero),
  spacings: [],
  crossSpacing: 0
)
private let block = TextBlock(
  widthTrait: .fixed(100),
  text: NSAttributedString(string: "item")
)

private func item(_ content: Block) -> GalleryViewModel.Item {
  .init(crossAlignment: .leading, content: content)
}

private func model(items: [GalleryViewModel.Item]) -> GalleryViewModel {
  GalleryViewModel(
    items: items,
    metrics: metrics,
    scrollMode: .default,
    path: UIElementPath("gallery"),
    direction: .horizontal
  )
}
#endif
