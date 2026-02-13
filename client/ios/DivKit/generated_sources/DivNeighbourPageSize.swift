// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivNeighbourPageSize: Sendable {
  public static let type: String = "fixed"
  public let neighbourPageWidth: DivFixedSize

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      neighbourPageWidth: try dictionary.getField("neighbour_page_width", transform: { (dict: [String: Any]) in try DivFixedSize(dictionary: dict, context: context) })
    )
  }

  init(
    neighbourPageWidth: DivFixedSize
  ) {
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["neighbour_page_width"] = neighbourPageWidth.toDictionary()
    return result
  }
}
