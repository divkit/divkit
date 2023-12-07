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
    columnCount: Int? = nil,
    infiniteScroll: Bool = false,
    scrollbar: GalleryViewModel.Scrollbar = .none
  ) throws -> GalleryViewModel {
    let expressionResolver = context.expressionResolver
    let childrenContext = context.modifying(errorsStorage: DivErrorsStorage(errors: []))
    var children: [GalleryViewModel.Item] = items.makeBlocks(
      context: childrenContext,
      sizeModifier: DivGallerySizeModifier(
        context: context,
        gallery: self,
        direction: direction
      ),
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

    if infiniteScroll,
       let last = children.last,
       let first = children.first {
      children = [last] + children + [first]
    }

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
      columnCount: columnCount ?? 1,
      infiniteScroll: infiniteScroll,
      scrollbar: scrollbar
    )
  }

  private func makeMetrics(
    spacing: CGFloat,
    crossSpacing: CGFloat,
    childrenCount: Int,
    direction: GalleryViewModel.Direction,
    with expressionResolver: ExpressionResolver
  ) throws -> GalleryViewMetrics {
    let spacings = try [CGFloat](
      repeating: spacing,
      times: UInt(value: childrenCount - 1)
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
