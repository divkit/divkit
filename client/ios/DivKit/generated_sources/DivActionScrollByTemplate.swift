// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionScrollByTemplate: TemplateValue {
  public typealias Overflow = DivActionScrollBy.Overflow

  public static let type: String = "scroll_by"
  public let parent: String?
  public let animated: Field<Expression<Bool>>? // default value: true
  public let id: Field<Expression<String>>?
  public let itemCount: Field<Expression<Int>>? // default value: 0
  public let offset: Field<Expression<Int>>? // default value: 0
  public let overflow: Field<Expression<Overflow>>? // default value: clamp

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      animated: dictionary.getOptionalExpressionField("animated"),
      id: dictionary.getOptionalExpressionField("id"),
      itemCount: dictionary.getOptionalExpressionField("item_count"),
      offset: dictionary.getOptionalExpressionField("offset"),
      overflow: dictionary.getOptionalExpressionField("overflow")
    )
  }

  init(
    parent: String?,
    animated: Field<Expression<Bool>>? = nil,
    id: Field<Expression<String>>? = nil,
    itemCount: Field<Expression<Int>>? = nil,
    offset: Field<Expression<Int>>? = nil,
    overflow: Field<Expression<Overflow>>? = nil
  ) {
    self.parent = parent
    self.animated = animated
    self.id = id
    self.itemCount = itemCount
    self.offset = offset
    self.overflow = overflow
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionScrollByTemplate?) -> DeserializationResult<DivActionScrollBy> {
    let animatedValue = parent?.animated?.resolveOptionalValue(context: context) ?? .noValue
    let idValue = parent?.id?.resolveValue(context: context) ?? .noValue
    let itemCountValue = parent?.itemCount?.resolveOptionalValue(context: context) ?? .noValue
    let offsetValue = parent?.offset?.resolveOptionalValue(context: context) ?? .noValue
    let overflowValue = parent?.overflow?.resolveOptionalValue(context: context) ?? .noValue
    var errors = mergeErrors(
      animatedValue.errorsOrWarnings?.map { .nestedObjectError(field: "animated", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      itemCountValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_count", error: $0) },
      offsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "offset", error: $0) },
      overflowValue.errorsOrWarnings?.map { .nestedObjectError(field: "overflow", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionScrollBy(
      animated: animatedValue.value,
      id: idNonNil,
      itemCount: itemCountValue.value,
      offset: offsetValue.value,
      overflow: overflowValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionScrollByTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionScrollBy> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var animatedValue: DeserializationResult<Expression<Bool>> = parent?.animated?.value() ?? .noValue
    var idValue: DeserializationResult<Expression<String>> = parent?.id?.value() ?? .noValue
    var itemCountValue: DeserializationResult<Expression<Int>> = parent?.itemCount?.value() ?? .noValue
    var offsetValue: DeserializationResult<Expression<Int>> = parent?.offset?.value() ?? .noValue
    var overflowValue: DeserializationResult<Expression<DivActionScrollBy.Overflow>> = parent?.overflow?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "animated":
        animatedValue = deserialize(__dictValue).merged(with: animatedValue)
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case "item_count":
        itemCountValue = deserialize(__dictValue).merged(with: itemCountValue)
      case "offset":
        offsetValue = deserialize(__dictValue).merged(with: offsetValue)
      case "overflow":
        overflowValue = deserialize(__dictValue).merged(with: overflowValue)
      case parent?.animated?.link:
        animatedValue = animatedValue.merged(with: { deserialize(__dictValue) })
      case parent?.id?.link:
        idValue = idValue.merged(with: { deserialize(__dictValue) })
      case parent?.itemCount?.link:
        itemCountValue = itemCountValue.merged(with: { deserialize(__dictValue) })
      case parent?.offset?.link:
        offsetValue = offsetValue.merged(with: { deserialize(__dictValue) })
      case parent?.overflow?.link:
        overflowValue = overflowValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    var errors = mergeErrors(
      animatedValue.errorsOrWarnings?.map { .nestedObjectError(field: "animated", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      itemCountValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_count", error: $0) },
      offsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "offset", error: $0) },
      overflowValue.errorsOrWarnings?.map { .nestedObjectError(field: "overflow", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionScrollBy(
      animated: animatedValue.value,
      id: idNonNil,
      itemCount: itemCountValue.value,
      offset: offsetValue.value,
      overflow: overflowValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionScrollByTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionScrollByTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionScrollByTemplate(
      parent: nil,
      animated: animated ?? mergedParent.animated,
      id: id ?? mergedParent.id,
      itemCount: itemCount ?? mergedParent.itemCount,
      offset: offset ?? mergedParent.offset,
      overflow: overflow ?? mergedParent.overflow
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionScrollByTemplate {
    return try mergedWithParent(templates: templates)
  }
}
