import Foundation

import CommonCorePublic

#if os(iOS)
public protocol RenderingDelegate: AnyObject {
  func mapView(_ view: BlockView, to id: BlockViewID)
  func tooltipAnchorViewAdded(anchorView: TooltipAnchorView)
  func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView)
}

public typealias BlockViewID = Tagged<BlockViewProtocol, String>

public protocol DivViewMetaProviding: AnyObject {
  func subview(with id: BlockViewID) -> BlockView?
}

public protocol TooltipAnchorView: UIView {
  var tooltips: [BlockTooltip] { get }
}
#else
public protocol RenderingDelegate {}
public typealias BlockViewID = Tagged<AnyObject, String>
#endif
