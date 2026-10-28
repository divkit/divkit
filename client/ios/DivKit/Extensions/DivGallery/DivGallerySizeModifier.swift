import LayoutKit

/// Rewrites a match_parent item size across the scroll axis of a gallery that wraps its content
/// across the scroll axis to constrained wrap_content with the same min/max, but only when all
/// items are match_parent across the scroll axis. Such an item takes part in the gallery cross
/// size and then fills it (see `GalleryViewModel.Item.fillsCrossAxis`).
///
/// This is the Android rule: items are measured by their content only if all of them are
/// match_parent, otherwise a match_parent item does not affect the cross size of the gallery.
struct DivGallerySizeModifier: DivSizeModifier {
  let shouldOverrideMatchParentWidth: Bool
  let shouldOverrideMatchParentHeight: Bool

  init(
    context: DivBlockModelingContext,
    gallery: DivGalleryProtocol,
    direction: ScrollDirection
  ) {
    let items = gallery.nonNilItems
    shouldOverrideMatchParentWidth = direction == .vertical
      && gallery.getTransformedWidth(context).isIntrinsic
      && items.allHorizontallyMatchParent
    shouldOverrideMatchParentHeight = direction == .horizontal
      && gallery.getTransformedHeight(context).isIntrinsic
      && items.allVerticallyMatchParent
  }

  func fillsCrossAxis(_ div: Div) -> Bool {
    shouldOverrideMatchParentWidth && div.isHorizontallyMatchParent
      || shouldOverrideMatchParentHeight && div.isVerticallyMatchParent
  }

  func transformWidth(_ width: DivSize) -> DivSize {
    transform(width, shouldOverrideMatchParent: shouldOverrideMatchParentWidth)
  }

  func transformHeight(_ height: DivSize) -> DivSize {
    transform(height, shouldOverrideMatchParent: shouldOverrideMatchParentHeight)
  }

  private func transform(_ size: DivSize, shouldOverrideMatchParent: Bool) -> DivSize {
    guard case let .divMatchParentSize(matchParentSize) = size else {
      return size
    }
    if shouldOverrideMatchParent {
      return .divWrapContentSize(DivWrapContentSize(
        constrained: .value(true),
        maxSize: matchParentSize.maxSize,
        minSize: matchParentSize.minSize
      ))
    }
    if matchParentSize.weight != nil {
      return .divMatchParentSize(DivMatchParentSize(
        maxSize: matchParentSize.maxSize,
        minSize: matchParentSize.minSize
      ))
    }
    return size
  }
}
