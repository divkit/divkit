// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class EndDestinationTemplate: TemplateValue, Sendable {
  public static let type: String = "end"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EndDestinationTemplate?) -> DeserializationResult<EndDestination> {
    return .success(EndDestination())
  }

  public static func resolveValue(context: TemplatesContext, parent: EndDestinationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EndDestination> {
    return .success(EndDestination())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EndDestinationTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EndDestinationTemplate {
    return self
  }
}
