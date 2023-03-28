import Foundation

import CommonCorePublic

#if os(iOS)
public protocol RenderingDelegate: AnyObject {
  func mapView(_ view: BlockView, to id: BlockViewID)
}

public typealias BlockViewID = Tagged<BlockViewProtocol, String>

public protocol DivViewMetaProviding: AnyObject {
  func subview(with id: BlockViewID) -> BlockView?
}
#else
public protocol RenderingDelegate {}
public typealias BlockViewID = Tagged<AnyObject, String>
#endif
