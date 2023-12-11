// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithoutPropertiesTemplate: TemplateValue {
  public static let type: String = "entity_without_properties"
  public let parent: String?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type")
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

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithoutPropertiesTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithoutPropertiesTemplate {
    return self
  }
}
