#if os(iOS)
@testable import LayoutKit
import Testing
import UIKit
import VGSL

@MainActor
@Suite
struct TooltipManagerTests {
  @Test
  func factoryViewWithoutContentSizeProvider_reportsErrorAndDoesNotShow() async {
    var reportedError: String?
    let hostView = TestTooltipHostView(bounds: CGRect(x: 0, y: 0, width: 320, height: 480))
    let manager = DefaultTooltipManager(externalView: hostView)
    let anchorView = MockTooltipAnchorView(
      frame: CGRect(x: 10, y: 20, width: 100, height: 40),
      tooltips: [
        BlockTooltip(
          viewSource: .factory { MockPlainTooltipView() },
          params: BlockTooltipParams(
            id: "my_tooltip",
            mode: .modal,
            duration: 0,
            closeByTapOutside: true
          ),
          offset: .zero,
          position: .center
        ),
      ]
    )
    manager.tooltipAnchorViewAdded(anchorView: anchorView)

    let shown = await manager.showTooltip(
      info: TooltipInfo(
        id: "my_tooltip",
        showsOnStart: false,
        multiple: false,
        onError: { reportedError = $0 }
      )
    )

    #expect(shown == false)
    #expect(reportedError?.contains("TooltipContentSizeProviding") == true)
  }

  @Test
  func factoryViewWithoutContentSizeProvider_isClosed() async {
    let content = MockClosingTooltipView(size: nil)
    let manager = makeManager(content: content, providesSize: false)

    let shown = await manager.showTooltip(info: tooltipInfo)

    #expect(!shown)
    #expect(content.closeCount == 1)
  }

  @Test
  func factoryViewWithoutContentSize_isClosed() async {
    let content = MockClosingTooltipView(size: nil)
    let manager = makeManager(content: content, providesSize: true)

    let shown = await manager.showTooltip(info: tooltipInfo)

    #expect(!shown)
    #expect(content.closeCount == 1)
  }

  @Test
  func viewDiscardedAfterReservationIsCancelled_isClosedWithSubstrate() async {
    let content = MockClosingTooltipView()
    let substrate = MockClosingTooltipView()
    let hostView = TestTooltipHostView(bounds: hostBounds)
    let manager = DefaultTooltipManager(externalView: hostView)
    let identity = tooltipInfo.identity
    let anchorView = MockTooltipAnchorView(
      frame: anchorFrame,
      tooltips: [
        makeTooltip(
          viewFactory: {
            manager.hideTooltip(identity: identity)
            return content
          },
          substrateFactory: { substrate }
        ),
      ]
    )
    manager.tooltipAnchorViewAdded(anchorView: anchorView)

    let shown = await manager.showTooltip(info: tooltipInfo)

    #expect(!shown)
    #expect(content.closeCount == 1)
    #expect(substrate.closeCount == 1)
  }

