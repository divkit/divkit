// Copyright 2018 Yandex LLC. All rights reserved.

import CoreGraphics

public struct ImageLayerLayout {
  public let frame: CGRect
  public let contentRect: CGRect
  public let contentCenter: CGRect

  public init(
    contentMode: ImageContentMode,
    contentSize: CGSize,
    boundsSize: CGSize,
    capInsets: EdgeInsets
  ) {
    switch contentMode.scale {
    case .noScale:
      // layout will be done by system due to contentsGravity
      frame = CGRect(origin: .zero, size: boundsSize)
      contentRect = defaultContentRect
      contentCenter = defaultContentCenter
    case .aspectFill:
      frame = makeAspectFillFrame(
        contentMode: contentMode,
        contentSize: contentSize,
        bounds: boundsSize
      )
      contentRect = defaultContentRect
      contentCenter = makeContentCenter(for: contentSize, capInsets: capInsets)
    case .aspectFit:
      frame = makeAspectFitFrame(
        contentMode: contentMode,
        contentSize: contentSize,
        bounds: boundsSize
      )
      contentRect = defaultContentRect
      contentCenter = makeContentCenter(for: contentSize, capInsets: capInsets)
    case .aspectWidth:
      assert(
        contentMode.verticalAlignment == .top,
        "Aspect width scale supports only top alignment"
      )
      frame = CGRect(origin: .zero, size: boundsSize)
      contentRect = makeTopContentRect(for: contentSize, bounds: boundsSize)
      contentCenter = makeContentCenter(for: contentSize, capInsets: capInsets)
    case .resize:
      frame = CGRect(origin: .zero, size: boundsSize)
      contentRect = defaultContentRect
      contentCenter = makeContentCenter(for: contentSize, capInsets: capInsets)
    }
  }
}

private let defaultContentRect = CGRect(x: 0, y: 0, width: 1, height: 1)

private func makeTopContentRect(for contentSize: CGSize, bounds: CGSize) -> CGRect {
  guard contentSize.height > 0, contentSize.width > 0, bounds.width > 0 else { return .zero }
  let scaleByWidth = bounds.width / contentSize.width
  let scaledContentHeight = contentSize.height * scaleByWidth
  let normalizedVisibleContentHeight = bounds.height / scaledContentHeight
  return CGRect(x: 0, y: 0, width: 1, height: normalizedVisibleContentHeight)
}

private let defaultContentCenter = CGRect(x: 0, y: 0, width: 1, height: 1)

private func makeContentCenter(for contentSize: CGSize, capInsets: EdgeInsets) -> CGRect {
  guard contentSize.width > 0, contentSize.height > 0 else {
    return CGRect(x: 0, y: 0, width: 1, height: 1)
  }

  return CGRect(origin: .zero, size: contentSize)
    .inset(by: capInsets)
    .scaled(x: 1 / contentSize.width, y: 1 / contentSize.height)
}

private func makeAspectFillFrame(
  contentMode: ImageContentMode,
  contentSize: CGSize,
  bounds: CGSize
) -> CGRect {
  guard let size = contentSize.sizeToFill(size: bounds) else {
    return .zero
  }

  let origin = makeOrigin(
    contentMode: contentMode,
    contentSize: size,
    bounds: bounds
  )

  return CGRect(origin: origin, size: size)
}

private func makeAspectFitFrame(
  contentMode: ImageContentMode,
  contentSize: CGSize,
  bounds: CGSize
) -> CGRect {
  guard let size = contentSize.sizeToFit(size: bounds) else {
    return .zero
  }

  let origin = makeOrigin(
    contentMode: contentMode,
    contentSize: size,
    bounds: bounds
  )

  return CGRect(origin: origin, size: size)
}

private func makeOrigin(
  contentMode: ImageContentMode,
  contentSize: CGSize,
  bounds: CGSize
) -> CGPoint {
  let x: CGFloat
  switch contentMode.horizontalAlignment {
  case .left:
    x = 0
  case .center:
    x = (bounds.width - contentSize.width) / 2
  case .right:
    x = bounds.width - contentSize.width
  }

  let y: CGFloat
  switch contentMode.verticalAlignment {
  case .top:
    y = 0
  case .center:
    y = (bounds.height - contentSize.height) / 2
  case .bottom:
    y = bounds.height - contentSize.height
  }

  return CGPoint(x: x, y: y)
}
