// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivNeighbourPageSize {
  public static let type: String = "fixed"
  public let neighbourPageWidth: DivFixedSize

  init(neighbourPageWidth: DivFixedSize) {
    self.neighbourPageWidth = neighbourPageWidth
  }
}

#if DEBUG
extension DivNeighbourPageSize: Equatable {
  public static func ==(lhs: DivNeighbourPageSize, rhs: DivNeighbourPageSize) -> Bool {
    guard
      lhs.neighbourPageWidth == rhs.neighbourPageWidth
    else {
      return false
    }
    return true
  }
}
#endif

extension DivNeighbourPageSize: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["neighbour_page_width"] = neighbourPageWidth.toDictionary()
    return result
  }
}
