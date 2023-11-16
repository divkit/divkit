#if os(iOS)
import UIKit
public typealias BlockView = BlockViewProtocol & UIView
#else
public typealias BlockView = AnyObject
#endif
