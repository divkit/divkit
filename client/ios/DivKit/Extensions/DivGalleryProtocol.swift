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
    scrollMode: GalleryViewModel.ScrollMode,
    columnCount: Int? = nil
  ) throws -> GalleryViewModel {
    let expressionResolver = context.expressionResolver
    let fallbackWidth = getFallbackWidth(direction: direction, context: context)
    let fallbackHeight = getFallbackHeight(direction: direction, context: context)
    let childrenContext = modified(context) { $0.errorsStorage = DivErrorsStorage(errors: []) }
    let children: [GalleryViewModel.Item] = items.makeBlocks(
      context: childrenContext,
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

    if children.isEmpty {
      throw DivBlockModelingError(
        "\(typeName) has no items",
        path: context.parentPath,
        causes: childrenContext.errorsStorage.errors
      )
    } else {
      context.errorsStorage.add(contentsOf: childrenContext.errorsStorage)
    }

    let metrics = try makeMetrics(
      spacing: spacing,
      crossSpacing: crossSpacing,
      childrenCount: children.count,
      direction: direction,
      with: expressionResolver
    )

    return GalleryViewModel(
      items: children,
      layoutDirection: context.layoutDirection,
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

    return GalleryViewMetrics(
      axialInsetMode: .fixed(values: axialInsets),
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
          context.addWarning(
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
          context.addWarning(
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
