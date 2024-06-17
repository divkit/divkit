// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivCollectionItemBuilder {
  public final class Prototype {
    public let div: Div
    public let id: Expression<String>?
    public let selector: Expression<Bool> // default value: true

    public func resolveId(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(id)
    }

    public func resolveSelector(_ resolver: ExpressionResolver) -> Bool {
      resolver.resolveNumeric(selector) ?? true
    }

    init(
      div: Div,
      id: Expression<String>? = nil,
      selector: Expression<Bool>? = nil
    ) {
      self.div = div
      self.id = id
      self.selector = selector ?? .value(true)
    }
  }

  public let data: Expression<[Any]>
  public let dataElementName: String // default value: it
  public let prototypes: [Prototype] // at least 1 elements

  public func resolveData(_ resolver: ExpressionResolver) -> [Any]? {
    resolver.resolveArray(data)
  }

  static let prototypesValidator: AnyArrayValueValidator<DivCollectionItemBuilder.Prototype> =
    makeArrayValidator(minItems: 1)

  init(
    data: Expression<[Any]>,
    dataElementName: String? = nil,
    prototypes: [Prototype]
  ) {
    self.data = data
    self.dataElementName = dataElementName ?? "it"
    self.prototypes = prototypes
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DivCollectionItemBuilder: Equatable {
  public static func ==(lhs: DivCollectionItemBuilder, rhs: DivCollectionItemBuilder) -> Bool {
    guard
      lhs.dataElementName == rhs.dataElementName,
      lhs.prototypes == rhs.prototypes
    else {
      return false
    }
    return true
  }
}
#endif

extension DivCollectionItemBuilder: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["data"] = data.toValidSerializationValue()
    result["data_element_name"] = dataElementName
    result["prototypes"] = prototypes.map { $0.toDictionary() }
    return result
  }
}

#if DEBUG
extension DivCollectionItemBuilder.Prototype: Equatable {
  public static func ==(lhs: DivCollectionItemBuilder.Prototype, rhs: DivCollectionItemBuilder.Prototype) -> Bool {
    guard
      lhs.div == rhs.div,
      lhs.id == rhs.id,
      lhs.selector == rhs.selector
    else {
      return false
    }
    return true
  }
}
#endif

extension DivCollectionItemBuilder.Prototype: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["div"] = div.toDictionary()
    result["id"] = id?.toValidSerializationValue()
    result["selector"] = selector.toValidSerializationValue()
    return result
  }
}
