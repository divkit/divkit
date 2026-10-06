import CoreGraphics
import VGSL

#if os(iOS)
import UIKit
#endif

/// The `TooltipActionPerformer` protocol defines actions for showing and hiding tooltips within the
/// DivKit.
///
/// Conforming to this protocol allows your class to perform actions related to tooltips. It
/// introduces methods to control the display of tooltips:
/// - Synchronous `showTooltip(info:)`: Use this method to request showing a tooltip.
/// - Asynchronous `showTooltip(info:)`: Use this method to show a tooltip and report whether it was
/// displayed.
/// - `hideTooltip(identity:)`: Use this method to hide the tooltip described by `TooltipIdentity`.
public protocol TooltipActionPerformer {
  /// Shows a tooltip with the provided `TooltipInfo`.
  ///
  /// - Parameter info: The `TooltipInfo` containing the necessary information to display the
  /// tooltip.
  func showTooltip(info: TooltipInfo)

  /// Shows a tooltip with the provided `TooltipInfo` and reports whether it was actually
  /// displayed.
  ///
  /// The returned value resolves once the tooltip is on screen or the attempt is provably done
  /// (no presenter, no matching anchor view, or a tooltip with the same id is already showing).
  /// The `duration`-based auto-hide, if any, runs independently and does not delay the result.
  ///
  /// A request made while the tooltip is being hidden waits for that and then starts anew; it
  /// returns `false` if it was cancelled by a hide or reset meanwhile.
  ///
  /// - Parameter info: The `TooltipInfo` containing the necessary information to display the
  /// tooltip.
  /// - Returns: `true` if the tooltip was shown, `false` otherwise.
  @MainActor
  func showTooltip(info: TooltipInfo) async -> Bool

  /// Hides the tooltip described by the given `TooltipInfo`.
  ///
  /// - Parameter identity: The `TooltipIdentity` containing the tooltip id and optional scope.
  func hideTooltip(identity: TooltipIdentity)
}

extension TooltipActionPerformer {
  @MainActor
  public func showTooltip(info: TooltipInfo) async -> Bool {
    let showTooltipSync: (TooltipInfo) -> Void = showTooltip(info:)
    showTooltipSync(info)
    return true
  }
}

#if os(iOS)
/// The `TooltipManager` protocol is a dependency responsible for processing and displaying tooltips
/// in the application.
///
/// Conforming to this protocol allows your class to act as a tooltip manager, handling various
/// tooltip-related tasks. It combines three other protocols as its superclasses:
/// - `TooltipActionPerformer`: Provides functionality for performing actions related to tooltips.
/// - `RenderingDelegate`: Acts as a delegate for rendering tooltips.
///
/// The `TooltipManager` protocol introduces two methods to handle tooltip anchor views:
/// - `tooltipAnchorViewAdded(anchorView:)`: Notifies the manager when a tooltip anchor view is
/// added to the view hieararchy.
/// - `tooltipAnchorViewRemoved(anchorView:)`: Notifies the manager when a tooltip anchor view is
/// removed from the view hieararchy.
///
/// An implementation must call ``TooltipContentClosing/tooltipDidClose()`` for content created by
/// a ``BlockTooltip/ViewSource/factory(_:)`` when the tooltip closes or when the created view is
/// discarded without being shown.
public protocol TooltipManager: AnyObject, TooltipActionPerformer, RenderingDelegate {
  /// Notifies the manager when a tooltip anchor view is added to the view hieararchy.
  ///
  /// - Parameter anchorView: The event that anchorView appeared in view hierarchy.  An anchor view
  /// is the view to which a tooltip is attached.
  func tooltipAnchorViewAdded(anchorView: TooltipAnchorView)

  /// Notifies the manager when a tooltip anchor view is removed from the view hieararchy.
  ///
  /// - Parameter anchorView: The event that anchorView disappeared from view hierarchy.  An anchor
  /// view is the view to which a tooltip is attached.
  func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView)

  /// Removes all tooltips.
  func reset()

  /// Sets handler for the tooltip view UI events.
  func setHandler(_ handler: @escaping (UIActionEvent) -> Void)
}

extension TooltipManager {
  public func setHandler(_: @escaping (UIActionEvent) -> Void) {}
}

