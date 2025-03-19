import CoreGraphics
import LayoutKit
import VGSL

protocol DivGalleryProtocol: DivBase {
  var items: [Div]? { get }
  var itemBuilder: DivCollectionItemBuilder? { get }
}

extension DivGalleryProtocol {
  var nonNilItems: [Div] {
    items ?? []
  }

  func makeGalleryModel(
    context: DivBlockModelingContext,
    direction: ScrollDirection,
    alignment: Alignment,
    spacing: CGFloat,
    crossSpacing: CGFloat,
    defaultCrossAlignment: Alignment,
    scrollMode: GalleryViewModel.ScrollMode,
    columnCount: Int? = nil,
    infiniteScroll: Bool = false,
    bufferSize: Int = 1,
    scrollbar: GalleryViewModel.Scrollbar = .none,
    transformation: ElementsTransformation? = nil
  ) throws -> GalleryViewModel {
    let expressionResolver = context.expressionResolver
    var children: [GalleryViewModel.Item]
    let blockMapper: (Div, Block, DivBlockModelingContext) -> GalleryViewModel
      .Item = { div, block, _ in
        GalleryViewModel.Item(
          crossAlignment: (
            direction.isHorizontal
              ? div.value.resolveAlignmentVertical(expressionResolver)?.alignment
              : div.value.resolveAlignmentHorizontal(expressionResolver)?.alignment
          ) ?? defaultCrossAlignment,
          content: block
        )
      }

    if let itemBuilder {
      children = itemBuilder.makeBlocks(context: context, mappedBy: blockMapper)
    } else {
      children = nonNilItems.makeBlocks(
        context: context,
        sizeModifier: DivGallerySizeModifier(
          context: context,
          gallery: self,
          direction: direction
        ),
        mappedBy: blockMapper
      )
    }

    if infiniteScroll {
      let leadingBuffer = children[..<bufferSize]
      let trailingBuffer = children[(children.count - bufferSize)...]
      children = trailingBuffer + children + leadingBuffer
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
      path: context.path,
      alignment: alignment,
      direction: direction,
      bufferSize: bufferSize,
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
