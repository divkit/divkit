import CoreGraphics

import CommonCorePublic

extension AnchorBlock.Layout {
  init(
    size: CGSize,
    direction: ContainerBlock.LayoutDirection,
    crossAlignment: Alignment,
    leading: Block?,
    center: Block,
    trailing: Block?
  ) {
    let leadingSize = leading?.size(forResizableBlockSize: size) ?? .zero
    let trailingSize = trailing?.size(forResizableBlockSize: size) ?? .zero
    let freeSize: CGSize
    switch direction {
    case .horizontal:
      freeSize = CGSize(
        width: max(0, size.width - leadingSize.width - trailingSize.width),
        height: size.height
      )
    case .vertical:
      freeSize = CGSize(
        width: size.width,
        height: max(0, size.height - leadingSize.height - trailingSize.height)
      )
    }
    let desiredCenterSize = center.size(forResizableBlockSize: freeSize)
    let centerSize = CGSize(
      width: min(desiredCenterSize.width, freeSize.width),
      height: min(desiredCenterSize.height, freeSize.height)
    )
    func makeCrossOffset(contentSize: CGFloat) -> CGFloat {
      let crossDimension: CGFloat
      switch direction {
      case .horizontal: crossDimension = size.height
      case .vertical: crossDimension = size.width
      }
      return crossAlignment.offset(
        forAvailableSpace: crossDimension,
        contentSize: contentSize
      )
    }
    let leadingOrigin: CGPoint
    let centerOrigin: CGPoint
    let trailingOrigin: CGPoint
    switch direction {
    case .horizontal:
      leadingOrigin = CGPoint(
        x: 0,
        y: makeCrossOffset(contentSize: leadingSize.height)
      )
      centerOrigin = CGPoint(
        x: offset(
          for: centerSize.width,
          containerSize: size.width,
          leadingPadding: leadingSize.width,
          trailingPadding: trailingSize.width
        ),
        y: makeCrossOffset(contentSize: centerSize.height)
      )
      trailingOrigin = CGPoint(
        x: size.width - trailingSize.width,
        y: makeCrossOffset(contentSize: trailingSize.height)
      )
    case .vertical:
      leadingOrigin = CGPoint(
        x: makeCrossOffset(contentSize: leadingSize.width),
        y: 0
      )
      centerOrigin = CGPoint(
        x: makeCrossOffset(contentSize: centerSize.width),
        y: offset(
          for: centerSize.height,
          containerSize: size.height,
          leadingPadding: leadingSize.height,
          trailingPadding: trailingSize.height
        )
      )
      trailingOrigin = CGPoint(
        x: makeCrossOffset(contentSize: trailingSize.width),
        y: size.height - trailingSize.height
      )
    }
    self.init(
      leadingFrame: CGRect(origin: leadingOrigin, size: leadingSize),
      centerFrame: CGRect(origin: centerOrigin, size: centerSize),
      trailingFrame: CGRect(origin: trailingOrigin, size: trailingSize)
    )
  }
}

private func offset(
  for size: CGFloat,
  containerSize: CGFloat,
  leadingPadding: CGFloat,
  trailingPadding: CGFloat
) -> CGFloat {
  let half = containerSize / 2
  let leadingFits = half - leadingPadding >= size / 2
  let trailingFits = half - trailingPadding >= size / 2
  if leadingFits, trailingFits {
    return ((containerSize - size) / 2).roundedToScreenScale
  } else if trailingFits {
    return leadingPadding
  } else {
    return containerSize - size - trailingPadding
  }
}