public class DefaultTooltipManager: TooltipManager {
  public struct Tooltip {
    public let params: BlockTooltipParams
    public let view: VisibleBoundsTrackingView
    public let substrateView: VisibleBoundsTrackingView?
    public let bringToTopId: String?
  }

  /// Show state per tooltip identity. Invariant: at most one show is in progress per identity.
  /// `show`, `hide` and `reset` during a show only change the desired state, and the show applies
  /// it when it finishes. `r` is the request held by `hiding(next: r)`, started anew afterwards.
  ///
  /// | event         | no record | showing     | hiding(nil) | hiding(r) | visible |
  /// |---------------|-----------|-------------|-------------|-----------|---------|
  /// | show          | start     | rejected    | queue as r  | rejected  | rejected|
  /// | hide / reset  | -         | hiding(nil) | -           | cancel r  | close   |
  /// | returned view | -         | display     | discard     | discard, r| -       |
  /// | failed        | -         | none        | none        | start r   | -       |
  ///
  /// Each transition is one operation under the lock; UI calls are left to the caller.
  class TooltipStates {
    struct ShowRequest {
      /// Called exactly once: with the result of its show, or `false` if rejected or cancelled.
      let info: TooltipInfo
      let completion: (Bool) -> Void
    }

    enum ShowDecision {
      case start
      case queued
      case rejected
    }

    struct HideOutcome {
      var view: TooltipContainerView?
      var cancelledRequest: ShowRequest?
    }

    struct ResetOutcome {
      var views: [TooltipContainerView] = []
      var cancelledRequests: [ShowRequest] = []
    }

    enum ShowResolution {
      case display
      case discard(next: ShowRequest?)
    }

    enum Phase: Equatable {
      case showing
      case hiding(hasNext: Bool)
      case visible
    }

    private enum State {
      case showing
      case hiding(next: ShowRequest?)
      case visible(TooltipContainerView)
    }

    private var tooltips: [TooltipIdentity: State] = [:]
    private let lock = AllocatedUnfairLock()

    var hasOpenModals: Bool {
      lock.withLock {
        tooltips.values.contains {
          if case let .visible(view) = $0 {
            view.isModal
          } else {
            false
          }
        }
      }
    }

    func phase(of identity: TooltipIdentity) -> Phase? {
      lock.withLock {
        switch tooltips[identity] {
        case nil: nil
        case .showing: .showing
        case let .hiding(next): .hiding(hasNext: next != nil)
        case .visible: .visible
        }
      }
    }

    func requestShow(_ request: ShowRequest) -> ShowDecision {
      let identity = request.info.identity
      return lock.withLock {
        switch tooltips[identity] {
        case nil:
          tooltips[identity] = .showing
          return .start
        case .hiding(nil):
          tooltips[identity] = .hiding(next: request)
          return .queued
        case .showing, .hiding, .visible:
          return .rejected
        }
      }
    }

    func requestHide(_ identity: TooltipIdentity) -> HideOutcome {
      lock.withLock {
        switch tooltips[identity] {
        case nil:
          return HideOutcome()
        case .showing:
          tooltips[identity] = .hiding(next: nil)
          return HideOutcome()
        case let .hiding(next):
          tooltips[identity] = .hiding(next: nil)
          return HideOutcome(cancelledRequest: next)
        case let .visible(view):
          tooltips[identity] = nil
          return HideOutcome(view: view)
        }
      }
    }

    /// Decides what a show that returned content does. The record stays `showing` for `.display`
    /// until the caller has the container and calls `markVisible`.
    func finishShow(_ identity: TooltipIdentity) -> ShowResolution {
      lock.withLock {
        if case .showing = tooltips[identity] {
          return .display
        }
        return discardLocked(identity)
      }
    }

    /// `showing` → `visible(view)`. Anything else means the show is no longer wanted.
    func markVisible(_ identity: TooltipIdentity, view: TooltipContainerView) -> ShowResolution {
      lock.withLock {
        if case .showing = tooltips[identity] {
          tooltips[identity] = .visible(view)
          return .display
        }
        return discardLocked(identity)
      }
    }

