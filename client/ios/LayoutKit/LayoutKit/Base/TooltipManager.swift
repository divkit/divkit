import BasePublic
import CommonCorePublic
import UIKit

public protocol TooltipManager: AnyObject, TooltipActionPerformer, RenderingDelegate {
  func tooltipAnchorViewAdded(anchorView: TooltipAnchorView)
  func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView)
}

public protocol TooltipActionPerformer {
  func showTooltip(info: TooltipInfo)
  func hideTooltip(id: String)
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

  public var shownDivTooltips: BasePublic.Property<Set<String>>

  private let handleAction: (UIActionEvent) -> Void
  private var existingAnchorViews = WeakCollection<TooltipAnchorView>()
  private var showingTooltips = [String: TooltipContainerView]()

  public init(
    shownDivTooltips: BasePublic.Property<Set<String>>,
    handleAction: @escaping (UIActionEvent) -> Void
  ) {
    self.handleAction = handleAction
    self.shownDivTooltips = shownDivTooltips
  }

  public func showTooltip(info: TooltipInfo) {
    if !info.multiple, !shownDivTooltips.value.insert(info.id).inserted { return }
    guard !showingTooltips.keys.contains(info.id),
          let tooltip = existingAnchorViews.compactMap({ $0?.makeTooltip(id: info.id) }).first
    else { return }

    let window: UIWindow?
    if #available(iOS 13.0, *) {
      window = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?.windows.first
    } else {
      window = UIApplication.shared.windows.first
    }

    let view = TooltipContainerView(
      tooltipView: tooltip.view,
      tooltipID: tooltip.id,
      handleAction: handleAction,
      onCloseAction: { [weak self] in self?.showingTooltips.removeValue(forKey: tooltip.id) }
    )
    window?.addSubview(view)
    view.frame = window?.bounds ?? .zero
    showingTooltips[info.id] = view
    if !tooltip.duration.value.isZero {
      after(tooltip.duration.value, block: { self.hideTooltip(id: tooltip.id) })
    }
  }

  public func hideTooltip(id: String) {
    guard let tooltipView = showingTooltips[id] else { return }
    tooltipView.close()
  }

  public func tooltipAnchorViewAdded(anchorView: TooltipAnchorView) {
    existingAnchorViews.append(anchorView)
  }

  public func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView) {
    existingAnchorViews.remove(anchorView)
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
            let tooltipView = tooltip.block.makeBlockView()
            let frame = tooltip.calculateFrame(targeting: bounds)
            tooltipView.frame = convert(frame, to: nil)
            return tooltipView
          }()
        )
      }
  }
}
