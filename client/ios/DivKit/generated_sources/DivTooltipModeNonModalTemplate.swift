// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTooltipModeNonModalTemplate: TemplateValue, Sendable {
  public static let type: String = "non_modal"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTooltipModeNonModalTemplate?) -> DeserializationResult<DivTooltipModeNonModal> {
    return .success(DivTooltipModeNonModal())
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTooltipModeNonModalTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTooltipModeNonModal> {
    return .success(DivTooltipModeNonModal())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTooltipModeNonModalTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTooltipModeNonModalTemplate {
    return self
  }
}
