// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSetCursorPosition: Sendable {
  public final class Position: Sendable {
    public static let type: String = "absolute"
    public let end: Expression<Int>?
    public let start: Expression<Int>

    public func resolveEnd(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(end)
    }

    public func resolveStart(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(start)
    }

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        end: try dictionary.getOptionalExpressionField("end", context: context),
        start: try dictionary.getExpressionField("start", context: context)
      )
    }

    init(
      end: Expression<Int>? = nil,
      start: Expression<Int>
    ) {
      self.end = end
      self.start = start
    }
  }

  public static let type: String = "set_cursor_position"
  public let id: Expression<String>
  public let position: Position

  public func resolveId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(id)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      id: try dictionary.getExpressionField("id", context: context),
      position: try dictionary.getField("position", transform: { (dict: [String: Any]) in try DivActionSetCursorPosition.Position(dictionary: dict, context: context) }, context: context)
    )
  }

  init(
    id: Expression<String>,
    position: Position
  ) {
    self.id = id
    self.position = position
  }
}

#if DEBUG
extension DivActionSetCursorPosition: Equatable {
  public static func ==(lhs: DivActionSetCursorPosition, rhs: DivActionSetCursorPosition) -> Bool {
    guard
      lhs.id == rhs.id,
      lhs.position == rhs.position
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionSetCursorPosition: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["id"] = id.toValidSerializationValue()
    result["position"] = position.toDictionary()
    return result
  }
}

#if DEBUG
extension DivActionSetCursorPosition.Position: Equatable {
  public static func ==(lhs: DivActionSetCursorPosition.Position, rhs: DivActionSetCursorPosition.Position) -> Bool {
    guard
      lhs.end == rhs.end,
      lhs.start == rhs.start
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionSetCursorPosition.Position: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["end"] = end?.toValidSerializationValue()
    result["start"] = start.toValidSerializationValue()
    return result
  }
}
