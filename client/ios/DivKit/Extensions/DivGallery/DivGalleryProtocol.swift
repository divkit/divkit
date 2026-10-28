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
    allowTapWhileScroll: Bool = false,
    infiniteScroll: Bool = false,
    layoutMode: PagerBlock.LayoutMode? = nil,
    scrollbar: GalleryViewModel.Scrollbar = .none,
    transformation: ElementsTransformation? = nil,
    scrollAlignment: Alignment? = nil,
    onItem: (Div, Block, DivBlockModelingContext) -> Void = { _, _, _ in }
  ) throws -> GalleryViewModel {
    let expressionResolver = context.expressionResolver
    let sizeModifier = itemBuilder == nil
      ? DivGallerySizeModifier(context: context, gallery: self, direction: direction)
      : nil
    var children: [GalleryViewModel.Item]
    func blockMapper(
      _ div: Div,
      _ block: Block,
      _ itemContext: DivBlockModelingContext
    ) -> GalleryViewModel.Item {
      onItem(div, block, itemContext)
      return GalleryViewModel.Item(
        crossAlignment: (
          direction.isHorizontal
            ? div.value.resolveAlignmentVertical(expressionResolver)?.alignment
            : div.value.resolveAlignmentHorizontal(expressionResolver)?
            .alignment(isRTLLayout: context.layoutDirection == .rightToLeft)
        ) ?? defaultCrossAlignment,
        content: block,
        fillsCrossAxis: sizeModifier?.fillsCrossAxis(div) == true
      )
    }

    children = if let itemBuilder {
      itemBuilder.makeBlocks(context: context, mappedBy: blockMapper)
    } else {
      nonNilItems.makeBlocks(
        context: context,
        sizeModifier: sizeModifier,
        mappedBy: blockMapper
      )
    }

    let existingChildren = infiniteScroll
      ? children.filter { !$0.content.isEmpty }
      : children
    let itemsCount = existingChildren.count
    let bufferSize = layoutMode.map { min($0.bufferSize(itemsCount: itemsCount), itemsCount) } ?? 1

    if infiniteScroll, itemsCount > 0 {
      let leadingBuffer = existingChildren[..<bufferSize]
      let trailingBuffer = existingChildren[(existingChildren.count - bufferSize)...]
      children = trailingBuffer + children + leadingBuffer
    }

    let metrics = try makeMetrics(
      spacing: spacing,
      crossSpacing: crossSpacing,
      childrenCount: children.count,
      direction: direction,
      context: context
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
      allowTapWhileScroll: allowTapWhileScroll,
      infiniteScroll: infiniteScroll,
      scrollbar: scrollbar,
      transformation: transformation,
      scrollAlignment: scrollAlignment
    )
  }

  private func makeMetrics(
    spacing: CGFloat,
    crossSpacing: CGFloat,
    childrenCount: Int,
    direction: ScrollDirection,
    context: DivBlockModelingContext
  ) throws -> GalleryViewMetrics {
    let spacings = [CGFloat](
      repeating: spacing,
      times: UInt(max(0, childrenCount - 1))
    )

    let axialInsets: SideInsets
    let crossInsets: SideInsets
    let insets = paddings.resolve(context)
    let horizontalInsets = context.layoutDirection == .rightToLeft
      ? SideInsets(leading: insets.right, trailing: insets.left)
      : insets.horizontal
    switch direction {
    case .horizontal:
      axialInsets = horizontalInsets
      crossInsets = insets.vertical
    case .vertical:
      axialInsets = insets.vertical
      crossInsets = horizontalInsets
    }

    return GalleryViewMetrics(
      axialInsetMode: .fixed(values: axialInsets),
      crossInsetMode: .fixed(values: crossInsets),
      spacings: spacings,
      crossSpacing: crossSpacing
    )
  }
}

extension PagerBlock.LayoutMode {
  fileprivate func bufferSize(itemsCount: Int) -> Int {
    switch self {
    case let .pageSize(relativeValue):
      Int(1 / relativeValue.rawValue) + 1
    case .neighbourPageSize:
      2
    case .pageContentSize:
      itemsCount
    }
  }
}
