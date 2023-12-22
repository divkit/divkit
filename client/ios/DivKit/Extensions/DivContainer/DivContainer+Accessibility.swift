import Foundation

extension DivContainer {
  func resolveAccessibilityDescription(_ context: DivBlockModelingContext) -> String? {
    guard let accessibility = accessibility else {
      return nil
    }

    let expressionResolver = context.expressionResolver
    switch accessibility.resolveMode(expressionResolver) {
    case .default:
      return accessibility.resolveDescription(expressionResolver)
    case .merge:
      if let description = accessibility.resolveDescription(expressionResolver) {
        return description
      }
      return nonNilItems.resolveMergedDescription(context)
    case .exclude:
      return nil
    }
  }
}

extension Array where Element == Div {
  fileprivate func resolveMergedDescription(_ context: DivBlockModelingContext) -> String? {
    var result = ""
    func traverse(div: Div) {
      if let descritpion = div.resolveDescription(context), !descritpion.isEmpty {
        result = result.isEmpty ? descritpion : result + " " + descritpion
      }
      div.children.forEach(traverse)
    }
    forEach(traverse)
    return result.isEmpty ? nil : result
  }
}

extension Div {
  fileprivate func resolveDescription(_ context: DivBlockModelingContext) -> String? {
    let expressionResolver = context.expressionResolver
    let accessibility = value.accessibility
    guard accessibility?.resolveMode(expressionResolver) != .exclude else {
      return nil
    }
    switch self {
    case .divContainer,
         .divCustom,
         .divGallery,
         .divGifImage,
         .divGrid,
         .divImage,
         .divIndicator,
         .divInput,
         .divPager,
         .divSelect,
         .divSeparator,
         .divSlider,
         .divState,
         .divTabs,
         .divVideo:
      return accessibility?.resolveDescription(expressionResolver)
    case let .divText(divText):
      let extensionDescription = context
        .getExtensionHandlers(for: divText)
        .compactMap { $0.accessibilityElement?.strings.label }
        .reduce(nil) { $0?.appending(" " + $1) ?? $1 }
      return extensionDescription ??
        accessibility?.resolveDescription(expressionResolver) ??
        divText.resolveText(expressionResolver)
    }
  }
}
