import VGSL

#if os(iOS)
import UIKit
#endif

/// The `TooltipActionPerformer` protocol defines actions for showing and hiding tooltips within the
/// DivKit.
///
/// Conforming to this protocol allows your class to perform actions related to tooltips. It
/// introduces two methods to control the display of tooltips:
/// - `showTooltip(info:)`: Use this method to show a tooltip with the provided `TooltipInfo`.
/// - `hideTooltip(info:)`: Use this method to hide the tooltip described by `TooltipInfo`.
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
  /// - Parameter info: The `TooltipInfo` containing the necessary information to display the
  /// tooltip.
  /// - Returns: `true` if the tooltip was shown, `false` otherwise.
  func showTooltipAsync(info: TooltipInfo) async -> Bool

  /// Hides the tooltip described by the given `TooltipInfo`.
  ///
  /// - Parameter identity: The `TooltipIdentity` containing the tooltip id and optional scope.
  func hideTooltip(identity: TooltipIdentity)
}

extension TooltipActionPerformer {
  public func showTooltipAsync(info: TooltipInfo) async -> Bool {
    showTooltip(info: info)
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

  class TooltipStates {
    enum State {
      case pending
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

    func tryReserve(_ identity: TooltipIdentity) -> Bool {
      lock.withLock {
        guard !tooltips.keys.contains(identity) else {
          return false
        }

        tooltips[identity] = .pending
        return true
      }
    }

    func isReserved(_ identity: TooltipIdentity) -> Bool {
      lock.withLock {
        tooltips.keys.contains(identity)
      }
    }

    func addVisible(_ identity: TooltipIdentity, view: TooltipContainerView) {
      lock.withLock {
        tooltips[identity] = .visible(view)
      }
    }

    func removePending(_ identity: TooltipIdentity) {
      lock.withLock {
        if case .pending = tooltips[identity] {
          tooltips[identity] = nil
        }
      }
    }

    @discardableResult
    func remove(
      _ identity: TooltipIdentity
    ) -> TooltipContainerView? {
      lock.withLock {
        defer { tooltips[identity] = nil }
        return if case let .visible(view) = tooltips[identity] {
          view
        } else {
          nil
        }
      }
    }

    func reset() {
      let viewsToClose: [TooltipContainerView] = lock.withLock {
        let views = tooltips.values.reduce(into: [TooltipContainerView]()) {
          if case let .visible(view) = $1 {
            $0.append(view)
          }
        }
        tooltips.removeAll()
        return views
      }

      viewsToClose.forEach { $0.close(animated: false) }
    }
  }

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

  public init(
    shownTooltips: Property<Set<String>> = Property(),
    handleAction: @escaping (UIActionEvent) -> Void = { _ in },
    externalView: TooltipHostView? = nil
  ) {
    self.presenter = externalView.map { ViewTooltipPresenter(containerView: $0) }
      ?? WindowTooltipPresenter()
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
  }

  public func showTooltip(info: TooltipInfo) {
    guard stateStore.tryReserve(info.identity) else {
      return
    }
    guard let anchorView = findAnchorView(for: info),
          let prep = presenter.prepare() else {
      stateStore.removePending(info.identity)
      return
    }
    Task {
      @MainActor in _ = await performShow(
        info: info,
        anchorView: anchorView,
        prep: prep
      )
    }
  }

  public func showTooltipAsync(info: TooltipInfo) async -> Bool {
    guard stateStore.tryReserve(info.identity) else { return false }

    guard let anchorView = findAnchorView(for: info),
          let prep = presenter.prepare() else {
      stateStore.removePending(info.identity)
      return false
    }

    return await performShow(info: info, anchorView: anchorView, prep: prep)
  }

  public func hideTooltip(identity: TooltipIdentity) {
    stateStore.remove(identity)?.close(animated: true)
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

  @MainActor
  private func performShow(
    info: TooltipInfo,
    anchorView: TooltipAnchorView,
    prep: (constraint: CGRect, coordinateSpace: UIView?)
  ) async -> Bool {
    defer {
      stateStore.removePending(info.identity)
    }

    guard let tooltip = await anchorView.makeTooltip(
      id: info.id,
      scopePath: info.scopePath,
      in: prep.constraint,
      relativeTo: prep.coordinateSpace
    ) else { return false }
    return await displayTooltip(tooltip, info: info)
  }

  @MainActor
  private func displayTooltip(_ tooltip: Tooltip, info: TooltipInfo) async -> Bool {
    let key = info.identity

    guard stateStore.isReserved(key) else { return false }

    let view = TooltipContainerView(
      tooltip: tooltip,
      handleAction: handleAction,
      onCloseAction: { [weak self] in
        guard let self else { return }
        stateStore.remove(key)
        presenter.onClosed(
          tooltipID: tooltip.params.id,
          hasRemainingModals: stateStore.hasOpenModals
        )
      },
      getViewById: { [weak self] in
        self?.viewsById[$0]?.view
      }
    )

    presenter.present(view, for: tooltip)
    view.animateAppear()
    UIAccessibility.postDelayed(notification: .screenChanged, argument: view)
    stateStore.addVisible(key, view: view)

    let duration = tooltip.params.duration
    if !duration.isZero {
      try? await Task.sleep(nanoseconds: UInt64(duration.nanoseconds))
      stateStore.remove(key)?.close(animated: true)
    }

    return true
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
    relativeTo containerView: UIView? = nil
  ) async -> Tooltip? {
    guard let tooltip = firstMatchingTooltip(id: id, scopePath: scopePath) else {
      return nil
    }

    let tooltipView = await tooltip.tooltipViewFactory?() ?? tooltip.block.makeBlockView()

    let targetRect = window != nil ?
      convert(bounds, to: containerView) :
      frame

    tooltipView.frame = tooltip.calculateFrame(
      targeting: targetRect,
      constrainedBy: constraint,
      useLegacyWidth: tooltip.useLegacyWidth
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
  public func showTooltipAsync(info _: TooltipInfo) async -> Bool { false }
  public func hideTooltip(identity _: TooltipIdentity) {}
}
#endif

extension TooltipManager {
  public func mapView(_: any BlockView, to _: BlockViewID) {}
  public func reset() {}
}
