import LayoutKit

struct DivContainerSizeModifier: DivSizeModifier {
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

    let resolver = context.expressionResolver
    let transformedWidth = container.getTransformedWidth(context)
    let isWrapContentWidth = transformedWidth.isIntrinsic
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
      if isWrapContentWidth,
         items.allHorizontallyMatchParent,
         !hasPositiveMinSize(transformedWidth, resolver) {
        context.addWarning(
          message: "All items in DivContainer with wrap_content width has match_parent width"
        )
        shouldOverrideWidth = true
      } else {
        shouldOverrideWidth = false
      }
    }

    let transformedHeight = container.getTransformedHeight(context)
    let isWrapContentHeight = transformedHeight.isIntrinsic && container.aspect == nil
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
      if isWrapContentHeight,
         items.allVerticallyMatchParent,
         !hasPositiveMinSize(transformedHeight, resolver) {
        context.addWarning(
          message: "All items in DivContainer with wrap_content height has match_parent height"
        )
        shouldOverrideHeight = true
      } else {
        shouldOverrideHeight = false
      }
    }
  }

  // Convert a match_parent size into a constrained wrap_content, preserving the original
  // min_size/max_size. Otherwise the wrap_content parent wraps to the child's unconstrained
  // content size, ignoring the bounds. The child then fills back to the resolved parent size via
  // fillsCrossAxis (clamped again by its own min/max in ContainerBlockLayout).
  private static func fallbackSize(
    minSize: DivSizeUnitValue?,
    maxSize: DivSizeUnitValue?
  ) -> DivSize {
    .divWrapContentSize(DivWrapContentSize(
      constrained: .value(true),
      maxSize: maxSize,
      minSize: minSize
    ))
  }

  func transformWidth(_ width: DivSize) -> DivSize {
    if shouldOverrideWidth, case let .divMatchParentSize(size) = width {
      return Self.fallbackSize(minSize: size.minSize, maxSize: size.maxSize)
    }
    return width
  }

  func transformHeight(_ height: DivSize) -> DivSize {
    if shouldOverrideHeight, case let .divMatchParentSize(size) = height {
      return Self.fallbackSize(minSize: size.minSize, maxSize: size.maxSize)
    }
    return height
  }
}

private func hasPositiveMinSize(_ size: DivSize, _ resolver: ExpressionResolver) -> Bool {
  guard case let .divWrapContentSize(wrapContent) = size else {
    return false
  }
  return (wrapContent.minSize?.resolveValue(resolver) ?? 0) > 0
}
