// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public protocol DivSightAction {
  var downloadCallbacks: DivDownloadCallbacks? { get }
  var isEnabled: Expression<Bool> { get } // default value: true
  var logId: Expression<String> { get }
  var logLimit: Expression<Int> { get } // constraint: number >= 0; default value: 1
  var payload: [String: Any]? { get }
  var referer: Expression<URL>? { get }
  var typed: DivActionTyped? { get }
  var url: Expression<URL>? { get }
  func resolveIsEnabled(_ resolver: ExpressionResolver) -> Bool
  func resolveLogId(_ resolver: ExpressionResolver) -> String?
  func resolveLogLimit(_ resolver: ExpressionResolver) -> Int
  func resolveReferer(_ resolver: ExpressionResolver) -> URL?
  func resolveUrl(_ resolver: ExpressionResolver) -> URL?
}
