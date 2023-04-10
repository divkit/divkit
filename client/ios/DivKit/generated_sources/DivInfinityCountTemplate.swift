// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivInfinityCountTemplate: TemplateValue {
  public static let type: String = "infinity"
  public let parent: String? // at least 1 char

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator)
    )
  }

  init(
    parent: String?
  ) {
    self.parent = parent
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivInfinityCountTemplate?) -> DeserializationResult<DivInfinityCount> {
    return .success(DivInfinityCount())
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInfinityCountTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInfinityCount> {
    return .success(DivInfinityCount())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivInfinityCountTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInfinityCountTemplate {
    return self
  }
}
