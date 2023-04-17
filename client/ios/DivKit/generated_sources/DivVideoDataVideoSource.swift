// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVideoDataVideoSource {
  public final class Resolution {
    public static let type: String = "resolution"
    public let height: Expression<Int> // constraint: number > 0
    public let width: Expression<Int> // constraint: number > 0

    public func resolveHeight(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: height)
    }

    public func resolveWidth(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: width)
    }

    static let heightValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 > 0 })

    static let widthValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 > 0 })

    init(
      height: Expression<Int>,
      width: Expression<Int>
    ) {
      self.height = height
      self.width = width
    }
  }

  public static let type: String = "video_source"
  public let codec: Expression<String>?
  public let mimeType: Expression<String>?
  public let resolution: Resolution?
  public let url: Expression<URL>

  public func resolveCodec(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: codec, initializer: { $0 })
  }

  public func resolveMimeType(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: mimeType, initializer: { $0 })
  }

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: url, initializer: URL.init(string:))
  }

  static let codecValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let mimeTypeValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let resolutionValidator: AnyValueValidator<DivVideoDataVideoSource.Resolution> =
    makeNoOpValueValidator()

  init(
    codec: Expression<String>? = nil,
    mimeType: Expression<String>? = nil,
    resolution: Resolution? = nil,
    url: Expression<URL>
  ) {
    self.codec = codec
    self.mimeType = mimeType
    self.resolution = resolution
    self.url = url
  }
}

#if DEBUG
extension DivVideoDataVideoSource: Equatable {
  public static func ==(lhs: DivVideoDataVideoSource, rhs: DivVideoDataVideoSource) -> Bool {
    guard
      lhs.codec == rhs.codec,
      lhs.mimeType == rhs.mimeType,
      lhs.resolution == rhs.resolution
    else {
      return false
    }
    guard
      lhs.url == rhs.url
    else {
      return false
    }
    return true
  }
}
#endif

extension DivVideoDataVideoSource: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["codec"] = codec?.toValidSerializationValue()
    result["mime_type"] = mimeType?.toValidSerializationValue()
    result["resolution"] = resolution?.toDictionary()
    result["url"] = url.toValidSerializationValue()
    return result
  }
}

#if DEBUG
extension DivVideoDataVideoSource.Resolution: Equatable {
  public static func ==(lhs: DivVideoDataVideoSource.Resolution, rhs: DivVideoDataVideoSource.Resolution) -> Bool {
    guard
      lhs.height == rhs.height,
      lhs.width == rhs.width
    else {
      return false
    }
    return true
  }
}
#endif

extension DivVideoDataVideoSource.Resolution: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["height"] = height.toValidSerializationValue()
    result["width"] = width.toValidSerializationValue()
    return result
  }
}
