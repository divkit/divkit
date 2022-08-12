// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivInfinityCountTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "infinity"
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
    parent _: DivInfinityCountTemplate?
  ) -> DeserializationResult<DivInfinityCount> {
    .success(DivInfinityCount())
  }

  public static func resolveValue(
    context _: Context,
    parent _: DivInfinityCountTemplate?,
    useOnlyLinks _: Bool
  ) -> DeserializationResult<DivInfinityCount> {
    .success(DivInfinityCount())
  }

  private func mergedWithParent(templates _: Templates) throws -> DivInfinityCountTemplate {
    self
  }

  public func resolveParent(templates _: Templates) throws -> DivInfinityCountTemplate {
    self
  }
}
