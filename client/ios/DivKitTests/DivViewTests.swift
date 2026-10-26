@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing
import UIKit

@MainActor
@Suite
struct DivViewTests {
  private let components = DivKitComponents()
  private let divView: DivView

  init() {
    divView = DivView(divKitComponents: components)
  }

  @Test(arguments: ["gallery", "pager"])
  func scrollByAcrossRebuilds_beforeFirstLayout_preservesBothRequests(type: String) async throws {
    await renderScrollable(divView, type: type, frame: .zero)
    let nextPage = DivActionTyped.divActionScrollBy(DivActionScrollBy(
      animated: .value(false), id: .value("scrollable"), itemCount: .value(1)
    ))

    sendScrollAction(nextPage)
    sendScrollAction(nextPage)
    divView.frame = testFrame
    divView.forceLayout()

    #expect(try scrollOffset(in: divView) == 200)
  }

  @Test(arguments: ["gallery", "pager"])
  func batchedScrollToThenScrollBy_preservesBothRequests(type: String) async throws {
    await renderScrollable(divView, type: type)

    sendScrollAction(.divActionScrollTo(DivActionScrollTo(
      animated: .value(false),
      destination: .indexDestination(IndexDestination(value: .value(2))),
      id: .value("scrollable")
    )), updateImmediately: false)
    sendScrollAction(.divActionScrollBy(DivActionScrollBy(
      animated: .value(false), id: .value("scrollable"), itemCount: .value(1)
    )))
    divView.forceLayout()

    #expect(try scrollOffset(in: divView) == 300)
  }

  @Test(arguments: ["gallery", "pager"])
  func batchedScrollBy_preservesBothRequests(type: String) async throws {
    await renderScrollable(divView, type: type)
    let nextPage = DivActionTyped.divActionScrollBy(DivActionScrollBy(
      animated: .value(false), id: .value("scrollable"), itemCount: .value(1)
    ))

    sendScrollAction(nextPage, updateImmediately: false)
    sendScrollAction(nextPage)
    divView.forceLayout()

    #expect(try scrollOffset(in: divView) == 200)
  }

  @Test(arguments: ["gallery", "pager"])
  func batchedScrollBy_clampsEachCommandBeforeApplyingTheNext(type: String) async throws {
    await renderScrollable(divView, type: type)

    sendScrollAction(.divActionScrollBy(DivActionScrollBy(
      animated: .value(false), id: .value("scrollable"), itemCount: .value(10)
    )), updateImmediately: false)
    sendScrollAction(.divActionScrollBy(DivActionScrollBy(
      animated: .value(false), id: .value("scrollable"), itemCount: .value(-1)
    )))
    divView.forceLayout()

    #expect(try scrollOffset(in: divView) == 300)
  }

  @Test(arguments: ["gallery", "pager"])
  func batchedScrollBy_appliesRingBeforeClamp(type: String) async throws {
    await renderScrollable(divView, type: type)

    sendScrollAction(.divActionScrollBy(DivActionScrollBy(
      animated: .value(false), id: .value("scrollable"), itemCount: .value(6),
      overflow: .value(.ring)
    )), updateImmediately: false)
    sendScrollAction(.divActionScrollBy(DivActionScrollBy(
      animated: .value(false), id: .value("scrollable"), itemCount: .value(-2)
    )))
    divView.forceLayout()

    #expect(try scrollOffset(in: divView) == 0)
  }

  @Test
  func batchedGalleryOffsetThenItem_resolvesItemFromPendingOffset() async throws {
    await renderScrollable(divView, type: "gallery")

    sendScrollAction(.divActionScrollTo(DivActionScrollTo(
      animated: .value(false),
      destination: .offsetDestination(OffsetDestination(value: .value(150))),
      id: .value("scrollable")
    )), updateImmediately: false)
    sendScrollAction(.divActionScrollBy(DivActionScrollBy(
      animated: .value(false), id: .value("scrollable"), itemCount: .value(1)
    )))
    divView.forceLayout()

    #expect(try scrollOffset(in: divView) == 200)
  }

  @Test(arguments: ["gallery", "pager"])
  func firstLayout_doesNotReportScrolling(type: String) async {
    let host = GalleryScrollEventHost()
    host.addSubview(divView)

    await renderScrollable(divView, type: type)

    #expect(host.events.isEmpty)
  }

  @Test(arguments: ["gallery", "pager"])
  func clampedScrollBy_doesNotReplayWhenCardReopens(type: String) async throws {
    await renderScrollable(divView, type: type)
    sendScrollAction(.divActionScrollTo(DivActionScrollTo(
      animated: .value(false),
      destination: .endDestination(EndDestination()), id: .value("scrollable")
    )))
    divView.forceLayout()
    #expect(try scrollOffset(in: divView) == 400)

    sendScrollAction(.divActionScrollBy(DivActionScrollBy(
      id: .value("scrollable"), itemCount: .value(1)
    )))
    let reopenedView = DivView(divKitComponents: components)
    await renderScrollable(reopenedView, type: type)

    #expect(try scrollOffset(in: reopenedView) == 400)
  }

