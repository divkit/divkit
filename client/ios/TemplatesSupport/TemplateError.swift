enum TemplateError: Error, Equatable {
  case circularReference([String])
}
