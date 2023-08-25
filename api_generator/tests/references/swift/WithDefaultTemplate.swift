// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class WithDefaultTemplate: TemplateValue {
  public static let type: String = "default"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: WithDefaultTemplate?) -> DeserializationResult<WithDefault> {
    return .success(WithDefault())
  }

  public static func resolveValue(context: TemplatesContext, parent: WithDefaultTemplate?, useOnlyLinks: Bool) -> DeserializationResult<WithDefault> {
    return .success(WithDefault())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> WithDefaultTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> WithDefaultTemplate {
    return self
  }
}