  @Test(arguments: ["gallery", "pager"])
  func scrollBy_beforeFirstLayout_usesStoredPosition(type: String) async throws {
    await renderScrollable(divView, type: type, frame: .zero)
    sendScrollAction(.divActionScrollTo(DivActionScrollTo(
      animated: .value(false),
      destination: .indexDestination(IndexDestination(value: .value(2))),
      id: .value("scrollable")
    )))

    sendScrollAction(.divActionScrollBy(DivActionScrollBy(
      id: .value("scrollable"), itemCount: .value(1)
    )))
    divView.frame = testFrame
    divView.forceLayout()

    #expect(try scrollOffset(in: divView) == 300)
  }

  @Test
  func tabsScrollBy_beforeFirstLayout_updatesStoredPosition() async throws {
    await renderScrollable(divView, type: "tabs", frame: .zero)
    sendScrollAction(.divActionScrollTo(DivActionScrollTo(
      animated: .value(false),
      destination: .indexDestination(IndexDestination(value: .value(2))),
      id: .value("scrollable")
    )))

    sendScrollAction(.divActionScrollBy(DivActionScrollBy(
      id: .value("scrollable"), itemCount: .value(1)
    )))
    divView.frame = testFrame
    divView.forceLayout()

    let state: TabViewState? = components.blockStateStorage.getState(
      UIElementPath("scroll") + "0" + "scrollable"
    )
    #expect(state?.selectedPageIndex == 3)
    #expect(state?.scrollRequest == nil)
  }

  @Test
  func tooltipContentSize_matchesBlockSizing() async throws {
    let constraint = CGSize(width: 240, height: 320)
    let divs = [
      divContainer(
        height: .divFixedSize(DivFixedSize(value: .value(40))),
        width: .divFixedSize(DivFixedSize(value: .value(80)))
      ),
      divContainer(
        height: .divFixedSize(DivFixedSize(value: .value(40))),
        width: .divMatchParentSize(DivMatchParentSize())
      ),
      divText(
        text: "Tooltip",
        width: .divWrapContentSize(DivWrapContentSize()),
        height: .divWrapContentSize(DivWrapContentSize())
      ),
    ]

    for (index, div) in divs.enumerated() {
      let cardId = DivCardID(rawValue: "tooltip_size_\(index)")
      let data = divData(div)
      let block = try data.makeBlock(
        context: components.makeContext(cardId: cardId, cachedImageHolders: [])
      )
      let view = DivView(divKitComponents: components)
      await view.setSource(.init(kind: .divData(data), cardId: cardId))

      #expect(
        view.tooltipContentSize(constrainedBy: constraint, useLegacyWidth: true)
          == block.intrinsicSize
      )
      #expect(
        view.tooltipContentSize(constrainedBy: constraint, useLegacyWidth: false)
          == block.size(forResizableBlockSize: constraint)
      )
    }
  }

  @Test
  func visibilityActions_afterZeroFrame() async {
    await divView.setData(appearTestData)

    divView.appear()
    divView.disappear()
    divView.appear()

    let delays = divView.visibilityHierarchyDepth()
    await skipMainRunLoopCycles(delays)

    #expect(components.visibilityCounter.visibilityCount(for: appearPath) == 2)
  }

  @Test
  func disappearActions_afterZeroFrame() async {
    await divView.setData(disappearTestData)

    divView.appear()
    divView.disappear()
    divView.appear()
    divView.disappear()

    let delays = divView.visibilityHierarchyDepth()
    await skipMainRunLoopCycles(delays)

    #expect(components.visibilityCounter.visibilityCount(for: disappearPath) == 2)
  }

  @Test
  func tooltipAnchor_receivesTooltipOfNewData() async {
    await divView.setData(tooltipData(text: "a"))
    divView.forceLayout()
    let tooltips = divView.tooltipAnchor?.tooltips

    await divView.setData(tooltipData(text: "b"))
    divView.forceLayout()

    #expect(tooltips != nil)
    #expect(divView.tooltipAnchor?.tooltips != tooltips)
  }

  @Test
  func sizeThatFits_unboundedHeight_matchParentRoot_returnsContentHeight() async {
    await divView.setData(matchParentRootTestData)

    #expect(divView.cardSize?.height == .matchParent)

    let size = divView.sizeThatFits(CGSize(width: 100, height: CGFloat.greatestFiniteMagnitude))
    #expect(size.height == 40)
  }

  @Test
  func sizeThatFits_unboundedHeight_matchParentItem_countsItemAsMinSize() async {
    await divView.setData(matchParentItemTestData)

    #expect(divView.cardSize?.height == .matchParent)

    let size = divView.sizeThatFits(CGSize(width: 100, height: CGFloat.greatestFiniteMagnitude))
    #expect(size.height == 40)
  }

  private func renderScrollable(
    _ view: DivView,
    type: String,
    frame: CGRect = testFrame
  ) async {
    let page: [String: Any] = [
      "type": "text", "text": "Item",
      "width": ["type": "fixed", "value": 100],
      "height": ["type": "fixed", "value": 100],
    ]
    let item: [String: Any] = type == "tabs" ? ["title": "Tab", "div": page] : page
    view.frame = frame
    await view.setSource(.init(kind: .json([
      "card": ["log_id": "scroll", "states": [["state_id": 0, "div": [
        "type": type, "id": "scrollable", "orientation": "horizontal",
        "width": ["type": "match_parent"],
        "height": ["type": "fixed", "value": 100],
        "item_spacing": 0,
        "layout_mode": ["type": "percentage", "page_width": ["type": "percentage", "value": 100.0]],
        "items": Array(repeating: item, count: 5),
      ]]]],
    ]), cardId: "scroll"))
    components.forceUpdate()
    view.forceLayout()
    await skipMainRunLoopCycles(2)
  }

  private func sendScrollAction(_ typed: DivActionTyped, updateImmediately: Bool = true) {
    components.actionHandler.handle(
      DivAction(logId: .value("scroll"), typed: typed),
      path: "scroll", source: .tap, sender: nil
    )
    if updateImmediately {
      components.forceUpdate()
    }
  }

  private func scrollOffset(in view: UIView) throws -> CGFloat {
    try #require(view.scrollableCollection).contentOffset.x
  }
}

