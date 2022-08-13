// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

import CommonCore
import LayoutKit

struct GalleryBlockTestModels {
  static let path = UIElementPath("gallery")

  static let base = make(blocks: [TextBlockTestModels.base, TextBlockTestModels.base])

  static let tabs = make(blocks: [TabsBlockTestModels.base])

  private static func make(blocks: [Block]) -> GalleryBlock {
    try! GalleryBlock(
      model: GalleryViewModel(
        blocks: blocks,
        metrics: GalleryViewMetrics(gaps: Array(
          repeating: 0,
          times: try! UInt(value: blocks.count + 1)
        )),
        path: path
      ),
      state: .default,
      widthTrait: .fixed(128),
      heightTrait: .fixed(128)
    )
  }
}
