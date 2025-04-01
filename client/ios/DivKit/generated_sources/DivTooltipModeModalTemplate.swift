// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTooltipModeModalTemplate: TemplateValue, Sendable {
  public static let type: String = "modal"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTooltipModeModalTemplate?) -> DeserializationResult<DivTooltipModeModal> {
    return .success(DivTooltipModeModal())
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTooltipModeModalTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTooltipModeModal> {
    return .success(DivTooltipModeModal())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTooltipModeModalTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTooltipModeModalTemplate {
    return self
  }
}