extension DivView {
  fileprivate func setData(_ data: DivData) async {
    await setSource(.init(kind: .divData(data), cardId: "card"))
    frame = testFrame
  }

  fileprivate func appear() {
    onVisibleBoundsChanged(to: testFrame)
    forceLayout()
    delay()
  }

  fileprivate func disappear() {
    onVisibleBoundsChanged(to: .zero)
    forceLayout()
    delay()
  }
}

extension UIView {
  fileprivate var scrollableCollection: UICollectionView? {
    self as? UICollectionView ?? subviews.lazy.compactMap(\.scrollableCollection).first
  }

  fileprivate var tooltipAnchor: TooltipAnchorView? {
    self as? TooltipAnchorView ?? subviews.lazy.compactMap(\.tooltipAnchor).first
  }
}

private func tooltipData(text: String) -> DivData {
  divData(
    divSeparator(
      tooltips: [DivTooltip(div: divText(text: text), id: "tooltip", position: .value(.center))]
    )
  )
}

private func delay() {
  RunLoop.current.run(until: Date().addingTimeInterval(0.01))
}

private final class GalleryScrollEventHost: UIView, GalleryScrollEventHandling {
  private(set) var events: [GalleryScrollEvent] = []

  func handle(galleryScrollEvent: GalleryScrollEvent, from _: AnyObject) {
    events.append(galleryScrollEvent)
  }
}

private let testFrame = CGRect(x: 0, y: 0, width: 100, height: 100)
private let appearPath = UIElementPath("card") + "0" + "text" + "appear" + "appear_action"
private let disappearPath = UIElementPath("card") + "0" + "text" + "disappear" + "disappear_action"

private let appearTestData = divData(
  divText(
    text: "Sample",
    width: .divFixedSize(DivFixedSize(value: .value(100))),
    visibilityActions: [
      DivVisibilityAction(
        logId: .value("appear_action"),
        logLimit: .value(10),
        visibilityDuration: .value(0)
      ),
    ]
  )
)

private let disappearTestData = divData(
  divText(
    disappearActions: [
      DivDisappearAction(
        disappearDuration: .value(0),
        logId: .value("disappear_action"),
        logLimit: .value(10),
        visibilityPercentage: .value(0)
      ),
    ],
    text: "Sample",
    width: .divFixedSize(DivFixedSize(value: .value(100)))
  )
)

private let matchParentRootTestData = divData(
  divContainer(
    height: .divMatchParentSize(DivMatchParentSize()),
    items: [
      divText(
        text: "Sample",
        width: .divFixedSize(DivFixedSize(value: .value(100))),
        height: .divFixedSize(DivFixedSize(value: .value(40)))
      ),
    ]
  )
)

private let matchParentItemTestData = divData(
  divContainer(
    height: .divMatchParentSize(DivMatchParentSize()),
    items: [
      divText(
        text: "Sample",
        width: .divFixedSize(DivFixedSize(value: .value(100))),
        height: .divFixedSize(DivFixedSize(value: .value(40)))
      ),
      divImage(
        height: .divMatchParentSize(DivMatchParentSize()),
        imageUrl: "https://image.png",
        width: .divFixedSize(DivFixedSize(value: .value(100)))
      ),
    ]
  )
)