  @Test
  func hideTooltip_closesContentOnceAfterVisibilityNotification() async {
    let events = EventRecorder()
    let content = MockClosingTooltipView(events: events)
    let substrate = MockClosingTooltipView(events: events, name: "substrate")
    let manager = makeManager(content: content, substrate: substrate)
    #expect(await manager.showTooltip(info: tooltipInfo))

    manager.hideTooltip(identity: tooltipInfo.identity)
    manager.hideTooltip(identity: tooltipInfo.identity)

    #expect(content.closeCount == 1)
    #expect(substrate.closeCount == 1)
    #expect(events.values == [
      "content.visibility.zero",
      "substrate.visibility.zero",
      "content.close",
      "substrate.close",
    ])
  }

  @Test
  func tapOutside_closesContentOnce() async {
    let content = MockClosingTooltipView()
    let manager = makeManager(content: content, mode: .nonModal)
    #expect(await manager.showTooltip(info: tooltipInfo))
    let container = await manager.currentTooltipView() as? TooltipContainerView

    _ = container?.hitTest(.zero, with: nil)
    await drainMainQueue()

    #expect(content.closeCount == 1)
  }

  @Test
  func duration_closesContentOnce() async {
    let content = MockClosingTooltipView()
    let manager = makeManager(content: content, duration: 0.001)
    #expect(await manager.showTooltip(info: tooltipInfo))

    #expect(await waitUntil { content.closeCount == 1 })
    #expect(content.closeCount == 1)
  }

  @Test
  func asyncShow_doesNotWaitForDuration() async {
    let content = MockClosingTooltipView()
    let manager = makeManager(content: content, duration: 1)

    #expect(await manager.showTooltip(info: tooltipInfo))
    #expect(content.closeCount == 0)

    manager.hideTooltip(identity: tooltipInfo.identity)
  }

  @Test
  func staleAutoHide_doesNotCloseReshownTooltip() async throws {
    let first = MockClosingTooltipView()
    let second = MockClosingTooltipView()
    let hostView = TestTooltipHostView(bounds: hostBounds)
    let manager = DefaultTooltipManager(externalView: hostView)
    let anchorView = MockTooltipAnchorView(
      frame: anchorFrame,
      tooltips: [makeTooltip(viewFactory: { first }, duration: 0.05)]
    )
    hostView.addAnchorView(anchorView)
    manager.tooltipAnchorViewAdded(anchorView: anchorView)
    #expect(await manager.showTooltip(info: tooltipInfo))

    manager.hideTooltip(identity: tooltipInfo.identity)
    anchorView.tooltips = [makeTooltip(viewFactory: { second })]
    #expect(await manager.showTooltip(info: tooltipInfo))
    try await Task.sleep(nanoseconds: 200_000_000)

    #expect(second.closeCount == 0)
    manager.hideTooltip(identity: tooltipInfo.identity)
  }

  @Test
  func reset_closesContentOnce() async {
    let content = MockClosingTooltipView()
    let manager = makeManager(content: content)
    #expect(await manager.showTooltip(info: tooltipInfo))

    manager.reset()
    manager.reset()

    #expect(content.closeCount == 1)
  }

  @Test
  func managerDeinit_closesVisibleContent() async {
    let content = MockClosingTooltipView()
    var manager: DefaultTooltipManager? = makeManager(content: content)
    #expect(await manager?.showTooltip(info: tooltipInfo) == true)

    manager = nil

    #expect(content.closeCount == 1)
  }

  @Test
  func containerResize_closesContentOnce() async {
    let content = MockClosingTooltipView()
    let manager = makeManager(content: content)
    #expect(await manager.showTooltip(info: tooltipInfo))
    let container = await manager.currentTooltipView() as? TooltipContainerView
    container?.layoutSubviews()

    if var bounds = container?.bounds {
      bounds.size.width += 1
      container?.bounds = bounds
    }
    container?.layoutSubviews()

    #expect(content.closeCount == 1)
  }

  @Test
  func closingModalWithPendingReplacement_thatFailsToShow_hidesWindow() async {
    let mainWindow = UIWindow(frame: hostBounds)
    let modalWindow = UIWindow(frame: hostBounds)
    let manager = DefaultTooltipManager(
      presenter: WindowTooltipPresenter(
        tooltipWindowManager: TooltipWindowManager(
          mainWindow: mainWindow,
          modalWindow: modalWindow
        )
      )
    )
    let first = MockClosingTooltipView()
    let factoryGate = TooltipViewFactoryGate()
    let anchorView = MockTooltipAnchorView(
      frame: anchorFrame,
      tooltips: [makeTooltip(viewFactory: { first })]
    )
    mainWindow.addSubview(anchorView)
    manager.tooltipAnchorViewAdded(anchorView: anchorView)
    #expect(await manager.showTooltip(info: tooltipInfo))
    #expect(!modalWindow.isHidden)

    manager.hideTooltip(identity: tooltipInfo.identity)
    anchorView.tooltips = [
      makeTooltip(viewFactory: { await factoryGate.makeView() }),
    ]
    let replacementShow = Task { @MainActor in
      await manager.showTooltip(info: tooltipInfo)
    }
    #expect(await waitUntil { factoryGate.isWaiting })
    #expect(await waitUntil { modalWindow.isHidden })

    let replacement = MockClosingTooltipView(size: nil)
    factoryGate.resume(with: replacement)
    #expect(await replacementShow.value == false)
    #expect(modalWindow.isHidden)
    #expect(modalWindow.rootViewController == nil)
    #expect(replacement.closeCount == 1)
  }

  @Test
  func closingModalWithPendingReplacement_thatShows_reshowsWindowWithReplacement() async {
    let window = ModalWindows()
    let contentA = MockClosingTooltipView()
    let contentB = MockClosingTooltipView()
    let factoryGate = TooltipViewFactoryGate()
    let anchorView = window.addAnchorView(tooltips: [makeTooltip(viewFactory: { contentA })])
    #expect(await window.manager.showTooltip(info: tooltipInfo))
    #expect(!window.modalWindow.isHidden)

    window.manager.hideTooltip(identity: tooltipInfo.identity)
    anchorView.tooltips = [makeTooltip(viewFactory: { await factoryGate.makeView() })]
    let replacementShow = Task { @MainActor in
      await window.manager.showTooltip(info: tooltipInfo)
    }
    #expect(await waitUntil { factoryGate.isWaiting })
    #expect(await waitUntil { window.modalWindow.isHidden })
    #expect(window.modalWindow.rootViewController == nil)

    factoryGate.resume(with: contentB)
    #expect(await replacementShow.value)

    #expect(!window.modalWindow.isHidden)
    #expect(window.isShowing(contentB))
    #expect(contentA.closeCount == 1)
    #expect(contentB.closeCount == 0)

    window.manager.hideTooltip(identity: tooltipInfo.identity)
    #expect(await waitUntil(timeout: 3) { window.modalWindow.isHidden })
    #expect(window.modalWindow.rootViewController == nil)
    #expect(contentB.closeCount == 1)
  }

  @Test
  func closingModalReplacedBeforeCloseAnimationEnds_keepsReplacementVisible() async {
    let window = ModalWindows()
    let contentA = MockClosingTooltipView()
    let contentB = MockClosingTooltipView()
    let longFadeOut = TransitioningAnimation(
      kind: .fade,
      start: 1,
      end: 0,
      duration: 1,
      delay: 0,
      timingFunction: .easeInEaseOut
    )
    let anchorView = window.addAnchorView(
      tooltips: [makeTooltip(viewFactory: { contentA }, animationOut: [longFadeOut])]
    )
    #expect(await window.manager.showTooltip(info: tooltipInfo))
    weak var weakContainerA: TooltipContainerView?
    weakContainerA = window.shownContainer()
    #expect(weakContainerA != nil)

    window.manager.hideTooltip(identity: tooltipInfo.identity)
    anchorView.tooltips = [makeTooltip(viewFactory: { contentB })]
    #expect(await window.manager.showTooltip(info: tooltipInfo))
    #expect(weakContainerA != nil)

    // The container is released once its close animation has completed.
    #expect(await waitUntil(timeout: 3) { weakContainerA == nil })
    #expect(window.presenter.closedCount == 0)
    #expect(!window.modalWindow.isHidden)
    #expect(window.isShowing(contentB))
    #expect(contentA.closeCount == 1)
    #expect(contentB.closeCount == 0)

    window.manager.hideTooltip(identity: tooltipInfo.identity)
    #expect(await waitUntil(timeout: 3) { window.modalWindow.isHidden })
    #expect(window.modalWindow.rootViewController == nil)
    #expect(window.presenter.closedCount == 1)
    #expect(contentB.closeCount == 1)
  }

  /// The default setup - no `externalView`, so tooltips go to a window, and `modal` mode - is
  /// the one that used to leak: the hidden modal window kept its root view controller and,
  /// through it, the closed tooltip's view. Whatever that view owns (DivKit ties a tooltip's
  /// registrations to it) must go away when the tooltip closes, not at the next modal show.
  @Test
  func modalTooltipInWindow_releasesItsContentOnClose() async {
    let mainWindow = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 480))
    let modalWindow = UIWindow(frame: mainWindow.bounds)
    let manager = DefaultTooltipManager(
      presenter: WindowTooltipPresenter(
        tooltipWindowManager: TooltipWindowManager(
          mainWindow: mainWindow,
          modalWindow: modalWindow
        )
      )
    )
    let created = CreatedViewTracker()
    let anchorView = MockTooltipAnchorView(
      frame: CGRect(x: 10, y: 20, width: 100, height: 40),
      tooltips: [
        BlockTooltip(
          viewSource: .factory {
            let view = MockSizedTooltipView()
            created.view = view
            return view
          },
          params: BlockTooltipParams(
            id: "my_tooltip",
            mode: .modal,
            duration: 0,
            closeByTapOutside: true
          ),
          offset: .zero,
          position: .center
        ),
      ]
    )
    mainWindow.addSubview(anchorView)
    manager.tooltipAnchorViewAdded(anchorView: anchorView)

    let shown = await manager.showTooltip(
      info: TooltipInfo(id: "my_tooltip", showsOnStart: false, multiple: false)
    )
    #expect(shown)
    #expect(created.view != nil)

    manager.hideTooltip(identity: TooltipIdentity(id: "my_tooltip", scopePath: nil))
    // Closing runs a UIView animation, and it is its completion that reports the tooltip
    // closed to the presenter - wait for that rather than assume a timing.
    #expect(await waitUntil(timeout: 3) { created.view == nil })
    #expect(modalWindow.rootViewController == nil)
    #expect(created.view == nil)
  }

  // MARK: show / hide / reset while a show is in progress

  @Test
  func showHideShow_discardsFirstShowAndStartsSecondOnlyAfterIt() async {
    let events = EventRecorder()
    let fixture = GatedFixture(events: events)
    let content1 = MockClosingTooltipView(events: events, name: "content1")
    let content2 = MockClosingTooltipView(events: events, name: "content2")

    let first = fixture.show()
    #expect(await waitUntil { fixture.gate1.isWaiting })
    fixture.manager.hideTooltip(identity: tooltipInfo.identity)
    fixture.useSecondFactory()
    let second = fixture.show()
    await drainMainQueue()
    #expect(!fixture.gate2.isWaiting)

    fixture.gate1.resume(with: content1)

    #expect(await first.value == false)
    #expect(content1.closeCount == 1)
    #expect(await waitUntil { fixture.gate2.isWaiting })
    #expect(events.values == ["factory1", "content1.close", "factory2"])
    fixture.gate2.resume(with: content2)
    #expect(await second.value == true)
    #expect(fixture.shownContainerCount == 1)
    #expect(content2.closeCount == 0)

    fixture.manager.hideTooltip(identity: tooltipInfo.identity)
    #expect(content2.closeCount == 1)
  }

  @Test
  func showHide_discardsTheShowAndNextShowWorksAsUsual() async {
    let fixture = GatedFixture()
    let content1 = MockClosingTooltipView()

    let first = fixture.show()
    #expect(await waitUntil { fixture.gate1.isWaiting })
    fixture.manager.hideTooltip(identity: tooltipInfo.identity)
    fixture.gate1.resume(with: content1)

    #expect(await first.value == false)
    #expect(content1.closeCount == 1)
    #expect(fixture.shownContainerCount == 0)

    fixture.useSecondFactory()
    let content2 = MockClosingTooltipView()
    let second = fixture.show()
    #expect(await waitUntil { fixture.gate2.isWaiting })
    fixture.gate2.resume(with: content2)
    #expect(await second.value == true)
    #expect(fixture.shownContainerCount == 1)
    fixture.manager.hideTooltip(identity: tooltipInfo.identity)
  }

  @Test
  func showHideShowHide_cancelsTheQueuedShow() async {
    let events = EventRecorder()
    let fixture = GatedFixture(events: events)
    let content1 = MockClosingTooltipView()

    let first = fixture.show()
    #expect(await waitUntil { fixture.gate1.isWaiting })
    fixture.manager.hideTooltip(identity: tooltipInfo.identity)
    fixture.useSecondFactory()
    let second = fixture.show()
    await drainMainQueue()
    fixture.manager.hideTooltip(identity: tooltipInfo.identity)

    #expect(await second.value == false)
    fixture.gate1.resume(with: content1)
    #expect(await first.value == false)
    await drainMainQueue()

    #expect(content1.closeCount == 1)
    #expect(!events.values.contains("factory2"))
    #expect(fixture.shownContainerCount == 0)
  }

  @Test
  func showHideShow_whenFirstShowFails_secondShowStillRuns() async {
    let fixture = GatedFixture()
    let content1 = MockClosingTooltipView(size: nil)
    let content2 = MockClosingTooltipView()

    let first = fixture.show()
    #expect(await waitUntil { fixture.gate1.isWaiting })
    fixture.manager.hideTooltip(identity: tooltipInfo.identity)
    fixture.useSecondFactory()
    let second = fixture.show()
    await drainMainQueue()

    fixture.gate1.resume(with: content1)

    #expect(await first.value == false)
    #expect(content1.closeCount == 1)
    #expect(await waitUntil { fixture.gate2.isWaiting })
    fixture.gate2.resume(with: content2)
    #expect(await second.value == true)
    #expect(fixture.shownContainerCount == 1)
    fixture.manager.hideTooltip(identity: tooltipInfo.identity)
  }

  @Test
  func resetDuringShow_discardsIt_andLaterShowStartsAnewAfterIt() async {
    let events = EventRecorder()
    let fixture = GatedFixture(events: events)
    let content1 = MockClosingTooltipView(events: events, name: "content1")
    let content2 = MockClosingTooltipView(events: events, name: "content2")

    let first = fixture.show()
    #expect(await waitUntil { fixture.gate1.isWaiting })
    #expect(fixture.presenter.prepareCount == 1)
    fixture.manager.reset()
    fixture.useSecondFactory()
    let second = fixture.show()
    await drainMainQueue()
    #expect(fixture.presenter.prepareCount == 1)
    #expect(!fixture.gate2.isWaiting)

    fixture.gate1.resume(with: content1)

    #expect(await first.value == false)
    #expect(content1.closeCount == 1)
    #expect(await waitUntil { fixture.gate2.isWaiting })
    #expect(fixture.presenter.prepareCount == 2)
    fixture.gate2.resume(with: content2)
    #expect(await second.value == true)
    #expect(fixture.shownContainerCount == 1)
    fixture.manager.hideTooltip(identity: tooltipInfo.identity)
  }

  @Test
  func showWhileShowingOrVisible_isRejectedWithoutCallingFactory() async {
    let fixture = GatedFixture()
    let content1 = MockClosingTooltipView()

    let first = fixture.show()
    #expect(await waitUntil { fixture.gate1.isWaiting })
    #expect(await fixture.manager.showTooltip(info: tooltipInfo) == false)

    fixture.gate1.resume(with: content1)
    #expect(await first.value == true)
    #expect(await fixture.manager.showTooltip(info: tooltipInfo) == false)

    #expect(fixture.factoryCallCount == 1)
    #expect(fixture.shownContainerCount == 1)
    fixture.manager.hideTooltip(identity: tooltipInfo.identity)
  }
}

