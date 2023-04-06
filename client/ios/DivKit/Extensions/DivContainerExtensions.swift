import CoreGraphics

import CommonCorePublic
import LayoutKit

extension DivContainer: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: modified(context) {
        $0.childrenA11yDescription = makeChildrenA11yDescription(context: $0)
      },
      actions: makeActions(context: context.actionContext),
      actionAnimation: actionAnimation.makeActionAnimation(with: context.expressionResolver),
      doubleTapActions: makeDoubleTapActions(context: context.actionContext),
      longTapActions: makeLongTapActions(context: context.actionContext)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let childContext = modified(context) {
      $0.parentPath = $0.parentPath + (id ?? DivContainer.type)
    }
    let orientation = resolveOrientation(context.expressionResolver)
    switch orientation {
    case .overlap:
      return try makeOverlapBlock(context: childContext)
    case .horizontal, .vertical:
      return try makeContainerBlock(
        context: childContext,
        orientation: orientation,
        layoutMode: resolveLayoutMode(context.expressionResolver)
      )
    }
  }

  private func makeChildrenA11yDescription(context: DivBlockModelingContext) -> String? {
    var result = ""
    func traverse(div: Div) {
      result = [result, div.makeA11yDecription(context: context)].compactMap { $0 }
        .joined(separator: " ")
      div.children.forEach(traverse(div:))
    }
    items.forEach(traverse)
    return result.isEmpty ? nil : result
  }

  private func getFallbackWidth(
    orientation: Orientation,
    context: DivBlockModelingContext
  ) -> DivOverridenSize? {
    if width.isIntrinsic {
      switch orientation {
      case .horizontal:
        if items.hasHorizontallyMatchParent {
          context.addError(
            level: .warning,
            message: "Horizontal DivContainer with wrap_content width contains item with match_parent width"
          )
          return defaultFallbackSize
        }
      case .vertical, .overlap:
        if items.allHorizontallyMatchParent {
          context.addError(
            level: .warning,
            message: "All items in DivContainer with wrap_content width has match_parent width"
          )
          return defaultFallbackSize
        }
      }
    }

    return nil
  }

  private func getFallbackHeight(
    orientation: Orientation,
    context: DivBlockModelingContext
  ) -> DivOverridenSize? {
    if height.isIntrinsic {
      switch orientation {
      case .horizontal, .overlap:
        if items.allVerticallyMatchParent {
          context.addError(
            level: .warning,
            message: "All items in DivContainer with wrap_content height has match_parent height"
          )
          return defaultFallbackSize
        }
      case .vertical:
        if items.hasVerticallyMatchParent {
          context.addError(
            level: .warning,
            message: "Vertical DivContainer with wrap_content height contains item with match_parent height"
          )
          return defaultFallbackSize
        }
      }
    }
    return nil
  }

  private func makeOverlapBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver
    let defaultAlignment = BlockAlignment2D(
      horizontal: resolveContentAlignmentHorizontal(expressionResolver).alignment,
      vertical: resolveContentAlignmentVertical(expressionResolver).alignment
    )

    let fallbackWidth = getFallbackWidth(orientation: .overlap, context: context)
    let fallbackHeight = getFallbackHeight(orientation: .overlap, context: context)

    let children = try items.makeBlocks(
      context: context,
      overridenWidth: fallbackWidth,
      overridenHeight: fallbackHeight,
      mappedBy: { div, block in
        LayeredBlock.Child(
          content: block,
          alignment: div.value.alignment2D(
            withDefault: defaultAlignment,
            expressionResolver: expressionResolver
          )
        )
      }
    )

    guard !children.isEmpty else {
      throw DivBlockModelingError("DivContainer is empty", path: context.parentPath)
    }

    let aspectRatio = resolveAspectRatio(expressionResolver)
    let layeredBlock = LayeredBlock(
      widthTrait: makeContentWidthTrait(with: context),
      heightTrait: makeHeightTrait(context: context, aspectRatio: aspectRatio),
      children: children
    )

    if let aspectRatio = aspectRatio {
      return AspectBlock(content: layeredBlock, aspectRatio: aspectRatio)
    }

    return layeredBlock
  }

  private func makeContainerBlock(
    context: DivBlockModelingContext,
    orientation: Orientation,
    layoutMode: LayoutMode
  ) throws -> Block {
    let expressionResolver = context.expressionResolver
    let layoutDirection = orientation.layoutDirection
    let axialAlignment: Alignment
    let crossAlignment: ContainerBlock.CrossAlignment
    switch layoutDirection {
    case .horizontal:
      axialAlignment = resolveContentAlignmentHorizontal(expressionResolver).alignment
      crossAlignment = resolveContentAlignmentVertical(expressionResolver).crossAlignment
    case .vertical:
      axialAlignment = resolveContentAlignmentVertical(expressionResolver).alignment
      crossAlignment = resolveContentAlignmentHorizontal(expressionResolver).crossAlignment
    }

    let fallbackWidth = getFallbackWidth(
      orientation: orientation,
      context: context
    )
    let fallbackHeight = getFallbackHeight(
      orientation: orientation,
      context: context
    )

    // Before block's making we need to filter items and remove
    // what has "matchParent" for opposite directions
    let filtredItems = items.filter {
      guard layoutMode == .wrap else { return true }
      if orientation == .vertical {
        if items.hasHorizontallyMatchParent {
          context.addError(
            level: .warning,
            message: "Vertical DivContainer with wrap layout mode contains item with match_parent width"
          )
        }
      } else {
        if items.hasVerticallyMatchParent {
          context.addError(
            level: .warning,
            message: "Horizontal DivContainer with wrap layout mode contains item with match_parent height"
          )
        }
      }

      return orientation == .horizontal ? !$0.isVerticallyMatchParent : !$0
        .isHorizontallyMatchParent
    }

    let defaultCrossAlignment: ContainerBlock.CrossAlignment
    switch layoutMode {
    case .noWrap:
      defaultCrossAlignment = crossAlignment
    case .wrap:
      defaultCrossAlignment = ContainerBlock.CrossAlignment.leading
    }

    let children = try filtredItems.makeBlocks(
      context: context,
      overridenWidth: fallbackWidth,
      overridenHeight: fallbackHeight,
      mappedBy: { div, block in
        ContainerBlock.Child(
          content: block,
          crossAlignment: div.value.crossAlignment(
            for: layoutDirection,
            expressionResolver: expressionResolver
          ) ?? defaultCrossAlignment
        )
      }
    )

    guard !children.isEmpty else {
      throw DivBlockModelingError("DivContainer is empty", path: context.parentPath)
    }

    let widthTrait = makeContentWidthTrait(with: context)
    let aspectRatio = resolveAspectRatio(expressionResolver)
    let containerBlock = try ContainerBlock(
      layoutDirection: layoutDirection,
      layoutMode: layoutMode.system,
      widthTrait: widthTrait,
      heightTrait: makeHeightTrait(context: context, aspectRatio: aspectRatio),
      axialAlignment: axialAlignment,
      crossAlignment: crossAlignment,
      children: children,
      separator: makeSeparator(with: context),
      lineSeparator: makeLineSeparator(with: context)
    )

    if let aspectRatio = aspectRatio {
      if containerBlock.calculateWidthFirst {
        return AspectBlock(content: containerBlock, aspectRatio: aspectRatio)
      }
      context.addError(
        level: .warning,
        message: "Aspect height is not supported for vertical container with wrap layout mode"
      )
    }

    return containerBlock
  }

  private func resolveAspectRatio(_ expressionResolver: ExpressionResolver) -> CGFloat? {
    if let aspect = aspect, let ratio = aspect.resolveRatio(expressionResolver) {
      // AspectBlock has inverted ratio
      return 1 / ratio
    }
    return nil
  }

  private func makeHeightTrait(
    context: DivBlockModelingContext,
    aspectRatio: CGFloat?
  ) -> LayoutTrait {
    if aspectRatio != nil {
      return .resizable
    }
    return makeContentHeightTrait(with: context)
  }

  private func makeSeparator(
    with context: DivBlockModelingContext
  ) throws -> ContainerBlock.Separator? {
    guard let separator = separator else {
      return nil
    }
    let separatorBlock = try separator.style.makeBlock(context: context, corners: .all)
    let style = ContainerBlock.Child(
      content: separatorBlock,
      crossAlignment: .center
    )
    return ContainerBlock.Separator(
      style: style,
      showAtEnd: separator.resolveShowAtEnd(context.expressionResolver),
      showAtStart: separator.resolveShowAtStart(context.expressionResolver),
      showBetween: separator.resolveShowBetween(context.expressionResolver)
    )
  }

  private func makeLineSeparator(
    with context: DivBlockModelingContext
  ) throws -> ContainerBlock.Separator? {
    guard let lineSeparator = lineSeparator else {
      return nil
    }
    let lineSeparatorBlock = try lineSeparator.style.makeBlock(context: context, corners: .all)
    let style = ContainerBlock.Child(
      content: lineSeparatorBlock,
      crossAlignment: .center
    )
    return ContainerBlock.Separator(
      style: style,
      showAtEnd: lineSeparator.resolveShowAtEnd(context.expressionResolver),
      showAtStart: lineSeparator.resolveShowAtStart(context.expressionResolver),
      showBetween: lineSeparator.resolveShowBetween(context.expressionResolver)
    )
  }
}

