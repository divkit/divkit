// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivInfinityCountTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "infinity"
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

  private static func resolveOnlyLinks(context: Context, parent: DivInfinityCountTemplate?) -> DeserializationResult<DivInfinityCount> {
    return .success(DivInfinityCount())
  }

  public static func resolveValue(context: Context, parent: DivInfinityCountTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInfinityCount> {
    return .success(DivInfinityCount())
  }

  private func mergedWithParent(templates: Templates) throws -> DivInfinityCountTemplate {
    return self
  }

  public func resolveParent(templates: Templates) throws -> DivInfinityCountTemplate {
    return self
  }
}