@MainActor
private func drainMainQueue() async {
  await withCheckedContinuation { continuation in
    DispatchQueue.main.async {
      continuation.resume()
    }
  }
}

@MainActor
private func waitUntil(
  timeout: TimeInterval = 1,
  condition: @escaping () -> Bool
) async -> Bool {
  let deadline = Date().addingTimeInterval(timeout)
  while !condition(), Date() < deadline {
    try? await Task.sleep(nanoseconds: 10_000_000)
  }
  return condition()
}

@MainActor
private func makeManager(
  content: MockClosingTooltipView,
  substrate: MockClosingTooltipView? = nil,
  mode: BlockTooltip.Mode = .modal,
  duration: TimeInterval = 0,
  providesSize: Bool = true
) -> DefaultTooltipManager {
  let hostView = TestTooltipHostView(bounds: hostBounds)
  let manager = DefaultTooltipManager(externalView: hostView)
  let viewFactory: TooltipViewFactory = if providesSize {
    { content }
  } else {
    { MockUnsizedClosingTooltipView(wrapped: content) }
  }
  let anchorView = MockTooltipAnchorView(
    frame: anchorFrame,
    tooltips: [
      makeTooltip(
        viewFactory: viewFactory,
        substrateFactory: substrate.map { substrate in { substrate } },
        mode: mode,
        duration: duration
      ),
    ]
  )
  hostView.addAnchorView(anchorView)
  manager.tooltipAnchorViewAdded(anchorView: anchorView)
  return manager
}

