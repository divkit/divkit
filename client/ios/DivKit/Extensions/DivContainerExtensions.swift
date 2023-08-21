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
      actions: makeActions(context: context),
      actionAnimation: actionAnimation.makeActionAnimation(with: context.expressionResolver),
      doubleTapActions: makeDoubleTapActions(context: context),
      longTapActions: makeLongTapActions(context: context)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let childContext = modified(context) {
      $0.parentPath = $0.parentPath + (id ?? DivContainer.type)
    }
    let expressionResolver = context.expressionResolver
    let orientation = resolveOrientation(expressionResolver)
    switch orientation {
    case .overlap:
      return try makeOverlapBlock(context: childContext)
    case .horizontal, .vertical:
      return try makeContainerBlock(
        context: childContext,
        orientation: orientation,
        layoutMode: resolveLayoutMode(expressionResolver)
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
    let defaultStateAlignment = BlockAlignment2D(
      horizontal: resolveContentAlignmentHorizontal(expressionResolver).alignment,
      vertical: resolveContentAlignmentVertical(expressionResolver).alignment
    )
    let defaultAlignment = alignment2D(withDefault: defaultStateAlignment, context: context)

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
            context: context
          )
        )
      }
    )

    guard !children.isEmpty else {
      throw DivBlockModelingError("DivContainer is empty", path: context.parentPath)
    }

    let aspectRatio = aspect.resolveAspectRatio(expressionResolver)
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

    let divContentAlignmentVertical = resolveContentAlignmentVertical(expressionResolver)
    let divContentAlignmentHorizontal = resolveContentAlignmentHorizontal(expressionResolver)

    let axialAlignment = makeAxialAlignment(
      layoutDirection,
      verticalAlignment: divContentAlignmentVertical,
      horizontalAlignment: divContentAlignmentHorizontal,
      uiLayoutDirection: context.layoutDirection
    )

    let crossAlignment = makeCrossAlignment(
      layoutDirection,
      verticalAlignment: divContentAlignmentVertical,
      horizontalAlignment: divContentAlignmentHorizontal,
      uiLayoutDirection: context.layoutDirection
    )

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
            context: context
          ) ?? defaultCrossAlignment
        )
      }
    )

    guard !children.isEmpty else {
      throw DivBlockModelingError("DivContainer is empty", path: context.parentPath)
    }

    let widthTrait = makeContentWidthTrait(with: context)
    let aspectRatio = aspect.resolveAspectRatio(expressionResolver)
    let containerBlock = try ContainerBlock(
      blockLayoutDirection: context.layoutDirection,
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
    let separatorBlock = try separator.style.makeBlock(
      context: context, corners: .all
    ).addingEdgeInsets(separator.margins.makeEdgeInsets(context: context))

    let style = ContainerBlock.Child(
      content: separatorBlock,
      crossAlignment: .center
    )

    let expressionResolver = context.expressionResolver
    return ContainerBlock.Separator(
      style: style,
      showAtEnd: separator.resolveShowAtEnd(expressionResolver),
      showAtStart: separator.resolveShowAtStart(expressionResolver),
      showBetween: separator.resolveShowBetween(expressionResolver)
    )
  }

  private func makeLineSeparator(
    with context: DivBlockModelingContext
  ) throws -> ContainerBlock.Separator? {
    guard let lineSeparator = lineSeparator else {
      return nil
    }
    let lineSeparatorBlock = try lineSeparator.style.makeBlock(
      context: context, corners: .all
    ).addingEdgeInsets(lineSeparator.margins.makeEdgeInsets(context: context))

    let style = ContainerBlock.Child(
      content: lineSeparatorBlock,
      crossAlignment: .center
    )

    let expressionResolver = context.expressionResolver
    return ContainerBlock.Separator(
      style: style,
      showAtEnd: lineSeparator.resolveShowAtEnd(expressionResolver),
      showAtStart: lineSeparator.resolveShowAtStart(expressionResolver),
      showBetween: lineSeparator.resolveShowBetween(expressionResolver)
    )
  }
}

extension DivBase {
  fileprivate func crossAlignment(
    for direction: ContainerBlock.LayoutDirection,
    context: DivBlockModelingContext
  ) -> ContainerBlock.CrossAlignment? {
    let expressionResolver = context.expressionResolver
    switch direction {
    case .horizontal: return resolveAlignmentVertical(expressionResolver)?.crossAlignment
    case .vertical: return resolveAlignmentHorizontal(expressionResolver)?
      .makeCrossAlignment(uiLayoutDirection: context.layoutDirection)
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
    case .left, .start:
      return .leading
    case .center:
      return .center
    case .right, .end:
      return .trailing
    }
  }

