import Foundation
import LayoutKit
import VGSL

typealias ImageBlockTestModels = BlockTestsModels.Image
typealias TextBlockTestModels = BlockTestsModels.Text
typealias ContainerBlockTestModels = BlockTestsModels.Container
typealias GalleryBlockTestModels = BlockTestsModels.Gallery
typealias TabsBlockTestModels = BlockTestsModels.Tabs
typealias SwitchableContainerBlockTestModels = BlockTestsModels.SwitchableContainer

enum BlockTestsModels {
  enum Text {
    static let base = TextBlock(widthTrait: .fixed(128), text: text)
    static let text = "Северная Ирландия".with(typo: typo)
    static let typo = Typo(size: .textM, weight: .regular).with(height: .textM).kerned(.textM)
    static let intrinsicTextSize = text.sizeForWidth(.infinity)
  }

  enum Container {
    static let threeGaps: [CGFloat] = [10, 20, 30]
    static let threeGapsSize = threeGaps.reduce(0, +)
  }

  enum Gallery {
    static let path = UIElementPath("gallery")

    static let base = make(blocks: [TextBlockTestModels.base, TextBlockTestModels.base])

    static func make(blocks: [Block]) -> GalleryBlock {
      try! GalleryBlock(
        model: GalleryViewModel(
          blocks: blocks,
          metrics: GalleryViewMetrics(gaps: Array(
            repeating: 0,
            times: try! UInt(value: blocks.count + 1)
          )),
          path: path
        ),
        state: GalleryViewState(contentOffset: 0, itemsCount: 2),
        widthTrait: .fixed(128),
        heightTrait: .fixed(128)
      )
    }
  }

  enum Tabs {
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
        model: TabViewModel(
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

  enum Image {
    static let imageSize = CGSize(width: 80, height: 60)
    static let image = VGSL.Image.imageWithSolidColor(.black, size: imageSize)!
  }

  enum SwitchableContainer {
    static let path = UIElementPath("switchableContainerBlock")

    static let base = SwitchableContainerBlock(
      selectedItem: .left,
      items: (
        .init(
          title: .init(text: "A", selectedTypo: Typo(), deselectedTypo: Typo()),
          content: GalleryBlockTestModels.base
        ),
        .init(
          title: .init(text: "A", selectedTypo: Typo(), deselectedTypo: Typo()),
          content: GalleryBlockTestModels.base
        )
      ),
      backgroundColor: .clear,
      selectedBackgroundColor: .clear,
      titleGaps: 0,
      titleContentGap: 0,
      selectorSideGaps: 0,
      switchAction: nil,
      path: path
    )
  }

  static func block(withSize size: CGSize) -> Block {
    try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .fixed(size.width),
      heightTrait: .fixed(size.height),
      children: [TextBlock(widthTrait: .resizable, text: "".with(typo: Typo()))]
    )
  }
}