private func makeTooltip(
  viewFactory: @escaping TooltipViewFactory,
  substrateFactory: TooltipViewFactory? = nil,
  mode: BlockTooltip.Mode = .modal,
  duration: TimeInterval = 0,
  animationOut: [TransitioningAnimation]? = nil
) -> BlockTooltip {
  BlockTooltip(
    viewSource: .factory(viewFactory),
    params: BlockTooltipParams(
      id: tooltipInfo.id,
      mode: mode,
      duration: duration,
      closeByTapOutside: true,
      animationOut: animationOut
    ),
    offset: .zero,
    position: .center,
    substrateViewFactory: substrateFactory
  )
}

private let tooltipInfo = TooltipInfo(
  id: "my_tooltip",
  showsOnStart: false,
  multiple: false
)
private let hostBounds = CGRect(x: 0, y: 0, width: 320, height: 480)
private let anchorFrame = CGRect(x: 10, y: 20, width: 100, height: 40)

private final class CreatedViewTracker {
  weak var view: UIView?
}

private final class MockPlainTooltipView: UIView, VisibleBoundsTrackingLeaf {}

private final class MockSizedTooltipView: UIView, VisibleBoundsTrackingLeaf,
  TooltipContentSizeProviding {
  func tooltipContentSize(constrainedBy _: CGSize, useLegacyWidth _: Bool) -> CGSize? {
    CGSize(width: 50, height: 30)
  }
}

