import CoreGraphics

import CommonCorePublic
import LayoutKit

extension DivCustom: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let children = try items?.makeBlocks(
      context: context
    ) ?? []
    return try applyBaseProperties(
      to: { try makeBaseBlock(context: context, children: children) },
      context: context,
      actions: nil,
      actionAnimation: nil,
      doubleTapActions: nil,
      longTapActions: nil
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext, children: [Block]) throws -> Block {
    let contentHeightTrait = makeContentHeightTrait(with: context)
    let contentWidthTrait = makeContentWidthTrait(with: context)
    let customData = DivCustomData(
      name: customType,
      data: customProps ?? [:],
      children: children,
      widthTrait: contentWidthTrait,
      heightTrait: contentHeightTrait
    )
    return try ContainerBlock(
      layoutDirection: contentHeightTrait.isResizable ? .vertical : .horizontal,
      widthTrait: makeContentWidthTrait(with: context),
      heightTrait: contentHeightTrait,
      children: [
        context.divCustomBlockFactory.makeBlock(data: customData, context: context),
      ]
    )
  }
}
