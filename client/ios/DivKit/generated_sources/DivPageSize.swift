// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivPageSize {
  public static let type: String = "percentage"
  public let pageWidth: DivPercentageSize

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
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["page_width"] = pageWidth.toDictionary()
    return result
  }
}
