import Foundation

import CommonCore

public typealias BlockViewID = Tagged<BlockViewProtocol, String>

public protocol RenderingDelegate: AnyObject {
  func mapView(_ view: BlockView, to id: BlockViewID)
}

public protocol DivViewMetaProviding: AnyObject {
  func subview(with id: BlockViewID) -> BlockView?
}
