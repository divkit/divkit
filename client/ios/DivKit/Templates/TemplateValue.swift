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

  @usableFromInline
  func resolveParent(templates: [TemplateName: Any]) -> [Element] {
    let result: [Element] = enumerated().compactMap {
      try? $0.element.resolveParent(templates: templates)
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
      let itemErrors: [DeserializationError] = (itemResult.errorsOrWarnings?.asArray() ?? [])
        .map { .nestedObjectError(field: "\(index)", error: $0) }
      errors.append(contentsOf: itemErrors)
    }
    guard validator?.isValid(result) != false else {
      if let errors = NonEmptyArray(errors) {
        return .failure(NonEmptyArray(.composite(
          error: .invalidValue(result: result, from: self),
          causes: errors
        )))
      }
      return .failure(NonEmptyArray(.invalidValue(result: result, value: self)))
    }
    return errors.isEmpty
      ? .success(result)
      : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }
}
