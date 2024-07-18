// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInfinityCountTemplate: TemplateValue {
  public static let type: String = "infinity"
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
