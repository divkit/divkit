// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivStrokeStyleDashedTemplate: TemplateValue, Sendable {
  public static let type: String = "dashed"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivStrokeStyleDashedTemplate?) -> DeserializationResult<DivStrokeStyleDashed> {
    return .success(DivStrokeStyleDashed())
  }

  public static func resolveValue(context: TemplatesContext, parent: DivStrokeStyleDashedTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivStrokeStyleDashed> {
    return .success(DivStrokeStyleDashed())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivStrokeStyleDashedTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivStrokeStyleDashedTemplate {
    return self
  }
}