extension DivBase {
  fileprivate func crossAlignment(
    for direction: ContainerBlock.LayoutDirection,
    expressionResolver: ExpressionResolver
  ) -> ContainerBlock.CrossAlignment? {
    switch direction {
    case .horizontal: return resolveAlignmentVertical(expressionResolver)?.crossAlignment
    case .vertical: return resolveAlignmentHorizontal(expressionResolver)?.crossAlignment
    }
  }
}

extension DivContainer.Orientation {
  fileprivate var layoutDirection: ContainerBlock.LayoutDirection {
    switch self {
    case .vertical:
      return .vertical
    case .horizontal:
      return .horizontal
    case .overlap:
      assertionFailure("layout direction for overlap")
      return .vertical
    }
  }
}

extension DivAlignmentHorizontal {
  var alignment: Alignment {
    switch self {
    case .left:
      return .leading
    case .center:
      return .center
    case .right:
      return .trailing
    }
  }

  var crossAlignment: ContainerBlock.CrossAlignment {
    switch self {
    case .left:
      return .leading
    case .center:
      return .center
    case .right:
      return .trailing
    }
  }
}

extension DivAlignmentVertical {
  var alignment: Alignment {
    switch self {
    case .top:
      return .leading
    case .center:
      return .center
    case .bottom:
      return .trailing
    case .baseline:
      DivKitLogger.warning("Baseline alignment not supported.")
      return .leading
    }
  }

