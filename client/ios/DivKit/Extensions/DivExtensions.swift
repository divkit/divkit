import Foundation

extension Div {
  var children: [Div] {
    switch self {
    case let .divContainer(div): return div.items
    case let .divGrid(div): return div.items
    case let .divGallery(div): return div.items
    case let .divPager(div): return div.items
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
        .divText:
      return []
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
