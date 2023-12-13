import LayoutKit

struct DivGallerySizeModifier: DivSizeModifier {
  private static let wrapContentSize: DivSize =
    .divWrapContentSize(DivWrapContentSize(constrained: .value(true)))

  private static let matchParentSize: DivSize =
    .divMatchParentSize(DivMatchParentSize())

  private let shouldOverrideMatchParentWidth: Bool
  private let shouldOverrideMatchParentHeight: Bool

  init(
    context: DivBlockModelingContext,
    gallery: DivGalleryProtocol,
    direction: GalleryViewModel.Direction
  ) {
    let items = gallery.nonNilItems
    
    if direction == .vertical,
       gallery.getTransformedWidth(context).isIntrinsic,
       items.allHorizontallyMatchParent {
      context.addWarning(
        message: "All items in vertical \(gallery.typeName) with wrap_content width has match_parent width"
      )
      shouldOverrideMatchParentWidth = true
    } else {
      shouldOverrideMatchParentWidth = false
    }

    if direction == .horizontal,
       gallery.getTransformedHeight(context).isIntrinsic,
       items.allVerticallyMatchParent {
      context.addWarning(
        message: "All items in horizontal \(gallery.typeName) with wrap_content height has match_parent height"
      )
      shouldOverrideMatchParentHeight = true
    } else {
      shouldOverrideMatchParentHeight = false
    }
  }

  func transformWidth(_ width: DivSize) -> DivSize {
    if case let .divMatchParentSize(size) = width {
      if shouldOverrideMatchParentWidth {
        return Self.wrapContentSize
      }
      if size.weight != nil {
        return Self.matchParentSize
      }
    }
    return width
  }

  func transformHeight(_ height: DivSize) -> DivSize {
    if case let .divMatchParentSize(size) = height {
      if shouldOverrideMatchParentHeight {
        return Self.wrapContentSize
      }
      if size.weight != nil {
        return Self.matchParentSize
      }
    }
    return height
  }
}