  func makeCrossAlignment(uiLayoutDirection: UserInterfaceLayoutDirection) -> ContainerBlock
    .CrossAlignment {
    switch self {
    case .left:
      return .leading
    case .right:
      return .trailing
    case .start:
      return uiLayoutDirection == .leftToRight ? .leading : .trailing
    case .center:
      return .center
    case .end:
      return uiLayoutDirection == .rightToLeft ? .leading : .trailing
    }
  }

  func makeContentAlignment(uiLayoutDirection: UserInterfaceLayoutDirection) -> Alignment {
    switch self {
    case .left:
      return .leading
    case .right:
      return .trailing
    case .start:
      return uiLayoutDirection == .leftToRight ? .leading : .trailing
    case .center:
      return .center
    case .end:
      return uiLayoutDirection == .rightToLeft ? .leading : .trailing
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

extension DivContentAlignmentHorizontal {
  fileprivate var alignment: Alignment {
    switch self {
    case .left, .start:
      return .leading
    case .center:
      return .center
    case .right, .end:
      return .trailing
    case .spaceEvenly, .spaceAround, .spaceBetween:
      DivKitLogger.warning("Alignment \(rawValue) is not supported")
      return .leading
    }
  }
}

extension DivContentAlignmentVertical {
  fileprivate var alignment: Alignment {
    switch self {
    case .top:
      return .leading
    case .center:
      return .center
    case .bottom:
      return .trailing
    case .spaceEvenly, .spaceAround, .spaceBetween, .baseline:
      DivKitLogger.warning("Alignment \(rawValue) is not supported")
      return .leading
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
    let expressionResolver = context.expressionResolver
    guard value.accessibility.resolveMode(expressionResolver) != .exclude
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
         .divVideo,
         .divState:
      return value.accessibility.resolveDescription(expressionResolver)
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

private let defaultFallbackSize = DivOverridenSize(
  original: .divMatchParentSize(DivMatchParentSize()),
  overriden: .divWrapContentSize(DivWrapContentSize(constrained: .value(true)))
)

fileprivate func makeCrossAlignment(
  _ direction: ContainerBlock.LayoutDirection,
  verticalAlignment: DivContentAlignmentVertical,
  horizontalAlignment: DivContentAlignmentHorizontal,
  uiLayoutDirection: UserInterfaceLayoutDirection = .leftToRight
) -> ContainerBlock.CrossAlignment {
  switch direction {
  case .horizontal:
    switch verticalAlignment {
    case .top:
      return .leading
    case .center:
      return .center
    case .bottom:
      return .trailing
    case .baseline:
      return .baseline
    case .spaceBetween, .spaceEvenly, .spaceAround:
      return .leading
    }
  case .vertical:
    switch horizontalAlignment {
    case .left:
      return uiLayoutDirection == .leftToRight ? .leading : .trailing
    case .right:
      return uiLayoutDirection == .rightToLeft ? .leading : .trailing
    case .start:
      return .leading
    case .center:
      return .center
    case .end:
      return .trailing
    case .spaceBetween, .spaceEvenly, .spaceAround:
      return .center
    }
  }
}

fileprivate func makeAxialAlignment(
  _ direction: ContainerBlock.LayoutDirection,
  verticalAlignment: DivContentAlignmentVertical,
  horizontalAlignment: DivContentAlignmentHorizontal,
  uiLayoutDirection: UserInterfaceLayoutDirection = .leftToRight
) -> ContainerBlock.AxialAlignment {
  switch direction {
  case .horizontal:
    switch horizontalAlignment {
    case .left:
      return .leading
    case .right:
      return .trailing
    case .start:
      return uiLayoutDirection == .leftToRight ? .leading : .trailing
    case .center:
      return .center
    case .end:
      return uiLayoutDirection == .rightToLeft ? .leading : .trailing
    case .spaceBetween:
      return .spaceBetween
    case .spaceAround:
      return .spaceAround
    case .spaceEvenly:
      return .spaceEvenly
    }
  case .vertical:
    switch verticalAlignment {
    case .top:
      return .leading
    case .center:
      return .center
    case .bottom:
      return .trailing
    case .baseline:
      DivKitLogger.warning("Baseline alignment not supported.")
      return .leading
    case .spaceBetween:
      return .spaceBetween
    case .spaceAround:
      return .spaceAround
    case .spaceEvenly:
      return .spaceEvenly
    }
  }
}
