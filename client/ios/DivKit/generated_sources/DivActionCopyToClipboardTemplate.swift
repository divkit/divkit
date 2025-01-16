// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionCopyToClipboardTemplate: TemplateValue, Sendable {
  public static let type: String = "copy_to_clipboard"
  public let parent: String?
  public let content: Field<DivActionCopyToClipboardContentTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      content: dictionary.getOptionalField("content", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    content: Field<DivActionCopyToClipboardContentTemplate>? = nil
  ) {
    self.parent = parent
    self.content = content
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionCopyToClipboardTemplate?) -> DeserializationResult<DivActionCopyToClipboard> {
    let contentValue = { parent?.content?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      contentValue.errorsOrWarnings?.map { .nestedObjectError(field: "content", error: $0) }
    )
    if case .noValue = contentValue {
      errors.append(.requiredFieldIsMissing(field: "content"))
    }
    guard
      let contentNonNil = contentValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionCopyToClipboard(
      content: { contentNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionCopyToClipboardTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionCopyToClipboard> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var contentValue: DeserializationResult<DivActionCopyToClipboardContent> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "content" {
           contentValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionCopyToClipboardContentTemplate.self).merged(with: contentValue)
          }
        }()
        _ = {
         if key == parent?.content?.link {
           contentValue = contentValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionCopyToClipboardContentTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { contentValue = contentValue.merged(with: { parent.content?.resolveValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      contentValue.errorsOrWarnings?.map { .nestedObjectError(field: "content", error: $0) }
    )
    if case .noValue = contentValue {
      errors.append(.requiredFieldIsMissing(field: "content"))
    }
    guard
      let contentNonNil = contentValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionCopyToClipboard(
      content: { contentNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionCopyToClipboardTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionCopyToClipboardTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionCopyToClipboardTemplate(
      parent: nil,
      content: content ?? mergedParent.content
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionCopyToClipboardTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionCopyToClipboardTemplate(
      parent: nil,
      content: try merged.content?.resolveParent(templates: templates)
    )
  }
}
