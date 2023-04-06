// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization

public final class EntityWithoutPropertiesTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "entity_without_properties"
  public let parent: String? // at least 1 char

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator)
    )
  }

  init(
    parent: String?
  ) {
    self.parent = parent
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithoutPropertiesTemplate?) -> DeserializationResult<EntityWithoutProperties> {
    return .success(EntityWithoutProperties())
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithoutPropertiesTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithoutProperties> {
    return .success(EntityWithoutProperties())
  }

  private func mergedWithParent(templates: Templates) throws -> EntityWithoutPropertiesTemplate {
    return self
  }

  public func resolveParent(templates: Templates) throws -> EntityWithoutPropertiesTemplate {
    return self
  }
}
