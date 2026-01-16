// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionCustomTemplate: TemplateValue, Sendable {
  public static let type: String = "custom"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionCustomTemplate?) -> DeserializationResult<DivActionCustom> {
    return .success(DivActionCustom())
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionCustomTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionCustom> {
    return .success(DivActionCustom())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionCustomTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionCustomTemplate {
    return self
  }
}
