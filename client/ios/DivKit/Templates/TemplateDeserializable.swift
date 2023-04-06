import Serialization

public protocol TemplateDeserializable {
  init(
    dictionary: [String: Any],
    templateToType: [TemplateName: String]
  ) throws
}
