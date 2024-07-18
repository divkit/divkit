// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivFilterRtlMirrorTemplate: TemplateValue {
  public static let type: String = "rtl_mirror"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivFilterRtlMirrorTemplate?) -> DeserializationResult<DivFilterRtlMirror> {
    return .success(DivFilterRtlMirror())
  }

  public static func resolveValue(context: TemplatesContext, parent: DivFilterRtlMirrorTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFilterRtlMirror> {
    return .success(DivFilterRtlMirror())
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivFilterRtlMirrorTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivFilterRtlMirrorTemplate {
    return self
  }
}
