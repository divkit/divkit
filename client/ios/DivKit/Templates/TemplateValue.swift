import CommonCorePublic
import Serialization

public protocol TemplateValue {
  associatedtype ResolvedValue

  init(
    dictionary: [String: Any],
    templateToType: [TemplateName: String]
  ) throws

  func resolveParent(templates: [TemplateName: Any]) throws -> Self

  static func resolveValue(
    context: TemplatesContext,
    parent: Self?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<ResolvedValue>
}

extension TemplateValue {
  @usableFromInline
  func tryResolveParent(templates: [TemplateName: Any]) -> Self? {
    try? resolveParent(templates: templates)
  }

  @usableFromInline
  func resolveValue(
    context: TemplatesContext,
    useOnlyLinks: Bool
  ) -> DeserializationResult<ResolvedValue> {
    Self.resolveValue(context: context, parent: self, useOnlyLinks: useOnlyLinks)
  }

  @usableFromInline
  static func resolveValue(
    context: TemplatesContext,
    useOnlyLinks: Bool
  ) -> DeserializationResult<ResolvedValue> {
    resolveValue(
      context: context,
      parent: fetchParent(context: context),
      useOnlyLinks: useOnlyLinks
    )
  }

  private static func fetchParent(context: TemplatesContext) -> Self? {
    let type = context.templateData["type"] as? String
    return type.flatMap { context.templates[$0] as? Self }
  }
}

extension Array where Element: TemplateValue {
  @usableFromInline
  typealias ResolvedValue = [Element.ResolvedValue]

  func resolveParent(templates: [TemplateName: Any]) throws -> [Element] {
    try resolveParent(templates: templates, validator: nil)
  }

  @usableFromInline
  func resolveParent(
    templates: [TemplateName: Any],
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

  @usableFromInline
  func resolveValue(
    context: TemplatesContext,
    validator: AnyArrayValueValidator<Element.ResolvedValue>?
  ) -> DeserializationResult<[Element.ResolvedValue]> {
    var result: [Element.ResolvedValue] = []
    var errors: [DeserializationError] = []
    result.reserveCapacity(count)
    for index in indices {
      let itemResult = self[index].resolveValue(context: context, useOnlyLinks: true)
      if let resultValue = itemResult.value {
        result.append(resultValue)
      }
      errors.append(contentsOf: (itemResult.errorsOrWarnings?.asArray() ?? [])
        .map { .nestedObjectError(field: "\(index)", error: $0) })
    }
    if result.count != count,
       validator?.isPartialDeserializationAllowed == false {
      return .failure(NonEmptyArray(.invalidValue(result: result, value: self), errors))
    }
    guard validator?.isValid(result) != false else {
      return .failure(NonEmptyArray(.invalidValue(result: result, value: self), errors))
    }
    return errors.isEmpty
      ? .success(result)
      : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }
}
