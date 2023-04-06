public struct TemplatesContext {
  @usableFromInline
  let templates: [TemplateName: Any]
  let templateToType: [TemplateName: String]
  @usableFromInline
  var templateData: [String: Any]

  init(
    templates: [TemplateName: Any],
    templateToType: [TemplateName: String],
    templateData: [String: Any]
  ) {
    self.templates = templates
    self.templateToType = templateToType
    self.templateData = templateData
  }
}
