import CommonCore
import Serialization

public protocol TemplateValue {
  associatedtype ResolvedValue
  func resolveParent(templates: Templates) throws -> Self
  static func resolveValue(context: Context, parent: Self?, useOnlyLinks: Bool)
    -> DeserializationResult<ResolvedValue>
}

extension TemplateValue {
  public func tryResolveParent(templates: Templates) -> Self? {
    try? resolveParent(templates: templates)
  }

  public func resolveValue(context: Context, useOnlyLinks: Bool)
    -> DeserializationResult<ResolvedValue> {
    Self.resolveValue(context: context, parent: self, useOnlyLinks: useOnlyLinks)
  }

  private static func fetchParent(from context: Context) -> Self? {
    let type = context.templateData["type"] as? String
    return type.flatMap { context.templates[$0] as? Self }
  }

  public static func resolveValue(context: Context, useOnlyLinks: Bool)
    -> DeserializationResult<ResolvedValue> {
    resolveValue(
      context: context,
      parent: fetchParent(from: context),
      useOnlyLinks: useOnlyLinks
    )
  }
}

extension Array where Element: TemplateValue {
  public typealias ResolvedValue = [Element.ResolvedValue]

  public func resolveParent(templates: Templates) throws -> [Element] {
    try resolveParent(templates: templates, validator: nil)
  }

  public func resolveParent(
    templates: Templates,
    validator: AnyArrayValueValidator<Element.ResolvedValue>?
  ) throws -> [Element] {
    let result: [Element] = try enumerated().compactMap {
      do {
        let result: Element = try $0.element.resolveParent(templates: templates)
        return result
      } catch {
        if validator?.isPartialDeserializationAllowed == false {
          throw error
        }
        return nil
      }
    }
    if count != result.count,
       validator?.isPartialDeserializationAllowed == false {
      throw DeserializationError.invalidValue(
        result: result,
        value: self
      )
    }
    return result
  }

  public func resolveValue(
    context: Context,
    validator: AnyArrayValueValidator<Element.ResolvedValue>?
  ) -> DeserializationResult<[Element.ResolvedValue]> {
    var result: [Element.ResolvedValue] = []
    var errors: [Either<DeserializationError, FieldError>] = []
    result.reserveCapacity(count)
    for index in indices {
      let itemResult = self[index].resolveValue(context: context, useOnlyLinks: true)
      if let resultValue = itemResult.value {
        result.append(resultValue)
      }
      errors.append(contentsOf: itemResult.errorsOrWarnings?.asArray() ?? [])
    }
    if result.count != count,
       validator?.isPartialDeserializationAllowed == false {
      return .failure(NonEmptyArray(.invalidValue(result: result, value: self)))
    }
    guard validator?.isValid(result) != false else {
      return .failure(NonEmptyArray(.invalidValue(result: result, value: self)))
    }
    return errors.isEmpty
      ? .success(result)
      : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }
}
