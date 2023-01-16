#if os(iOS)
import UIKit
public typealias BlockView = UIView & BlockViewProtocol
#else
public typealias BlockView = AnyObject
#endif
