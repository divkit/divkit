// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPageSize: Sendable {
  public static let type: String = "percentage"
  public let pageWidth: DivPercentageSize

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      pageWidth: try dictionary.getField("page_width", transform: { (dict: [String: Any]) in try DivPercentageSize(dictionary: dict, context: context) }, context: context)
    )
  }

  init(
    pageWidth: DivPercentageSize
  ) {
    self.pageWidth = pageWidth
  }
}

#if DEBUG
extension DivPageSize: Equatable {
  public static func ==(lhs: DivPageSize, rhs: DivPageSize) -> Bool {
    guard
      lhs.pageWidth == rhs.pageWidth
    else {
      return false
    }
    return true
  }
}
#endif

extension DivPageSize: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["page_width"] = pageWidth.toDictionary()
    return result
  }
}
