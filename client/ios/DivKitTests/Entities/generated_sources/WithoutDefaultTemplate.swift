// Generated code. Do not modify.

@testable import DivKit

import Foundation
import Serialization
import VGSL

public final class WithoutDefaultTemplate: TemplateValue {
  public static let type: String = "non_default"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: WithoutDefaultTemplate?) -> DeserializationResult<WithoutDefault> {
    return .success(WithoutDefault())
  }

  public static func resolveValue(context: TemplatesContext, parent: WithoutDefaultTemplate?, useOnlyLinks: Bool) -> DeserializationResult<WithoutDefault> {
    return .success(WithoutDefault())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> WithoutDefaultTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> WithoutDefaultTemplate {
    return self
  }
}
