import CoreGraphics

import BaseUIPublic
import CommonCorePublic
import LayoutKit

protocol DivGalleryProtocol: DivBase {
  var items: [Div] { get }
}

extension DivGalleryProtocol {
  func makeGalleryModel(
    context: DivBlockModelingContext,
    direction: GalleryViewModel.Direction,
    spacing: CGFloat,
    crossSpacing: CGFloat,
    defaultAlignment: Alignment,
    resizableInsets: InsetMode.Resizable?,
    scrollMode: GalleryViewModel.ScrollMode,
    columnCount: Int? = nil
  ) throws -> GalleryViewModel {
    let expressionResolver = context.expressionResolver
    let fallbackWidth = getFallbackWidth(direction: direction, context: context)
    let fallbackHeight = getFallbackHeight(direction: direction, context: context)
    let children: [GalleryViewModel.Item] = try items.makeBlocks(
      context: context,
      overridenWidth: fallbackWidth,
      overridenHeight: fallbackHeight,
      mappedBy: {
        GalleryViewModel.Item(
          crossAlignment: (
            direction.isHorizontal
              ? $0.value.resolveAlignmentVertical(expressionResolver)?.alignment
              : $0.value.resolveAlignmentHorizontal(expressionResolver)?.alignment
          ) ?? defaultAlignment,
          content: $1
        )
      }
    )

    try checkLayoutConstraints(
      children,
      path: context.parentPath
    )

    let metrics = try makeMetrics(
      spacing: spacing,
      crossSpacing: crossSpacing,
      childrenCount: children.count,
      resizableInsets: resizableInsets,
      direction: direction,
      with: expressionResolver
    )

    return GalleryViewModel(
      items: children,
      metrics: metrics,
      scrollMode: scrollMode,
      path: context.parentPath,
      direction: direction,
      columnCount: columnCount ?? 1
    )
  }

  private func makeMetrics(
    spacing: CGFloat,
    crossSpacing: CGFloat,
    childrenCount: Int,
    resizableInsets: InsetMode.Resizable?,
    direction: GalleryViewModel.Direction,
    with expressionResolver: ExpressionResolver
  ) throws -> GalleryViewMetrics {
    let spacings = [CGFloat](
      repeating: spacing,
      times: try UInt(value: childrenCount - 1)
    )

    let axialInsets: SideInsets
    let crossInsets: SideInsets
    let horizontalInsets = paddings.makeHorizontalInsets(with: expressionResolver)
    let verticalInsets = paddings.makeVerticalInsets(with: expressionResolver)
    switch direction {
    case .horizontal:
      axialInsets = horizontalInsets
      crossInsets = verticalInsets
    case .vertical:
      axialInsets = verticalInsets
      crossInsets = horizontalInsets
    }

    let axialInsetOverride = resizableInsets.map { InsetMode.resizable(params: $0) }
    return GalleryViewMetrics(
      axialInsetMode: axialInsetOverride ?? .fixed(values: axialInsets),
      crossInsetMode: .fixed(values: crossInsets),
      spacings: spacings,
      crossSpacing: crossSpacing
    )
  }

  private func getFallbackWidth(
    direction: GalleryViewModel.Direction,
    context: DivBlockModelingContext
  ) -> DivOverridenSize? {
    if width.isIntrinsic {
      switch direction {
      case .vertical:
        if items.allHorizontallyMatchParent {
          context.addError(
            level: .warning,
            message: "All items in vertical \(typeName) with wrap_content width has match_parent width"
          )
          return defaultFallbackSize
        }
      case .horizontal:
        break
      }
    }

    return nil
  }

  private func getFallbackHeight(
    direction: GalleryViewModel.Direction,
    context: DivBlockModelingContext
  ) -> DivOverridenSize? {
    if height.isIntrinsic {
      switch direction {
      case .horizontal:
        if items.allVerticallyMatchParent {
          context.addError(
            level: .warning,
            message: "All items in horizontal \(typeName) with wrap_content height has match_parent height"
          )
          return defaultFallbackSize
        }
      case .vertical:
        break
      }
    }
    return nil
  }

  private func checkLayoutConstraints(
    _ children: [GalleryViewModel.Item],
    path: UIElementPath
  ) throws {
    guard !children.isEmpty else {
      throw DivBlockModelingError(
        "\(typeName) has no items",
        path: path
      )
    }
  }

  private var typeName: String {
    guard let typeName = String(describing: self).split(separator: ".").last else {
      return "DivGallery"
    }
    return String(typeName)
  }
}

private let defaultFallbackSize = DivOverridenSize(
  original: .divMatchParentSize(DivMatchParentSize()),
  overriden: .divWrapContentSize(DivWrapContentSize(constrained: .value(true)))
)
