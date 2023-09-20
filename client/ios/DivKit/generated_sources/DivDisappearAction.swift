// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivDisappearAction: DivSightAction {
  public let disappearDuration: Expression<Int> // constraint: number >= 0; default value: 800
  public let downloadCallbacks: DivDownloadCallbacks?
  public let logId: String // at least 1 char
  public let logLimit: Expression<Int> // constraint: number >= 0; default value: 1
  public let payload: [String: Any]?
  public let referer: Expression<URL>?
  public let typed: DivActionTyped?
  public let url: Expression<URL>?
  public let visibilityPercentage: Expression<Int> // constraint: number >= 0 && number < 100; default value: 0

  public func resolveDisappearDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: disappearDuration) ?? 800
  }

  public func resolveLogLimit(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: logLimit) ?? 1
  }

  public func resolveReferer(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: referer, initializer: URL.init(string:))
  }

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: url, initializer: URL.init(string:))
  }

  public func resolveVisibilityPercentage(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: visibilityPercentage) ?? 0
  }

  static let disappearDurationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

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

  static let typedValidator: AnyValueValidator<DivActionTyped> =
    makeNoOpValueValidator()

  static let urlValidator: AnyValueValidator<URL> =
    makeNoOpValueValidator()

  static let visibilityPercentageValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 && $0 < 100 })

  init(
    disappearDuration: Expression<Int>? = nil,
    downloadCallbacks: DivDownloadCallbacks? = nil,
    logId: String,
    logLimit: Expression<Int>? = nil,
    payload: [String: Any]? = nil,
    referer: Expression<URL>? = nil,
    typed: DivActionTyped? = nil,
    url: Expression<URL>? = nil,
    visibilityPercentage: Expression<Int>? = nil
  ) {
    self.disappearDuration = disappearDuration ?? .value(800)
    self.downloadCallbacks = downloadCallbacks
    self.logId = logId
    self.logLimit = logLimit ?? .value(1)
    self.payload = payload
    self.referer = referer
    self.typed = typed
    self.url = url
    self.visibilityPercentage = visibilityPercentage ?? .value(0)
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DivDisappearAction: Equatable {
  public static func ==(lhs: DivDisappearAction, rhs: DivDisappearAction) -> Bool {
    guard
      lhs.disappearDuration == rhs.disappearDuration,
      lhs.downloadCallbacks == rhs.downloadCallbacks,
      lhs.logId == rhs.logId
    else {
      return false
    }
    guard
      lhs.logLimit == rhs.logLimit,
      lhs.referer == rhs.referer,
      lhs.typed == rhs.typed
    else {
      return false
    }
    guard
      lhs.url == rhs.url,
      lhs.visibilityPercentage == rhs.visibilityPercentage
    else {
      return false
    }
    return true
  }
}
#endif

extension DivDisappearAction: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["disappear_duration"] = disappearDuration.toValidSerializationValue()
    result["download_callbacks"] = downloadCallbacks?.toDictionary()
    result["log_id"] = logId
    result["log_limit"] = logLimit.toValidSerializationValue()
    result["payload"] = payload
    result["referer"] = referer?.toValidSerializationValue()
    result["typed"] = typed?.toDictionary()
    result["url"] = url?.toValidSerializationValue()
    result["visibility_percentage"] = visibilityPercentage.toValidSerializationValue()
    return result
  }
}