    func failShow(_ identity: TooltipIdentity) -> ShowRequest? {
      lock.withLock {
        switch tooltips[identity] {
        case .showing:
          tooltips[identity] = nil
          return nil
        case let .hiding(next):
          tooltips[identity] = next == nil ? nil : .showing
          return next
        case .visible, nil:
          return nil
        }
      }
    }

    /// Removes the view only if it is still the visible one. With no record or a show in progress
    /// returns `expectedView` and changes nothing, so a closing view never affects a newer show.
    @discardableResult
    func remove(
      _ identity: TooltipIdentity,
      ifShowing expectedView: TooltipContainerView
    ) -> TooltipContainerView? {
      lock.withLock {
        switch tooltips[identity] {
        case nil, .showing, .hiding:
          return expectedView
        case let .visible(currentView) where currentView !== expectedView:
          return nil
        case let .visible(view):
          tooltips[identity] = nil
          return view
        }
      }
    }

    func beginReset() -> ResetOutcome {
      lock.withLock {
        var outcome = ResetOutcome()
        for (identity, state) in tooltips {
          switch state {
          case .showing:
            tooltips[identity] = .hiding(next: nil)
          case let .hiding(next):
            tooltips[identity] = .hiding(next: nil)
            next.map { outcome.cancelledRequests.append($0) }
          case let .visible(view):
            tooltips[identity] = nil
            outcome.views.append(view)
          }
        }
        return outcome
      }
    }

    func reset() {
      let outcome = beginReset()
      outcome.cancelledRequests.forEach { $0.completion(false) }
      outcome.views.forEach { $0.close(animated: false) }
    }

    private func discardLocked(_ identity: TooltipIdentity) -> ShowResolution {
      guard case let .hiding(next) = tooltips[identity] else {
        return .discard(next: nil)
      }
      tooltips[identity] = next == nil ? nil : .showing
      return .discard(next: next)
    }
  }

  private typealias ShowOutcome = (shown: Bool, next: TooltipStates.ShowRequest?)

  private struct WeakBlockView {
    weak var view: BlockView?

    init(_ view: BlockView) {
      self.view = view
    }
  }

  public var shownTooltips: Property<Set<String>>

  private var viewsById: [BlockViewID: WeakBlockView] = [:]

  private var handleAction: (UIActionEvent) -> Void
  private var existingAnchorViews = WeakCollection<TooltipAnchorView>()
  private var stateStore = TooltipStates()
  private var previousOrientation = UIDevice.current.orientation

  private let lock = AllocatedUnfairLock()
  private let presenter: TooltipPresenter

  public convenience init(
    shownTooltips: Property<Set<String>> = Property(),
    handleAction: @escaping (UIActionEvent) -> Void = { _ in },
    externalView: TooltipHostView? = nil
  ) {
    self.init(
      shownTooltips: shownTooltips,
      handleAction: handleAction,
      presenter: externalView.map { ViewTooltipPresenter(containerView: $0) }
        ?? WindowTooltipPresenter()
    )
  }

  init(
    shownTooltips: Property<Set<String>> = Property(),
    handleAction: @escaping (UIActionEvent) -> Void = { _ in },
    presenter: TooltipPresenter
  ) {
    self.presenter = presenter
    self.handleAction = handleAction
    self.shownTooltips = shownTooltips

    NotificationCenter.default.addObserver(
      self,
      selector: #selector(orientationDidChange),
      name: UIDevice.orientationDidChangeNotification,
      object: nil
    )
  }

  deinit {
    NotificationCenter.default.removeObserver(
      self,
      name: UIDevice.orientationDidChangeNotification,
      object: nil
    )
    let stateStore = stateStore
    onMainThread {
      stateStore.reset()
    }
  }

  public func showTooltip(info: TooltipInfo) {
    requestShow(TooltipStates.ShowRequest(info: info, completion: { _ in }))
  }

  /// A queued request waits through a `CheckedContinuation` resumed by its own completion.
  @MainActor
  public func showTooltip(info: TooltipInfo) async -> Bool {
    await withCheckedContinuation { continuation in
      requestShow(
        TooltipStates.ShowRequest(
          info: info,
          completion: { continuation.resume(returning: $0) }
        )
      )
    }
  }

