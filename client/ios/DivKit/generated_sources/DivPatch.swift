// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPatch: Sendable {
  @frozen
  public enum Mode: String, CaseIterable, Sendable {
    case transactional = "transactional"
    case partial = "partial"
  }

  public final class Change: Sendable {
    public let id: String
    public let items: [Div]?

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        id: try dictionary.getField("id", context: context),
        items: try dictionary.getOptionalArray("items", transform: { (dict: [String: Any]) in try? Div(dictionary: dict, context: context) })
      )
    }

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
  public let onAppliedActions: [DivAction]?
  public let onFailedActions: [DivAction]?

  public func resolveMode(_ resolver: ExpressionResolver) -> Mode {
    resolver.resolveEnum(mode) ?? Mode.partial
  }

  static let changesValidator: AnyArrayValueValidator<DivPatch.Change> =
    makeArrayValidator(minItems: 1)

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      changes: try dictionary.getArray("changes", transform: { (dict: [String: Any]) in try? DivPatch.Change(dictionary: dict, context: context) }, validator: Self.changesValidator, context: context),
      mode: try dictionary.getOptionalExpressionField("mode", context: context),
      onAppliedActions: try dictionary.getOptionalArray("on_applied_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      onFailedActions: try dictionary.getOptionalArray("on_failed_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) })
    )
  }

  init(
    changes: [Change],
    mode: Expression<Mode>? = nil,
    onAppliedActions: [DivAction]? = nil,
    onFailedActions: [DivAction]? = nil
  ) {
    self.changes = changes
    self.mode = mode ?? .value(.partial)
    self.onAppliedActions = onAppliedActions
    self.onFailedActions = onFailedActions
  }
}

#if DEBUG
extension DivPatch: Equatable {
  public static func ==(lhs: DivPatch, rhs: DivPatch) -> Bool {
    guard
      lhs.changes == rhs.changes,
      lhs.mode == rhs.mode,
      lhs.onAppliedActions == rhs.onAppliedActions
    else {
      return false
    }
    guard
      lhs.onFailedActions == rhs.onFailedActions
    else {
      return false
    }
    return true
  }
}
#endif

extension DivPatch: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["changes"] = changes.map { $0.toDictionary() }
    result["mode"] = mode.toValidSerializationValue()
    result["on_applied_actions"] = onAppliedActions?.map { $0.toDictionary() }
    result["on_failed_actions"] = onFailedActions?.map { $0.toDictionary() }
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["id"] = id
    result["items"] = items?.map { $0.toDictionary() }
    return result
  }
}
