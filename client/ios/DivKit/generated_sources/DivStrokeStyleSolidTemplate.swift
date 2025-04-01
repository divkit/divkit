// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivStrokeStyleSolidTemplate: TemplateValue, Sendable {
  public static let type: String = "solid"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivStrokeStyleSolidTemplate?) -> DeserializationResult<DivStrokeStyleSolid> {
    return .success(DivStrokeStyleSolid())
  }

  public static func resolveValue(context: TemplatesContext, parent: DivStrokeStyleSolidTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivStrokeStyleSolid> {
    return .success(DivStrokeStyleSolid())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivStrokeStyleSolidTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivStrokeStyleSolidTemplate {
    return self
  }
}
