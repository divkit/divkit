import Serialization

@available(*, deprecated, message: "Use DivTemplates.parseValue(type:from:) with the model type")
public protocol TemplateValue {
  associatedtype ResolvedValue: ContextDeserializable
}

@available(*, deprecated, renamed: "DivAction")
public enum DivActionTemplate: TemplateValue {
  public typealias ResolvedValue = DivAction
}

@available(*, deprecated, renamed: "DivVariable")
public enum DivVariableTemplate: TemplateValue {
  public typealias ResolvedValue = DivVariable
}

@available(*, deprecated, message: "DivTemplates.templates holds [String: Any]")
public final class DivTextTemplate: TemplateValue, Sendable {
  public typealias ResolvedValue = DivText

  public let text: Field<Expression<String>>?

  private init() {
    text = nil
  }
}

@available(*, deprecated, message: "Typed templates were removed")
@frozen
public indirect enum Field<T: Sendable>: Sendable {
  case value(T)
  case link(String, fallback: T?)
}
