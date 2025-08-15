// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionClearFocusTemplate: TemplateValue, Sendable {
  public static let type: String = "clear_focus"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionClearFocusTemplate?) -> DeserializationResult<DivActionClearFocus> {
    return .success(DivActionClearFocus())
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionClearFocusTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionClearFocus> {
    return .success(DivActionClearFocus())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionClearFocusTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionClearFocusTemplate {
    return self
  }
}
