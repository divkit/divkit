public struct TemplatesContext {
  public let templates: Templates
  public let templateToType: TemplateToType
  public var templateData: TemplateData

  init(
    templates: Templates,
    templateToType: TemplateToType,
    templateData: TemplateData
  ) {
    self.templates = templates
    self.templateToType = templateToType
    self.templateData = templateData
  }
}
