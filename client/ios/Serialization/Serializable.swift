import CoreGraphics
import Foundation

import VGSL

public protocol ValidSerializationValue {}

extension Int: ValidSerializationValue {}
extension Milliseconds: ValidSerializationValue {}
extension UInt: ValidSerializationValue {}
extension UInt32: ValidSerializationValue {}
extension Float: ValidSerializationValue {}
extension String: ValidSerializationValue {}
extension Array: ValidSerializationValue {}
extension Dictionary: ValidSerializationValue {}
extension CGFloat: ValidSerializationValue {}
extension Double: ValidSerializationValue {}
extension Bool: ValidSerializationValue {}

extension NSNumber: ValidSerializationValue {}
extension NSString: ValidSerializationValue {}
extension NSDictionary: ValidSerializationValue {}
extension NSArray: ValidSerializationValue {}

public protocol Serializable {
  func toDictionary() -> [String: ValidSerializationValue]
}
