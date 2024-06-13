import CoreGraphics

import CommonCorePublic
import LayoutKit

extension DivContainer: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: self,
      customAccessibilityParams: CustomAccessibilityParams { [unowned self] in
        resolveAccessibilityDescription(context)
      },
      clipToBounds: resolveClipToBounds(context.expressionResolver)
    )
  }

  var nonNilItems: [Div] {
    items ?? []
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let childContext = context.modifying(
      parentPath: context.parentPath + (id ?? DivContainer.type)
    )
    let expressionResolver = context.expressionResolver
    let orientation = resolveOrientation(expressionResolver)
    let block: Block = switch orientation {
    case .overlap:
      try makeOverlapBlock(context: childContext)
    case .horizontal, .vertical:
      try makeContainerBlock(
        context: childContext,
        orientation: orientation,
        layoutMode: resolveLayoutMode(expressionResolver)
      )
    }

    let aspectRatio = aspect.resolveAspectRatio(expressionResolver)
    if let aspectRatio {
      if block.calculateWidthFirst {
        return block.aspectRatio(aspectRatio)
      }
    }

    return block
  }

  private func makeOverlapBlock(context: DivBlockModelingContext) throws -> LayeredBlock {
    let expressionResolver = context.expressionResolver
    let defaultAlignment = BlockAlignment2D(
      horizontal: resolveContentAlignmentHorizontal(expressionResolver).alignment,
      vertical: resolveContentAlignmentVertical(expressionResolver).alignment
    )

    let children = makeChildren(
      context: context,
      mappedBy: { div, block, context in
        LayeredBlock.Child(
          content: block,
          alignment: div.value.resolveAlignment(context, defaultAlignment: defaultAlignment)
        )
      }
    )

    let aspectRatio = aspect.resolveAspectRatio(expressionResolver)
    let layeredBlock = LayeredBlock(
      widthTrait: resolveContentWidthTrait(context),
      heightTrait: resolveContentHeightTrait(context, aspectRatio: aspectRatio),
      children: children
    )

    return layeredBlock
  }

  private func makeContainerBlock(
    context: DivBlockModelingContext,
    orientation: Orientation,
    layoutMode: LayoutMode
  ) throws -> ContainerBlock {
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

    let defaultCrossAlignment = switch layoutMode {
    case .noWrap:
      crossAlignment
    case .wrap:
      ContainerBlock.CrossAlignment.leading
    }
    let children: [ContainerBlock.Child] = makeChildren(
      context: context,
      mappedBy: { div, block, context in
        ContainerBlock.Child(
          content: block,
          crossAlignment: div.value.crossAlignment(
            for: layoutDirection,
            context: context
          ) ?? defaultCrossAlignment
        )
      }
    )

    let aspectRatio = aspect.resolveAspectRatio(expressionResolver)
    let containerBlock = try ContainerBlock(
      blockLayoutDirection: context.layoutDirection,
      layoutDirection: layoutDirection,
      layoutMode: layoutMode.system,
      widthTrait: resolveContentWidthTrait(context),
      heightTrait: resolveContentHeightTrait(context, aspectRatio: aspectRatio),
      axialAlignment: axialAlignment,
      crossAlignment: crossAlignment,
      children: children,
      separator: resolveSeparator(context),
      lineSeparator: resolveLineSeparator(context),
      clipContent: resolveClipToBounds(expressionResolver)
    )

    return containerBlock
  }

  private func resolveContentHeightTrait(
    _ context: DivBlockModelingContext,
    aspectRatio: CGFloat?
  ) -> LayoutTrait {
    if aspectRatio != nil {
      return .resizable
    }
    return resolveContentHeightTrait(context)
  }

  private func resolveSeparator(
    _ context: DivBlockModelingContext
  ) throws -> ContainerBlock.Separator? {
    guard let separator else {
      return nil
    }
    let separatorBlock = separator.style.makeBlock(
      context: context, corners: .all
    ).addingEdgeInsets(separator.margins.resolve(context))

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

  private func resolveLineSeparator(
    _ context: DivBlockModelingContext
  ) -> ContainerBlock.Separator? {
    guard let lineSeparator else {
      return nil
    }
    let lineSeparatorBlock = lineSeparator.style.makeBlock(
      context: context, corners: .all
    ).addingEdgeInsets(lineSeparator.margins.resolve(context))

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
      .leading
    case .center:
      .center
    case .right, .end:
      .trailing
    }
  }

  func makeCrossAlignment(uiLayoutDirection: UserInterfaceLayoutDirection) -> ContainerBlock
    .CrossAlignment {
    switch self {
    case .left:
      .leading
    case .right:
      .trailing
    case .start:
      uiLayoutDirection == .leftToRight ? .leading : .trailing
    case .center:
      .center
    case .end:
      uiLayoutDirection == .rightToLeft ? .leading : .trailing
    }
  }

  func makeContentAlignment(uiLayoutDirection: UserInterfaceLayoutDirection) -> Alignment {
    switch self {
    case .left:
      .leading
    case .right:
      .trailing
    case .start:
      uiLayoutDirection == .leftToRight ? .leading : .trailing
    case .center:
      .center
    case .end:
      uiLayoutDirection == .rightToLeft ? .leading : .trailing
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
      .leading
    case .center:
      .center
    case .bottom:
      .trailing
    case .baseline:
      .baseline
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
      .noWrap
    case .wrap:
      .wrap
    }
  }
}

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
      .leading
    case .center:
      .center
    case .bottom:
      .trailing
    case .baseline:
      .baseline
    case .spaceBetween, .spaceEvenly, .spaceAround:
      .leading
    }
  case .vertical:
    switch horizontalAlignment {
    case .left:
      uiLayoutDirection == .leftToRight ? .leading : .trailing
    case .right:
      uiLayoutDirection == .rightToLeft ? .leading : .trailing
    case .start:
      .leading
    case .center:
      .center
    case .end:
      .trailing
    case .spaceBetween, .spaceEvenly, .spaceAround:
      .center
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
