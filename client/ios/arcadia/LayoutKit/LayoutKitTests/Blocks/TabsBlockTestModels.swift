// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

import CommonCore
import LayoutKit

struct TabsBlockTestModels {
  static let path = UIElementPath("tabs")

  static let base = try! make(
    titles: ["A", "B"],
    blocks: [TextBlockTestModels.base, TextBlockTestModels.base]
  )

  static let gallery = try! make(
    titles: ["A"],
    blocks: [GalleryBlockTestModels.base]
  )

  private static func make(titles: [String], blocks: [Block]) throws -> TabsBlock {
    try TabsBlock(
      model: try TabViewModel(
        listModel: TabListViewModel(
          tabTitleLinks: titles.enumerated().map { UILink(text: $1, path: path + "title.\($0)") }
        ),
        contentsModel: try! TabContentsViewModel(
          pages: blocks.enumerated().map { $1.makeTabPage(with: path + "page.\($0)") },
          pagesHeight: .default,
          path: path
        )
      ),
      state: .default,
      widthTrait: .fixed(128),
      heightTrait: .fixed(128)
    )
  }
}