private final class EventRecorder {
  var values = [String]()
}

private final class MockClosingTooltipView: UIView, VisibleBoundsTrackingLeaf,
  TooltipContentSizeProviding, TooltipContentClosing {
  private(set) var closeCount = 0

  private let size: CGSize?
  private let events: EventRecorder?
  private let name: String

  init(
    size: CGSize? = CGSize(width: 50, height: 30),
    events: EventRecorder? = nil,
    name: String = "content"
  ) {
    self.size = size
    self.events = events
    self.name = name
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    nil
  }

  func tooltipContentSize(constrainedBy _: CGSize, useLegacyWidth _: Bool) -> CGSize? {
    size
  }

  func tooltipDidClose() {
    closeCount += 1
    events?.values.append("\(name).close")
  }

  func onVisibleBoundsChanged(from _: CGRect, to: CGRect) {
    if to == .zero {
      events?.values.append("\(name).visibility.zero")
    }
  }
}

private final class MockUnsizedClosingTooltipView: UIView, VisibleBoundsTrackingLeaf,
  TooltipContentClosing {
  private let wrapped: MockClosingTooltipView

  init(wrapped: MockClosingTooltipView) {
    self.wrapped = wrapped
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    nil
  }

  func tooltipDidClose() {
    wrapped.tooltipDidClose()
  }
}

