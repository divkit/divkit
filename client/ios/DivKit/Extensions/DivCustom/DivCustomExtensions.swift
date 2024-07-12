import CoreGraphics

import LayoutKit
import VGSL

extension DivCustom: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let children = items?.makeBlocks(
      context: context
    ) ?? []
    return try applyBaseProperties(
      to: { try makeBaseBlock(context: context, children: children) },
      context: context,
      actionsHolder: nil
    )
  }

  private func makeBaseBlock(
    context: DivBlockModelingContext,
    children: [Block]
  ) throws -> Block {
    let contentWidthTrait = resolveContentWidthTrait(context)
    let contentHeightTrait = resolveContentHeightTrait(context)
    let customData = DivCustomData(
      name: customType,
      data: customProps ?? [:],
      children: children,
      widthTrait: contentWidthTrait,
      heightTrait: contentHeightTrait
    )
    return try ContainerBlock(
      layoutDirection: contentHeightTrait.isResizable ? .vertical : .horizontal,
      widthTrait: contentWidthTrait,
      heightTrait: contentHeightTrait,
      children: [
        context.divCustomBlockFactory.makeBlock(data: customData, context: context),
      ]
    )
  }
}
