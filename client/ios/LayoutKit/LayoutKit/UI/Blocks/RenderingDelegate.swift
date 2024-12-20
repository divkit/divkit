import Foundation
import VGSL

#if os(iOS)
public protocol RenderingDelegate: AnyObject {
  func mapView(_ view: BlockView, to id: BlockViewID)
  func tooltipAnchorViewAdded(anchorView: TooltipAnchorView)
  func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView)
  func reportRenderingError(message: String, isWarning: Bool, path: UIElementPath)
}

public typealias BlockViewID = Tagged<BlockViewProtocol, String>

public protocol DivViewMetaProviding: AnyObject {
  func subview(with id: BlockViewID) -> BlockView?
}

public protocol TooltipAnchorView: ViewType {
  var tooltips: [BlockTooltip] { get }
}

extension RenderingDelegate {
  public func reportRenderingError(message _: String, isWarning _: Bool, path _: UIElementPath) {}
}
#else
public protocol RenderingDelegate {}
public typealias BlockViewID = Tagged<AnyObject, String>
#endif
