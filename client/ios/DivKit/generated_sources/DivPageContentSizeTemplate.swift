// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPageContentSizeTemplate: TemplateValue {
  public static let type: String = "wrap_content"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivPageContentSizeTemplate?) -> DeserializationResult<DivPageContentSize> {
    return .success(DivPageContentSize())
  }

  public static func resolveValue(context: TemplatesContext, parent: DivPageContentSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPageContentSize> {
    return .success(DivPageContentSize())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivPageContentSizeTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivPageContentSizeTemplate {
    return self
  }
}