  public func hideTooltip(identity: TooltipIdentity) {
    let outcome = stateStore.requestHide(identity)
    outcome.cancelledRequest?.completion(false)
    outcome.view?.close(animated: true)
  }

  public func tooltipAnchorViewAdded(anchorView: TooltipAnchorView) {
    existingAnchorViews.append(anchorView)
  }

  public func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView) {
    existingAnchorViews.remove(anchorView)
  }

  public func mapView(_ view: BlockView, to id: BlockViewID) {
    lock.withLock {
      viewsById[id] = WeakBlockView(view)
    }
  }

  public func reset() {
    viewsById.removeAll()
    stateStore.reset()
    presenter.reset()
  }

  public func setHandler(_ handler: @escaping (UIActionEvent) -> Void) {
    handleAction = handler
  }

  @MainActor
  func currentTooltipView() async -> UIView? {
    await presenter.currentTooltipView()
  }

  @objc func orientationDidChange(_: Notification) {
    let orientation = UIDevice.current.orientation
    guard orientation != previousOrientation, !orientation.isFlat else { return }
    if !(orientation.isPortrait && previousOrientation.isPortrait) {
      reset()
    }
    previousOrientation = orientation
  }

  private func findAnchorView(for info: TooltipInfo) -> TooltipAnchorView? {
    let matchingViews = existingAnchorViews.reduce(into: [TooltipAnchorView]()) { views, view in
      if let view,
         view.firstMatchingTooltip(
           id: info.id,
           scopePath: info.scopePath
         ) != nil {
        views.append(view)
      }
    }

    let suffix = info.scopePath == nil ? "" : " in scope"
    switch matchingViews.count {
    case 0:
      info.onError?("Tooltip with id '\(info.id)' not found" + suffix)
      return nil
    case 1:
      return matchingViews.first
    default:
      info.onError?("Tooltip with id '\(info.id)' is ambiguous" + suffix)
      return nil
    }
  }

  private func requestShow(_ request: TooltipStates.ShowRequest) {
    switch stateStore.requestShow(request) {
    case .start:
      startShow(request)
    case .queued:
      break
    case .rejected:
      request.completion(false)
    }
  }

  private func startShow(_ request: TooltipStates.ShowRequest) {
    let info = request.info
    guard let anchorView = findAnchorView(for: info),
          let prep = presenter.prepare() else {
      finish(request, shown: false, next: stateStore.failShow(info.identity))
      return
    }
    Task { @MainActor [self] in
      let result = await performShow(info: info, anchorView: anchorView, prep: prep)
      finish(request, shown: result.shown, next: result.next)
    }
  }

  private func finish(
    _ request: TooltipStates.ShowRequest,
    shown: Bool,
    next: TooltipStates.ShowRequest?
  ) {
    request.completion(shown)
    if let next {
      startShow(next)
    }
  }

  @MainActor
  private func performShow(
    info: TooltipInfo,
    anchorView: TooltipAnchorView,
    prep: (constraint: CGRect, coordinateSpace: UIView?)
  ) async -> ShowOutcome {
    guard let tooltip = await anchorView.makeTooltip(
      id: info.id,
      scopePath: info.scopePath,
      in: prep.constraint,
      relativeTo: prep.coordinateSpace,
      onError: info.onError
    ) else {
      return (false, stateStore.failShow(info.identity))
    }
    return displayTooltip(tooltip, info: info)
  }

  @MainActor
  private func displayTooltip(
    _ tooltip: Tooltip,
    info: TooltipInfo
  ) -> ShowOutcome {
    let key = info.identity

    func discard(_ next: TooltipStates.ShowRequest?) -> ShowOutcome {
      tooltip.notifyContentDidClose()
      return (false, next)
    }

    if case let .discard(next) = stateStore.finishShow(key) {
      return discard(next)
    }

    weak var weakView: TooltipContainerView?
    let view = TooltipContainerView(
      tooltip: tooltip,
      handleAction: handleAction,
      onCloseAction: { [weak self] in
        guard let self, let view = weakView else { return }
        guard stateStore.remove(key, ifShowing: view) != nil else { return }
        presenter.onClosed(
          tooltipID: tooltip.params.id,
          hasRemainingModals: stateStore.hasOpenModals
        )
      },
      getViewById: { [weak self] in
        self?.viewsById[$0]?.view
      }
    )
    weakView = view

    if case let .discard(next) = stateStore.markVisible(key, view: view) {
      return discard(next)
    }

    presenter.present(view, for: tooltip)
    view.animateAppear()
    UIAccessibility.postDelayed(notification: .screenChanged, argument: view)

    let duration = tooltip.params.duration
    if !duration.isZero {
      Task { @MainActor [weak self, weak view] in
        do {
          try await Task.sleep(nanoseconds: UInt64(duration.nanoseconds))
        } catch {
          return
        }
        guard let self, let view else { return }
        stateStore.remove(key, ifShowing: view)?.close(animated: true)
      }
    }
    return (true, nil)
  }
}