private final class MockTooltipAnchorView: UIView, TooltipAnchorView {
  var tooltips: [BlockTooltip]

  init(frame: CGRect, tooltips: [BlockTooltip]) {
    self.tooltips = tooltips
    super.init(frame: frame)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    nil
  }
}

@MainActor
private final class TooltipViewFactoryGate {
  private(set) var isWaiting = false

  private var continuation: CheckedContinuation<VisibleBoundsTrackingView, Never>?

  func makeView() async -> VisibleBoundsTrackingView {
    isWaiting = true
    return await withCheckedContinuation {
      continuation = $0
    }
  }

  func resume(with view: VisibleBoundsTrackingView) {
    continuation?.resume(returning: view)
    continuation = nil
  }
}

@MainActor
private final class GatedFixture {
  let gate1 = TooltipViewFactoryGate()
  let gate2 = TooltipViewFactoryGate()
  let hostView = TestTooltipHostView(bounds: hostBounds)
  let presenter: CountingTooltipPresenter
  let manager: DefaultTooltipManager
  private(set) var factoryCallCount = 0

  private let anchorView: MockTooltipAnchorView
  private let events: EventRecorder

  var shownContainerCount: Int {
    hostView.coordinateSpace.subviews.filter { $0 is TooltipContainerView }.count
  }

