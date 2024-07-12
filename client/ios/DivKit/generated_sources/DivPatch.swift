// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPatch {
  @frozen
  public enum Mode: String, CaseIterable {
    case transactional = "transactional"
    case partial = "partial"
  }

  public final class Change {
    public let id: String
    public let items: [Div]?

    init(
      id: String,
      items: [Div]? = nil
    ) {
      self.id = id
      self.items = items
    }
  }

  public let changes: [Change] // at least 1 elements
  public let mode: Expression<Mode> // default value: partial

  public func resolveMode(_ resolver: ExpressionResolver) -> Mode {
    resolver.resolveEnum(mode) ?? Mode.partial
  }

  static let changesValidator: AnyArrayValueValidator<DivPatch.Change> =
    makeArrayValidator(minItems: 1)

  init(
    changes: [Change],
    mode: Expression<Mode>? = nil
  ) {
    self.changes = changes
    self.mode = mode ?? .value(.partial)
  }
}

#if DEBUG
extension DivPatch: Equatable {
  public static func ==(lhs: DivPatch, rhs: DivPatch) -> Bool {
    guard
      lhs.changes == rhs.changes,
      lhs.mode == rhs.mode
    else {
      return false
    }
    return true
  }
}
#endif

extension DivPatch: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["changes"] = changes.map { $0.toDictionary() }
    result["mode"] = mode.toValidSerializationValue()
    return result
  }
}

#if DEBUG
extension DivPatch.Change: Equatable {
  public static func ==(lhs: DivPatch.Change, rhs: DivPatch.Change) -> Bool {
    guard
      lhs.id == rhs.id,
      lhs.items == rhs.items
    else {
      return false
    }
    return true
  }
}
#endif

extension DivPatch.Change: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["id"] = id
    result["items"] = items?.map { $0.toDictionary() }
    return result
  }
}