extension TooltipAnchorView {
  typealias Tooltip = DefaultTooltipManager.Tooltip

  fileprivate func firstMatchingTooltip(id: String, scopePath: UIElementPath?) -> BlockTooltip? {
    if let scopePath {
      guard path?.starts(with: scopePath) == true else { return nil }
    }
    return tooltips.first(where: { $0.id == id })
  }

  @MainActor
  fileprivate func makeTooltip(
    id: String,
    scopePath: UIElementPath?,
    in constraint: CGRect,
    relativeTo containerView: UIView? = nil,
    onError: ((String) -> Void)? = nil
  ) async -> Tooltip? {
    guard let tooltip = firstMatchingTooltip(id: id, scopePath: scopePath) else {
      return nil
    }

    let tooltipView: VisibleBoundsTrackingView
    let contentSize: CGSize

    switch tooltip.viewSource {
    case let .block(block):
      tooltipView = block.makeBlockView()
      contentSize = block.tooltipContentSize(
        constrainedBy: constraint.size,
        useLegacyWidth: tooltip.useLegacyWidth
      )
    case let .factory(factory):
      let view = await factory()
      guard let sizeProvider = view as? TooltipContentSizeProviding else {
        (view as? TooltipContentClosing)?.tooltipDidClose()
        onError?(
          "Tooltip view does not implement TooltipContentSizeProviding (tooltip id: '\(tooltip.id)')"
        )
        return nil
      }
      guard let size = sizeProvider.tooltipContentSize(
        constrainedBy: constraint.size,
        useLegacyWidth: tooltip.useLegacyWidth
      ) else {
        (view as? TooltipContentClosing)?.tooltipDidClose()
        onError?("Tooltip content size is not available (tooltip id: '\(tooltip.id)')")
        return nil
      }
      tooltipView = view
      contentSize = size
    }

    let targetRect = window != nil ?
      convert(bounds, to: containerView) :
      frame

    tooltipView.frame = tooltip.calculateFrame(
      size: contentSize,
      targeting: targetRect,
      constrainedBy: constraint
    )

    let substrateView = await tooltip.substrateViewFactory?()

    return DefaultTooltipManager.Tooltip(
      params: tooltip.params,
      view: tooltipView,
      substrateView: substrateView,
      bringToTopId: tooltip.bringToTopId
    )
  }
}

extension UIAccessibility {
  fileprivate static func postDelayed(
    notification: UIAccessibility.Notification,
    argument: Any?
  ) {
    after(0.2) {
      UIAccessibility.post(
        notification: notification,
        argument: argument
      )
    }
  }
}
#else
public protocol TooltipManager: AnyObject, TooltipActionPerformer, RenderingDelegate {}

public final class DefaultTooltipManager: TooltipManager {
  public init() {}

  public func showTooltip(info _: TooltipInfo) {}
  @MainActor
  public func showTooltip(info _: TooltipInfo) async -> Bool { false }
  public func hideTooltip(identity _: TooltipIdentity) {}
}
#endif

extension TooltipManager {
  public func mapView(_: any BlockView, to _: BlockViewID) {}
  public func reset() {}
}

extension Block {
  fileprivate func tooltipContentSize(constrainedBy size: CGSize, useLegacyWidth: Bool) -> CGSize {
    useLegacyWidth ? intrinsicSize : self.size(forResizableBlockSize: size)
  }
}