  init(events: EventRecorder = EventRecorder()) {
    self.events = events
    presenter = CountingTooltipPresenter(wrapping: ViewTooltipPresenter(containerView: hostView))
    manager = DefaultTooltipManager(presenter: presenter)
    anchorView = MockTooltipAnchorView(frame: anchorFrame, tooltips: [])
    hostView.addAnchorView(anchorView)
    manager.tooltipAnchorViewAdded(anchorView: anchorView)
    anchorView.tooltips = [
      makeTooltip(viewFactory: { [unowned self] in
        factoryCallCount += 1
        events.values.append("factory1")
        return await gate1.makeView()
      }),
    ]
  }

  func useSecondFactory() {
    anchorView.tooltips = [
      makeTooltip(viewFactory: { [unowned self] in
        factoryCallCount += 1
        events.values.append("factory2")
        return await gate2.makeView()
      }),
    ]
  }

  func show() -> Task<Bool, Never> {
    Task { @MainActor [manager] in
      await manager.showTooltip(info: tooltipInfo)
    }
  }
}

private final class CountingTooltipPresenter: TooltipPresenter {
  private(set) var prepareCount = 0
  private(set) var closedCount = 0

  private let wrapped: TooltipPresenter

  init(wrapping wrapped: TooltipPresenter) {
    self.wrapped = wrapped
  }

  func prepare() -> (constraint: CGRect, coordinateSpace: UIView?)? {
    prepareCount += 1
    return wrapped.prepare()
  }

  func present(_ view: TooltipContainerView, for tooltip: DefaultTooltipManager.Tooltip) {
    wrapped.present(view, for: tooltip)
  }

  func currentTooltipView() async -> UIView? {
    await wrapped.currentTooltipView()
  }

  func onClosed(tooltipID: String, hasRemainingModals: Bool) {
    closedCount += 1
    wrapped.onClosed(tooltipID: tooltipID, hasRemainingModals: hasRemainingModals)
  }

  func reset() {
    wrapped.reset()
  }
}

@MainActor
private final class ModalWindows {
  let mainWindow = UIWindow(frame: hostBounds)
  let modalWindow = UIWindow(frame: hostBounds)
  let presenter: CountingTooltipPresenter
  let manager: DefaultTooltipManager

  init() {
    presenter = CountingTooltipPresenter(
      wrapping: WindowTooltipPresenter(
        tooltipWindowManager: TooltipWindowManager(
          mainWindow: mainWindow,
          modalWindow: modalWindow
        )
      )
    )
    manager = DefaultTooltipManager(presenter: presenter)
  }

  func addAnchorView(tooltips: [BlockTooltip]) -> MockTooltipAnchorView {
    let anchorView = MockTooltipAnchorView(frame: anchorFrame, tooltips: tooltips)
    mainWindow.addSubview(anchorView)
    manager.tooltipAnchorViewAdded(anchorView: anchorView)
    return anchorView
  }

  func shownContainer() -> TooltipContainerView? {
    func find(in view: UIView) -> TooltipContainerView? {
      (view as? TooltipContainerView) ?? view.subviews.lazy.compactMap(find).first
    }
    return modalWindow.rootViewController.flatMap { find(in: $0.view) }
  }

  func isShowing(_ content: UIView) -> Bool {
    modalWindow.rootViewController.map { content.isDescendant(of: $0.view) } ?? false
  }
}

private final class TestTooltipHostView: TooltipHostView {
  private let containerView: UIView
  private var retainedViews = [UIView]()

  var tooltipContainerBounds: CGRect { containerView.bounds }

  var coordinateSpace: ViewType { containerView }

  init(bounds: CGRect) {
    containerView = UIView(frame: bounds)
  }

  func addTooltipView(_ view: ViewType) {
    containerView.addSubview(view)
  }

  func addAnchorView(_ view: UIView) {
    retainedViews.append(view)
  }
}
#endif
