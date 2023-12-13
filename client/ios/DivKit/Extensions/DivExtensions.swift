import Foundation

extension Div {
  var children: [Div] {
    switch self {
    case let .divContainer(div): return div.nonNilItems
    case let .divGrid(div): return div.nonNilItems
    case let .divGallery(div): return div.nonNilItems
    case let .divPager(div): return div.nonNilItems
    case let .divTabs(div): return div.items.map(\.div)
    case let .divCustom(div): return div.items ?? []
    case let .divState(div): return div.states.compactMap(\.div)
    case .divGifImage,
         .divImage,
         .divIndicator,
         .divInput,
         .divSelect,
         .divSeparator,
         .divSlider,
         .divVideo,
         .divText:
      return []
    }
  }
}

extension Div {
  func resolveA11yDescription(_ context: DivBlockModelingContext) -> String? {
    let expressionResolver = context.expressionResolver
    let accessibility = value.accessibility
    guard accessibility.resolveMode(expressionResolver) != .exclude else {
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
         .divTabs,
         .divSelect,
         .divSeparator,
         .divSlider,
         .divVideo,
         .divState:
      return accessibility.resolveDescription(expressionResolver)
    case let .divText(divText):
      let handlerDescription = context
        .getExtensionHandlers(for: divText)
        .compactMap { $0.accessibilityElement?.strings.label }
        .reduce(nil) { $0?.appending(" " + $1) ?? $1 }
      return handlerDescription ??
        divText.accessibility.resolveDescription(expressionResolver) ??
        divText.resolveText(expressionResolver) as String?
    }
  }
}

extension Div {
  var isHorizontallyMatchParent: Bool {
    switch value.width {
    case .divMatchParentSize:
      return true
    case .divFixedSize, .divWrapContentSize:
      return false
    }
  }

  var isVerticallyMatchParent: Bool {
    switch value.height {
    case .divMatchParentSize:
      return true
    case .divFixedSize, .divWrapContentSize:
      return false
    }
  }
}

extension Sequence where Element == Div {
  var hasHorizontallyMatchParent: Bool {
    contains { $0.isHorizontallyMatchParent }
  }

  var allHorizontallyMatchParent: Bool {
    allSatisfy { $0.isHorizontallyMatchParent }
  }

  var hasVerticallyMatchParent: Bool {
    contains { $0.isVerticallyMatchParent }
  }

  var allVerticallyMatchParent: Bool {
    allSatisfy { $0.isVerticallyMatchParent }
  }
}
