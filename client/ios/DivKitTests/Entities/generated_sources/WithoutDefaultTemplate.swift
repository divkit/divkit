// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization

public final class WithoutDefaultTemplate: TemplateValue {
  public static let type: String = "non_default"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: WithoutDefaultTemplate?) -> DeserializationResult<WithoutDefault> {
    return .success(WithoutDefault())
  }

  public static func resolveValue(context: TemplatesContext, parent: WithoutDefaultTemplate?, useOnlyLinks: Bool) -> DeserializationResult<WithoutDefault> {
    return .success(WithoutDefault())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> WithoutDefaultTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> WithoutDefaultTemplate {
    return self
  }
}
