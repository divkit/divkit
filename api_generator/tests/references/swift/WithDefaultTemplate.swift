// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class WithDefaultTemplate: TemplateValue {
  public static let type: String = "default"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: WithDefaultTemplate?) -> DeserializationResult<WithDefault> {
    return .success(WithDefault())
  }

  public static func resolveValue(context: TemplatesContext, parent: WithDefaultTemplate?, useOnlyLinks: Bool) -> DeserializationResult<WithDefault> {
    return .success(WithDefault())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> WithDefaultTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> WithDefaultTemplate {
    return self
  }
}
