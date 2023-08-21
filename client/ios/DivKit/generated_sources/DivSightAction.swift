// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public protocol DivSightAction {
  var downloadCallbacks: DivDownloadCallbacks? { get }
  var logId: String { get } // at least 1 char
  var logLimit: Expression<Int> { get } // constraint: number >= 0; default value: 1
  var payload: [String: Any]? { get }
  var referer: Expression<URL>? { get }
  var url: Expression<URL>? { get }
  func resolveLogLimit(_ resolver: ExpressionResolver) -> Int
  func resolveReferer(_ resolver: ExpressionResolver) -> URL?
  func resolveUrl(_ resolver: ExpressionResolver) -> URL?
}
