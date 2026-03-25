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
/// - `hideTooltip(id:)`: Use this method to hide the tooltip identified by the given `id`.
public protocol TooltipActionPerformer {
  /// Shows a tooltip with the provided `TooltipInfo`.
  ///
  /// - Parameter info: The `TooltipInfo` containing the necessary information to display the
  /// tooltip.
  func showTooltip(info: TooltipInfo)

  /// Hides the tooltip identified by the given `id`.
  ///
  /// - Parameter id: The identifier of the tooltip to hide.
  func hideTooltip(id: String)
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
  private var showingTooltips = [String: TooltipContainerView]()
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
    guard !showingTooltips.keys.contains(info.id) else { return }

    guard let (constraint, coordinateSpace) = presenter.prepare() else { return }

    Task { @MainActor in
      guard let tooltip = await existingAnchorViews.compactMap(
        concurrencyLimit: 1,
        transform: {
          await $0?.makeTooltip(id: info.id, in: constraint, relativeTo: coordinateSpace)
        }
      ).first else { return }

      let view = TooltipContainerView(
        tooltip: tooltip,
        handleAction: handleAction,
        onCloseAction: { [weak self] in
          guard let self else { return }
          showingTooltips.removeValue(forKey: tooltip.params.id)
          let hasRemainingModals = !showingTooltips.values.filter(\.isModal).isEmpty
          presenter.onClosed(tooltipID: tooltip.params.id, hasRemainingModals: hasRemainingModals)
        },
        getViewById: { [weak self] in
          self?.viewsById[$0]?.view
        }
      )

      presenter.present(view, for: tooltip)

      view.animateAppear()

      UIAccessibility.postDelayed(notification: .screenChanged, argument: view)

      showingTooltips[info.id] = view
      let duration = tooltip.params.duration
      if !duration.isZero {
        try await Task.sleep(nanoseconds: UInt64(duration.nanoseconds))
        hideTooltip(id: tooltip.params.id)
      }
    }
  }

  public func hideTooltip(id: String) {
    guard let tooltipView = showingTooltips[id] else { return }
    tooltipView.close(animated: true)
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
    showingTooltips.values.forEach { $0.close(animated: false) }
    showingTooltips = [:]
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
}

extension TooltipAnchorView {
  @MainActor
  fileprivate func makeTooltip(
    id: String,
    in constraint: CGRect,
    relativeTo containerView: UIView? = nil
  ) async -> DefaultTooltipManager.Tooltip? {
    guard let tooltip = tooltips.first(where: { $0.id == id }) else {
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
  public func hideTooltip(id _: String) {}
}
#endif

extension TooltipManager {
  public func mapView(_: any BlockView, to _: BlockViewID) {}
  public func reset() {}
}
