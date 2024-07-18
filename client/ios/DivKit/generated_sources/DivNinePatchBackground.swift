// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivNinePatchBackground {
  public static let type: String = "nine_patch_image"
  public let imageUrl: Expression<URL>
  public let insets: DivAbsoluteEdgeInsets

  public func resolveImageUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(imageUrl)
  }

  init(
    imageUrl: Expression<URL>,
    insets: DivAbsoluteEdgeInsets
  ) {
    self.imageUrl = imageUrl
    self.insets = insets
  }
}

#if DEBUG
extension DivNinePatchBackground: Equatable {
  public static func ==(lhs: DivNinePatchBackground, rhs: DivNinePatchBackground) -> Bool {
    guard
      lhs.imageUrl == rhs.imageUrl,
      lhs.insets == rhs.insets
    else {
      return false
    }
    return true
  }
}
#endif

extension DivNinePatchBackground: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["image_url"] = imageUrl.toValidSerializationValue()
    result["insets"] = insets.toDictionary()
    return result
  }
}
