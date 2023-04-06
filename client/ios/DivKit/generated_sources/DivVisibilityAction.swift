// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVisibilityAction {
  public let downloadCallbacks: DivDownloadCallbacks?
  public let logId: String // at least 1 char
  public let logLimit: Expression<Int> // constraint: number >= 0; default value: 1
  public let payload: [String: Any]?
  public let referer: Expression<URL>?
  public let url: Expression<URL>?
  public let visibilityDuration: Expression<Int> // constraint: number >= 0; default value: 800
  public let visibilityPercentage: Expression<Int> // constraint: number > 0 && number <= 100; default value: 50

  public func resolveLogLimit(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: logLimit) ?? 1
  }

  public func resolveReferer(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: referer, initializer: URL.init(string:))
  }

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: url, initializer: URL.init(string:))
  }

  public func resolveVisibilityDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: visibilityDuration) ?? 800
  }

  public func resolveVisibilityPercentage(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: visibilityPercentage) ?? 50
  }

  static let downloadCallbacksValidator: AnyValueValidator<DivDownloadCallbacks> =
    makeNoOpValueValidator()

  static let logIdValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let logLimitValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let payloadValidator: AnyValueValidator<[String: Any]> =
    makeNoOpValueValidator()

  static let refererValidator: AnyValueValidator<URL> =
    makeNoOpValueValidator()

  static let urlValidator: AnyValueValidator<URL> =
    makeNoOpValueValidator()

  static let visibilityDurationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let visibilityPercentageValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 && $0 <= 100 })

  init(
    downloadCallbacks: DivDownloadCallbacks? = nil,
    logId: String,
    logLimit: Expression<Int>? = nil,
    payload: [String: Any]? = nil,
    referer: Expression<URL>? = nil,
    url: Expression<URL>? = nil,
    visibilityDuration: Expression<Int>? = nil,
    visibilityPercentage: Expression<Int>? = nil
  ) {
    self.downloadCallbacks = downloadCallbacks
    self.logId = logId
    self.logLimit = logLimit ?? .value(1)
    self.payload = payload
    self.referer = referer
    self.url = url
    self.visibilityDuration = visibilityDuration ?? .value(800)
    self.visibilityPercentage = visibilityPercentage ?? .value(50)
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DivVisibilityAction: Equatable {
  public static func ==(lhs: DivVisibilityAction, rhs: DivVisibilityAction) -> Bool {
    guard
      lhs.downloadCallbacks == rhs.downloadCallbacks,
      lhs.logId == rhs.logId,
      lhs.logLimit == rhs.logLimit
    else {
      return false
    }
    guard
      lhs.referer == rhs.referer,
      lhs.url == rhs.url,
      lhs.visibilityDuration == rhs.visibilityDuration
    else {
      return false
    }
    guard
      lhs.visibilityPercentage == rhs.visibilityPercentage
    else {
      return false
    }
    return true
  }
}
#endif

extension DivVisibilityAction: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["download_callbacks"] = downloadCallbacks?.toDictionary()
    result["log_id"] = logId
    result["log_limit"] = logLimit.toValidSerializationValue()
    result["payload"] = payload
    result["referer"] = referer?.toValidSerializationValue()
    result["url"] = url?.toValidSerializationValue()
    result["visibility_duration"] = visibilityDuration.toValidSerializationValue()
    result["visibility_percentage"] = visibilityPercentage.toValidSerializationValue()
    return result
  }
}
