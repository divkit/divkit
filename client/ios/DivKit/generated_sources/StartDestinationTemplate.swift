// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class StartDestinationTemplate: TemplateValue {
  public static let type: String = "start"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: StartDestinationTemplate?) -> DeserializationResult<StartDestination> {
    return .success(StartDestination())
  }

  public static func resolveValue(context: TemplatesContext, parent: StartDestinationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<StartDestination> {
    return .success(StartDestination())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> StartDestinationTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> StartDestinationTemplate {
    return self
  }
}
