// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivDisappearAction: DivSightAction {
  public let disappearDuration: Expression<Int> // constraint: number >= 0; default value: 800
  public let downloadCallbacks: DivDownloadCallbacks?
  public let isEnabled: Expression<Bool> // default value: true
  public let logId: Expression<String>
  public let logLimit: Expression<Int> // constraint: number >= 0; default value: 1
  public let payload: [String: Any]?
  public let referer: Expression<URL>?
  public let typed: DivActionTyped?
  public let url: Expression<URL>?
  public let visibilityPercentage: Expression<Int> // constraint: number >= 0 && number < 100; default value: 0

  public func resolveDisappearDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(disappearDuration) ?? 800
  }

  public func resolveIsEnabled(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(isEnabled) ?? true
  }

  public func resolveLogId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(logId)
  }

  public func resolveLogLimit(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(logLimit) ?? 1
  }

  public func resolveReferer(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(referer)
  }

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(url)
  }

  public func resolveVisibilityPercentage(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(visibilityPercentage) ?? 0
  }

  static let disappearDurationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let logLimitValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let visibilityPercentageValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 && $0 < 100 })

  init(
    disappearDuration: Expression<Int>? = nil,
    downloadCallbacks: DivDownloadCallbacks? = nil,
    isEnabled: Expression<Bool>? = nil,
    logId: Expression<String>,
    logLimit: Expression<Int>? = nil,
    payload: [String: Any]? = nil,
    referer: Expression<URL>? = nil,
    typed: DivActionTyped? = nil,
    url: Expression<URL>? = nil,
    visibilityPercentage: Expression<Int>? = nil
  ) {
    self.disappearDuration = disappearDuration ?? .value(800)
    self.downloadCallbacks = downloadCallbacks
    self.isEnabled = isEnabled ?? .value(true)
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
      lhs.isEnabled == rhs.isEnabled
    else {
      return false
    }
    guard
      lhs.logId == rhs.logId,
      lhs.logLimit == rhs.logLimit,
      lhs.referer == rhs.referer
    else {
      return false
    }
    guard
      lhs.typed == rhs.typed,
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
    result["is_enabled"] = isEnabled.toValidSerializationValue()
    result["log_id"] = logId.toValidSerializationValue()
    result["log_limit"] = logLimit.toValidSerializationValue()
    result["payload"] = payload
    result["referer"] = referer?.toValidSerializationValue()
    result["typed"] = typed?.toDictionary()
    result["url"] = url?.toValidSerializationValue()
    result["visibility_percentage"] = visibilityPercentage.toValidSerializationValue()
    return result
  }
}
