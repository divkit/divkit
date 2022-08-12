// Copyright 2018 Yandex LLC. All rights reserved.

public struct Context {
  public let templates: Templates
  public let templateToType: TemplateToType
  public var templateData: TemplateData

  public init(
    templates: Templates,
    templateToType: TemplateToType,
    templateData: TemplateData
  ) {
    self.templates = templates
    self.templateToType = templateToType
    self.templateData = templateData
  }

  public static let empty = Context(templates: [:], templateToType: [:], templateData: [:])
}
