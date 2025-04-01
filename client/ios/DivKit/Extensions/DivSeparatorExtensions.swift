import Foundation
import LayoutKit

extension DivSeparator: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let context = modifiedContextParentPath(context)
    return try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: self
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let widthTrait = resolveContentWidthTrait(context)
    let heightTrait = resolveContentHeightTrait(context)

    let needsBeWrappedInContainer: Bool
    let trait: LayoutTrait

    let delimiterStyle = delimiterStyle ?? DivSeparator.DelimiterStyle()
    let delimiterOrientation = delimiterStyle.resolveOrientation(
      context.expressionResolver
    )
    switch delimiterOrientation {
    case .horizontal:
      needsBeWrappedInContainer = !heightTrait.isIntrinsic
      trait = widthTrait
    case .vertical:
      needsBeWrappedInContainer = !widthTrait.isIntrinsic
      trait = heightTrait
    }

    let separatorBlock: SeparatorBlock

    switch trait {
    case .intrinsic:
      throw DivBlockModelingError(
        "DivSeparator has wrap_content size by orientation dimension",
        path: context.path
      )
    case let .fixed(size) where !needsBeWrappedInContainer:
      separatorBlock = SeparatorBlock(
        color: delimiterStyle.resolveColor(context.expressionResolver),
        direction: delimiterOrientation.direction,
        size: size
      )
    case .fixed: // where needsBeWrappedInContainer
      // There can be cases in theory when bothe wrapping container and separator
      // have fixed sizes. This will lead to undesired layout in case of their mismatch.
      separatorBlock = SeparatorBlock(
        color: delimiterStyle.resolveColor(context.expressionResolver),
        direction: delimiterOrientation.direction
      )
    case let .weighted(weight):
      separatorBlock = SeparatorBlock(
        color: delimiterStyle.resolveColor(context.expressionResolver),
        direction: delimiterOrientation.direction,
        weight: weight
      )
    }

    if needsBeWrappedInContainer {
      return try ContainerBlock(
        layoutDirection: .vertical,
        widthTrait: widthTrait,
        heightTrait: heightTrait,
        horizontalChildrenAlignment: .center,
        verticalChildrenAlignment: .center,
        children: [separatorBlock]
      )
    }

    return separatorBlock
  }
}

extension DivSeparator.DelimiterStyle.Orientation {
  fileprivate var direction: SeparatorBlock.Direction {
    switch self {
    case .horizontal:
      .horizontal
    case .vertical:
      .vertical
    }
  }
}

extension LayoutTrait {
  fileprivate var isIntrinsic: Bool {
    if case .intrinsic = self { return true }
    return false
  }
}
