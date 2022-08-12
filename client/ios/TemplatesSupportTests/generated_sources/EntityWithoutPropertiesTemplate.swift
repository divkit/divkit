// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithoutPropertiesTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "entity_without_properties"
  public let parent: String? // at least 1 char

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType _: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator)
    )
  }

  init(parent: String?) {
    self.parent = parent
  }

  private static func resolveOnlyLinks(
    context _: Context,
    parent _: EntityWithoutPropertiesTemplate?
  ) -> DeserializationResult<EntityWithoutProperties> {
    .success(EntityWithoutProperties())
  }

  public static func resolveValue(
    context _: Context,
    parent _: EntityWithoutPropertiesTemplate?,
    useOnlyLinks _: Bool
  ) -> DeserializationResult<EntityWithoutProperties> {
    .success(EntityWithoutProperties())
  }

  private func mergedWithParent(templates _: Templates) throws -> EntityWithoutPropertiesTemplate {
    self
  }

  public func resolveParent(templates _: Templates) throws -> EntityWithoutPropertiesTemplate {
    self
  }
}
