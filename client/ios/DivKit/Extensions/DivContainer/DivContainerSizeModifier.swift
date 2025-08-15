import LayoutKit

struct DivContainerSizeModifier: DivSizeModifier {
  private static let fallbackSize: DivSize =
    .divWrapContentSize(DivWrapContentSize(constrained: .value(true)))

  private let shouldOverrideWidth: Bool
  private let shouldOverrideHeight: Bool

  init(
    context: DivBlockModelingContext,
    container: DivContainer,
    orientation: DivContainer.Orientation
  ) {
    let items = container.nonNilItems
    if items.isEmpty {
      shouldOverrideWidth = false
      shouldOverrideHeight = false
      return
    }

    let isWrapContentWidth = container.getTransformedWidth(context).isIntrinsic
    switch orientation {
    case .horizontal:
      if isWrapContentWidth, items.hasHorizontallyMatchParent {
        context.addWarning(
          message: "Horizontal DivContainer with wrap_content width contains item with match_parent width"
        )
        shouldOverrideWidth = true
      } else {
        shouldOverrideWidth = false
      }
    case .vertical, .overlap:
      if isWrapContentWidth, items.allHorizontallyMatchParent {
        context.addWarning(
          message: "All items in DivContainer with wrap_content width has match_parent width"
        )
        shouldOverrideWidth = true
      } else {
        shouldOverrideWidth = false
      }
    }

    let isWrapContentHeight = container.getTransformedHeight(context).isIntrinsic
      && container.aspect == nil
    switch orientation {
    case .vertical:
      if isWrapContentHeight, items.hasVerticallyMatchParent {
        context.addWarning(
          message: "Vertical DivContainer with wrap_content height contains item with match_parent height"
        )
        shouldOverrideHeight = true
      } else {
        shouldOverrideHeight = false
      }
    case .horizontal, .overlap:
      if isWrapContentHeight, items.allVerticallyMatchParent {
        context.addWarning(
          message: "All items in DivContainer with wrap_content height has match_parent height"
        )
        shouldOverrideHeight = true
      } else {
        shouldOverrideHeight = false
      }
    }
  }

  func transformWidth(_ width: DivSize) -> DivSize {
    if shouldOverrideWidth, case .divMatchParentSize = width {
      return Self.fallbackSize
    }
    return width
  }

  func transformHeight(_ height: DivSize) -> DivSize {
    if shouldOverrideHeight, case .divMatchParentSize = height {
      return Self.fallbackSize
    }
    return height
  }
}
