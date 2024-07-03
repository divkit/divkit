import BasePublic
import CommonCorePublic

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
}

extension TooltipManager {
  public func mapView(_: BlockView, to _: BlockViewID) {}
}

public final class DefaultTooltipManager: TooltipManager {
  public struct Tooltip {
    public let id: String
    public let duration: Duration
    public let view: VisibleBoundsTrackingView
  }

  public var shownTooltips: BasePublic.Property<Set<String>>

  private let handleAction: (UIActionEvent) -> Void
  private var existingAnchorViews = WeakCollection<TooltipAnchorView>()
  private var showingTooltips = [String: TooltipContainerView]()
  private var tooltipWindow: UIWindow?

  public init(
    shownTooltips: BasePublic.Property<Set<String>>,
    handleAction: @escaping (UIActionEvent) -> Void
  ) {
    self.handleAction = handleAction
    self.shownTooltips = shownTooltips
  }

  public func showTooltip(info: TooltipInfo) {
    if !info.multiple, !shownTooltips.value.insert(info.id).inserted { return }
    guard !showingTooltips.keys.contains(info.id),
          let tooltip = existingAnchorViews.compactMap({ $0?.makeTooltip(id: info.id) }).first
    else { return }

    setupTooltipWindow()

    let view = TooltipContainerView(
      tooltipView: tooltip.view,
      tooltipID: tooltip.id,
      handleAction: handleAction,
      onCloseAction: { [weak self] in 
        self?.showingTooltips.removeValue(forKey: tooltip.id)
        self?.tooltipWindow?.isHidden = true
      }
    )
    tooltipWindow?.addSubview(view)
    tooltipWindow?.isHidden = false
    tooltipWindow?.makeKeyAndVisible()
    view.frame = tooltipWindow?.bounds ?? .zero
    showingTooltips[info.id] = view
    if !tooltip.duration.value.isZero {
      after(tooltip.duration.value, block: { self.hideTooltip(id: tooltip.id) })
    }
  }

  public func hideTooltip(id: String) {
    guard let tooltipView = showingTooltips[id] else { return }
    tooltipView.close()
    tooltipWindow?.isHidden = true
  }

  public func tooltipAnchorViewAdded(anchorView: TooltipAnchorView) {
    existingAnchorViews.append(anchorView)
  }

  public func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView) {
    existingAnchorViews.remove(anchorView)
  }

  private func setupTooltipWindow() {
    if #available(iOS 13.0, *) {
      if tooltipWindow == nil {
        guard let windowScene = UIApplication.shared.connectedScenes
          .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene else { return }
        tooltipWindow = UIWindow(windowScene: windowScene)
        tooltipWindow?.windowLevel = UIWindow.Level.alert + 1
        tooltipWindow?.isHidden = true
      }
    } else {
      tooltipWindow = UIApplication.shared.windows.first
    }
  }
}

extension TooltipAnchorView {
  fileprivate func makeTooltip(id: String) -> DefaultTooltipManager.Tooltip? {
    tooltips
      .first { $0.id == id }
      .flatMap {
        let tooltip = $0
        return DefaultTooltipManager.Tooltip(
          id: tooltip.id,
          duration: tooltip.duration,
          view: {
            let frame = tooltip.calculateFrame(targeting: bounds)
            let tooltipView = tooltip.tooltipViewFactory?.value ?? tooltip.block.makeBlockView()
            tooltipView.frame = convert(frame, to: nil)
            return tooltipView
          }()
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
