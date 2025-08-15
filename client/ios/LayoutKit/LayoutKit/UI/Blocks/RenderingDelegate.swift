import Foundation
import VGSL

#if os(iOS)
public protocol RenderingDelegate: AnyObject {
  func mapView(_ view: BlockView, to id: BlockViewID)
  func tooltipAnchorViewAdded(anchorView: TooltipAnchorView)
  func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView)
  func reportRenderingError(message: String, isWarning: Bool, path: UIElementPath)

  @_spi(Performance)
  func reportViewWasCreated()
  @_spi(Performance)
  func reportBlockWillConfigure(path: UIElementPath)
  @_spi(Performance)
  func reportBlockDidConfigure(path: UIElementPath)
  @_spi(Performance)
  func reportViewWillLayout(path: UIElementPath)
  @_spi(Performance)
  func reportViewDidLayout(path: UIElementPath)
}

public typealias BlockViewID = Tagged<BlockViewProtocol, String>

public protocol DivViewMetaProviding: AnyObject {
  func subview(with id: BlockViewID) -> BlockView?
}

extension RenderingDelegate {
  public func reportRenderingError(message _: String, isWarning _: Bool, path _: UIElementPath) {}
  public func reportViewWasCreated() {}
  public func reportBlockWillConfigure(path _: UIElementPath) {}
  public func reportBlockDidConfigure(path _: UIElementPath) {}
  public func reportViewWillLayout(path _: UIElementPath) {}
  public func reportViewDidLayout(path _: UIElementPath) {}
}
#else
public protocol RenderingDelegate {}
public typealias BlockViewID = Tagged<AnyObject, String>
#endif
