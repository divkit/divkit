import CoreGraphics

import LayoutKit
import VGSL

protocol DivGalleryProtocol: DivBase {
  var items: [Div]? { get }
}

extension DivGalleryProtocol {
  var nonNilItems: [Div] {
    items ?? []
  }

  func makeGalleryModel(
    context: DivBlockModelingContext,
    direction: ScrollDirection,
    spacing: CGFloat,
    crossSpacing: CGFloat,
    defaultAlignment: Alignment,
    scrollMode: GalleryViewModel.ScrollMode,
    columnCount: Int? = nil,
    infiniteScroll: Bool = false,
    scrollbar: GalleryViewModel.Scrollbar = .none,
    transformation: ElementsTransformation? = nil
  ) throws -> GalleryViewModel {
    let expressionResolver = context.expressionResolver
    var children: [GalleryViewModel.Item] = nonNilItems.makeBlocks(
      context: context,
      sizeModifier: DivGallerySizeModifier(
        context: context,
        gallery: self,
        direction: direction
      ),
      mappedBy: { div, block, _ in
        GalleryViewModel.Item(
          crossAlignment: (
            direction.isHorizontal
              ? div.value.resolveAlignmentVertical(expressionResolver)?.alignment
              : div.value.resolveAlignmentHorizontal(expressionResolver)?.alignment
          ) ?? defaultAlignment,
          content: block
        )
      }
    )

    if infiniteScroll,
       let last = children.last,
       let first = children.first {
      children = [last] + children + [first]
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
      columnCount: columnCount ?? 1,
      infiniteScroll: infiniteScroll,
      scrollbar: scrollbar,
      transformation: transformation
    )
  }

  private func makeMetrics(
    spacing: CGFloat,
    crossSpacing: CGFloat,
    childrenCount: Int,
    direction: ScrollDirection,
    with expressionResolver: ExpressionResolver
  ) throws -> GalleryViewMetrics {
    let spacings = [CGFloat](
      repeating: spacing,
      times: UInt(max(0, childrenCount - 1))
    )

    let axialInsets: SideInsets
    let crossInsets: SideInsets
    let horizontalInsets = paddings.resolveHorizontalInsets(expressionResolver)
    let verticalInsets = paddings.resolveVerticalInsets(expressionResolver)
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

  var typeName: String {
    guard let typeName = String(describing: self).split(separator: ".").last else {
      return "DivGallery"
    }
    return String(typeName)
  }
}
