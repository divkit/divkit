import XCTest

import BaseUIPublic
import CommonCorePublic
import LayoutKit

final class SwitchableContainerSnapshotTest: XCTestCase {
  func test_LeftSideSelected() {
    performTest(selection: .left, mode: mode)
  }

  func test_RightSideSelected() {
    performTest(selection: .right, mode: mode)
  }

  func test_LongTitles() {
    performTest(
      leftTitle: leftSideLongTitle,
      rightTitle: rightSideLongTitle,
      selection: .left,
      mode: mode
    )
  }

  func test_LeftShortAndRightLongTitle() {
    performTest(
      leftTitle: leftSideTitle,
      rightTitle: rightSideLongTitle,
      selection: .left,
      mode: mode
    )
  }

  // NOTE: actually content always work as resizable, so this test is needed to illustrate such deficiency
  func test_FixedContentSizes() {
    performTest(
      leftContent: makeContentBlock(
        text: leftSideContent,
        widthTrait: .fixed(0.5 * blockViewSize.width),
        heightTrait: .resizable
      ),
      rightContent: makeContentBlock(
        text: rightSideContent,
        widthTrait: .fixed(0.5 * blockViewSize.width),
        heightTrait: .resizable
      ),
      mode: mode
    )
  }

  // NOTE: actually content always work as resizable, so this test is needed to illustrate such deficiency
  func test_IntrinsicContentSizes() {
    performTest(
      leftContent: makeContentBlock(
        text: leftSideContent,
        widthTrait: .intrinsic,
        heightTrait: .resizable
      ),
      rightContent: makeContentBlock(
        text: rightSideContent,
        widthTrait: .intrinsic,
        heightTrait: .resizable
      ),
      mode: mode
    )
  }

  private func performTest(
    leftContent: Block = makeContentBlock(text: leftSideContent),
    rightContent: Block = makeContentBlock(text: rightSideContent),
    leftTitle: String = leftSideTitle,
    rightTitle: String = rightSideTitle,
    selection: SwitchableContainerBlock.Selection = .left,
    name: String = #function,
    mode: TestMode
  ) {
    let baseTypo = Typo(size: 14, weight: .regular).centered
    let selectedTypo = baseTypo.with(color: .black)
    let deselectedTypo = baseTypo.with(color: .white)
    let block = SwitchableContainerBlock(
      selectedItem: selection,
      items: (
        SwitchableContainerBlock.Item(
          title: SwitchableContainerBlock.Title(
            text: leftTitle,
            selectedTypo: selectedTypo,
            deselectedTypo: deselectedTypo
          ),
          content: leftContent
        ),
        SwitchableContainerBlock.Item(
          title: SwitchableContainerBlock.Title(
            text: rightTitle,
            selectedTypo: selectedTypo,
            deselectedTypo: deselectedTypo
          ),
          content: rightContent
        )
      ),
      backgroundColor: .black,
      selectedBackgroundColor: .white,
      titleGaps: 5,
      titleContentGap: 16,
      selectorSideGaps: 12,
      switchAction: nil,
      path: UIElementPath("R")
    )

    LayoutKitSnapshotTest.perform(on: block, size: blockViewSize, name: name, mode: mode)
  }
}

private func makeContentBlock(
  text: String,
  widthTrait: LayoutTrait = .resizable,
  heightTrait: LayoutTrait = .resizable
) -> Block {
  let textBlock = TextBlock(
    widthTrait: widthTrait,
    heightTrait: heightTrait,
    text: text.with(typo: textTypo),
    maxIntrinsicNumberOfLines: 0
  )
  return try! ContainerBlock(
    layoutDirection: .vertical,
    widthTrait: widthTrait,
    heightTrait: heightTrait,
    horizontalChildrenAlignment: .center,
    verticalChildrenAlignment: .center,
    children: [textBlock]
  ).addingDecorations(backgroundColor: Color(red: 0, green: 0, blue: 1, alpha: 0.5))
}

private let leftSideTitle = "Левая сторона"
private let leftSideLongTitle = "Очень очень очень длинное название левой стороны"
private let rightSideTitle = "Правая сторона"
private let rightSideLongTitle = "Очень очень очень длинное название правой стороны"
private let leftSideContent = "Вас приветствует программа спокойной ночи малыши! Спокойной ночи!"
private let rightSideContent =
  "Вас приветствует программа утренняя зарядка! Сели-встали-сели-встали!"

private let blockViewSize = CGSize(width: 320, height: 240)
private let textTypo = Typo(size: 18, weight: .medium).centered

private let mode = TestMode.verify
