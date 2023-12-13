// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVideoSource {
  public final class Resolution {
    public static let type: String = "resolution"
    public let height: Expression<Int> // constraint: number > 0
    public let width: Expression<Int> // constraint: number > 0

    public func resolveHeight(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(height)
    }

    public func resolveWidth(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(width)
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
  public let bitrate: Expression<Int>?
  public let mimeType: Expression<String>
  public let resolution: Resolution?
  public let url: Expression<URL>

  public func resolveBitrate(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(bitrate)
  }

  public func resolveMimeType(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(mimeType, initializer: { $0 })
  }

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(url)
  }

  init(
    bitrate: Expression<Int>? = nil,
    mimeType: Expression<String>,
    resolution: Resolution? = nil,
    url: Expression<URL>
  ) {
    self.bitrate = bitrate
    self.mimeType = mimeType
    self.resolution = resolution
    self.url = url
  }
}

#if DEBUG
extension DivVideoSource: Equatable {
  public static func ==(lhs: DivVideoSource, rhs: DivVideoSource) -> Bool {
    guard
      lhs.bitrate == rhs.bitrate,
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

extension DivVideoSource: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["bitrate"] = bitrate?.toValidSerializationValue()
    result["mime_type"] = mimeType.toValidSerializationValue()
    result["resolution"] = resolution?.toDictionary()
    result["url"] = url.toValidSerializationValue()
    return result
  }
}

#if DEBUG
extension DivVideoSource.Resolution: Equatable {
  public static func ==(lhs: DivVideoSource.Resolution, rhs: DivVideoSource.Resolution) -> Bool {
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

extension DivVideoSource.Resolution: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["height"] = height.toValidSerializationValue()
    result["width"] = width.toValidSerializationValue()
    return result
  }
}
