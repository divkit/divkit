import Foundation

extension Div {
  var children: [Div] {
    switch self {
    case let .divContainer(div): div.nonNilItems
    case let .divGrid(div): div.nonNilItems
    case let .divGallery(div): div.nonNilItems
    case let .divPager(div): div.nonNilItems
    case let .divTabs(div): div.items.map(\.div)
    case let .divCustom(div): div.items ?? []
    case let .divState(div): div.states.compactMap(\.div)
    case .divGifImage,
         .divImage,
         .divIndicator,
         .divInput,
         .divSelect,
         .divSeparator,
         .divSlider,
         .divSwitch,
         .divVideo,
         .divText:
      []
    }
  }
}

extension Div {
  var isHorizontallyMatchParent: Bool {
    switch value.width {
    case .divMatchParentSize:
      true
    case .divFixedSize, .divWrapContentSize:
      false
    }
  }

  var isVerticallyMatchParent: Bool {
    switch value.height {
    case .divMatchParentSize:
      true
    case .divFixedSize, .divWrapContentSize:
      false
    }
  }
}

extension Sequence<Div> {
  var hasHorizontallyMatchParent: Bool {
    contains { $0.isHorizontallyMatchParent }
  }

  var allHorizontallyMatchParent: Bool {
    allSatisfy(\.isHorizontallyMatchParent)
  }

  var hasVerticallyMatchParent: Bool {
    contains { $0.isVerticallyMatchParent }
  }

  var allVerticallyMatchParent: Bool {
    allSatisfy(\.isVerticallyMatchParent)
  }
}