  var crossAlignment: ContainerBlock.CrossAlignment {
    switch self {
    case .top:
      return .leading
    case .center:
      return .center
    case .bottom:
      return .trailing
    case .baseline:
      return .baseline
    }
  }
}

extension DivContainer.LayoutMode {
  fileprivate var system: ContainerBlock.LayoutMode {
    switch self {
    case .noWrap:
      return .noWrap
    case .wrap:
      return .wrap
    }
  }
}

extension Div {
  fileprivate func makeA11yDecription(context: DivBlockModelingContext) -> String? {
    guard value.accessibility.resolveMode(context.expressionResolver) != .exclude
    else { return nil }
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
        .divState:
      return value.accessibility.resolveDescription(context.expressionResolver)
    case let .divText(divText):
      let handlerDescription = context
        .getExtensionHandlers(for: divText)
        .compactMap { $0.accessibilityElement?.strings.label }
        .reduce(nil) { $0?.appending(" " + $1) ?? $1 }
      return handlerDescription ??
        divText.accessibility.resolveDescription(context.expressionResolver) ??
        divText.resolveText(context.expressionResolver) as String?
    }
  }
}

private let defaultFallbackSize = DivOverridenSize(
  original: .divMatchParentSize(DivMatchParentSize()),
  overriden: .divWrapContentSize(DivWrapContentSize(constrained: .value(true)))
)
