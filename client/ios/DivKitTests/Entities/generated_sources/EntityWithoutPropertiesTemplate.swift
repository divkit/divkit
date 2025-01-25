// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class EntityWithoutPropertiesTemplate: TemplateValue {
  public static let type: String = "entity_without_properties"
  public let parent: String?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String
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
