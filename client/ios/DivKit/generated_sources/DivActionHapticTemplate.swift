// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionHapticTemplate: TemplateValue, Sendable {
  public typealias Feedback = DivActionHaptic.Feedback

  public static let type: String = "haptic"
  public let parent: String?
  public let feedback: Field<Expression<Feedback>>? // default value: light

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      feedback: dictionary.getOptionalExpressionField("feedback")
    )
  }

  init(
    parent: String?,
    feedback: Field<Expression<Feedback>>? = nil
  ) {
    self.parent = parent
    self.feedback = feedback
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionHapticTemplate?) -> DeserializationResult<DivActionHaptic> {
    let feedbackValue = { parent?.feedback?.resolveOptionalValue(context: context) ?? .noValue }()
    let errors = mergeErrors(
      feedbackValue.errorsOrWarnings?.map { .nestedObjectError(field: "feedback", error: $0) }
    )
    let result = DivActionHaptic(
      feedback: { feedbackValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionHapticTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionHaptic> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var feedbackValue: DeserializationResult<Expression<DivActionHaptic.Feedback>> = { parent?.feedback?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "feedback" {
           feedbackValue = deserialize(__dictValue).merged(with: feedbackValue)
          }
        }()
        _ = {
         if key == parent?.feedback?.link, context.templateData["feedback"] == nil {
           feedbackValue = deserialize(__dictValue).orFallback(feedbackValue)
          }
        }()
      }
    }()
    let errors = mergeErrors(
      feedbackValue.errorsOrWarnings?.map { .nestedObjectError(field: "feedback", error: $0) }
    )
    let result = DivActionHaptic(
      feedback: { feedbackValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionHapticTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionHapticTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionHapticTemplate(
      parent: nil,
      feedback: feedback ?? mergedParent.feedback
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionHapticTemplate {
    return try mergedWithParent(templates: templates)
  }
}
