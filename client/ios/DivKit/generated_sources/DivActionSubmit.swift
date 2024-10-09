// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSubmit {
  public final class Request {
    @frozen
    public enum Method: String, CaseIterable {
      case gET = "GET"
      case pOST = "POST"
      case pUT = "PUT"
      case pATCH = "PATCH"
      case dELETE = "DELETE"
      case hEAD = "HEAD"
      case oPTIONS = "OPTIONS"
    }

    public final class Header {
      public let name: Expression<String>
      public let value: Expression<String>

      public func resolveName(_ resolver: ExpressionResolver) -> String? {
        resolver.resolveString(name)
      }

      public func resolveValue(_ resolver: ExpressionResolver) -> String? {
        resolver.resolveString(value)
      }

      init(
        name: Expression<String>,
        value: Expression<String>
      ) {
        self.name = name
        self.value = value
      }
    }

    public let headers: [Header]?
    public let method: Expression<Method> // default value: POST
    public let url: Expression<URL>

    public func resolveMethod(_ resolver: ExpressionResolver) -> Method {
      resolver.resolveEnum(method) ?? Method.pOST
    }

    public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
      resolver.resolveUrl(url)
    }

    init(
      headers: [Header]? = nil,
      method: Expression<Method>? = nil,
      url: Expression<URL>
    ) {
      self.headers = headers
      self.method = method ?? .value(.pOST)
      self.url = url
    }
  }

  public static let type: String = "submit"
  public let containerId: Expression<String>
  public let onFailActions: [DivAction]?
  public let onSuccessActions: [DivAction]?
  public let request: Request

  public func resolveContainerId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(containerId)
  }

  init(
    containerId: Expression<String>,
    onFailActions: [DivAction]? = nil,
    onSuccessActions: [DivAction]? = nil,
    request: Request
  ) {
    self.containerId = containerId
    self.onFailActions = onFailActions
    self.onSuccessActions = onSuccessActions
    self.request = request
  }
}

#if DEBUG
extension DivActionSubmit: Equatable {
  public static func ==(lhs: DivActionSubmit, rhs: DivActionSubmit) -> Bool {
    guard
      lhs.containerId == rhs.containerId,
      lhs.onFailActions == rhs.onFailActions,
      lhs.onSuccessActions == rhs.onSuccessActions
    else {
      return false
    }
    guard
      lhs.request == rhs.request
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionSubmit: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["container_id"] = containerId.toValidSerializationValue()
    result["on_fail_actions"] = onFailActions?.map { $0.toDictionary() }
    result["on_success_actions"] = onSuccessActions?.map { $0.toDictionary() }
    result["request"] = request.toDictionary()
    return result
  }
}

#if DEBUG
extension DivActionSubmit.Request.Header: Equatable {
  public static func ==(lhs: DivActionSubmit.Request.Header, rhs: DivActionSubmit.Request.Header) -> Bool {
    guard
      lhs.name == rhs.name,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

#if DEBUG
extension DivActionSubmit.Request: Equatable {
  public static func ==(lhs: DivActionSubmit.Request, rhs: DivActionSubmit.Request) -> Bool {
    guard
      lhs.headers == rhs.headers,
      lhs.method == rhs.method,
      lhs.url == rhs.url
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionSubmit.Request.Header: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["name"] = name.toValidSerializationValue()
    result["value"] = value.toValidSerializationValue()
    return result
  }
}

extension DivActionSubmit.Request: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["headers"] = headers?.map { $0.toDictionary() }
    result["method"] = method.toValidSerializationValue()
    result["url"] = url.toValidSerializationValue()
    return result
  }
}
