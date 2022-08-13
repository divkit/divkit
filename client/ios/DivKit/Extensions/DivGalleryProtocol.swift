// Copyright 2022 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseUI
import CommonCore
import LayoutKit

protocol DivGalleryProtocol: DivBase {
  var items: [Div] { get }
}

extension DivGalleryProtocol {
  func makeGalleryModel(
    context: DivBlockModelingContext,
    direction: GalleryViewModel.Direction,
    spacing: CGFloat,
    defaultAlignment: Alignment,
    resizableInsets: InsetMode.Resizable?,
    scrollMode: GalleryViewModel.ScrollMode,
    columnCount: Int? = nil
  ) throws -> GalleryViewModel {
    let expressionResolver = context.expressionResolver
    let children: [GalleryViewModel.Item] = try items.makeBlocks(
      context: context,
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
      direction: direction,
      path: context.parentPath
    )

    let metrics = try makeMetrics(
      spacing: spacing,
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
      spacings: spacings
    )
  }

  private func checkLayoutConstraints(
    _ children: [GalleryViewModel.Item],
    direction: GalleryViewModel.Direction,
    path: UIElementPath
  ) throws {
    guard !children.isEmpty else {
      throw DivBlockModelingError(
        "\(typeName) has no items",
        path: path
      )
    }

    let blocks = children.map { $0.content }
    switch direction {
    case .horizontal:
      if height.isIntrinsic, blocks.allVerticallyResizable {
        throw DivBlockModelingError(
          "All items in horizontal \(typeName) with wrap_content height has match_parent height",
          path: path
        )
      }
    case .vertical:
      if width.isIntrinsic, blocks.allHorizontallyResizable {
        throw DivBlockModelingError(
          "All items in vertical \(typeName) with wrap_content width has match_parent width",
          path: path
        )
      }
    }
  }

  private var typeName: String {
    guard let typeName = String(describing: self).split(separator: ".").last else {
      return "DivGallery"
    }
    return String(typeName)
  }
}
