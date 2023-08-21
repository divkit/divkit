import Foundation
import UIKit

import BaseUIPublic
import LayoutKit

final class ErrorListView: UIView {
  private let errorView: UIView
  private let errorString: String

  init(errors: [String]) {
    errorString = errors.joined(separator: "\n\n")
    let block = makeErrorBlock(errors: errors)
    self.errorView = block.makeBlockView()
    let blockFrame = CGRect(origin: .zero, size: block.intrinsicSize)
    super.init(frame: blockFrame)
    self.frame = blockFrame
    addSubview(errorView)
  }

  public override func layoutSubviews() {
    self.errorView.frame = self.bounds.inset(by: safeAreaInsets)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  private func copyToPasteboard() {
    UIPasteboard.general.string = errorString
  }
}

extension ErrorListView: UIActionEventPerforming {
  func perform(uiActionEvent event: LayoutKit.UIActionEvent, from _: AnyObject) {
    guard case let .url(url) = event.payload else {
      event.sendFrom(self)
      return
    }
    if url == copyActionURL {
      copyToPasteboard()
    } else if url == closeActionURL {
      removeFromSuperview()
    }
  }
}

private func makeErrorBlock(errors: [String]) -> Block {
  try! ContainerBlock(
    layoutDirection: .vertical,
    widthTrait: .intrinsic,
    heightTrait: .intrinsic,
    children: [
      .init(content: makeHeaderBlock(errorsCount: errors.count)),
      .init(content: makeErrorListBlock(errors: errors)),
    ]
  ).addingHorizontalGaps(15)
    .addingVerticalGaps(top: 0, bottom: 15)
    .addingDecorations(backgroundColor: .init(white: 0.8, alpha: 1))
    .addingDecorations(border: .init(color: .black, width: 3))
}

private func makeCopyButtonBlock() -> Block {
  let copyText = "Copy".with(typo: headerTypo)

  let copyTextBlock = TextBlock(
    widthTrait: .intrinsic,
    heightTrait: .fixed(headerHeight),
    text: copyText,
    verticalAlignment: .center,
    maxIntrinsicNumberOfLines: 1
  ).addingDecorations(
    action: UserInterfaceAction(url: copyActionURL, path: rootPath)
  )
  return copyTextBlock
}

private func makeCloseButtonBlock() -> Block {
  TextBlock(
    widthTrait: .intrinsic,
    heightTrait: .fixed(headerHeight),
    text: "Close".with(typo: headerTypo),
    verticalAlignment: .center,
    maxIntrinsicNumberOfLines: 1
  ).addingDecorations(
    action: UserInterfaceAction(url: closeActionURL, path: rootPath)
  )
}

private func makeTitleBlock(errorsCount: Int) -> Block {
  let titleText = ("\(errorsCount) " + (errorsCount == 1 ? "error" : "errors"))
    .with(typo: headerTypo)

  let errorsBlock = TextBlock(
    widthTrait: .intrinsic,
    heightTrait: .fixed(headerHeight),
    text: titleText,
    verticalAlignment: .center,
    maxIntrinsicNumberOfLines: 1
  )

  return try! ContainerBlock(
    layoutDirection: .horizontal,
    widthTrait: .intrinsic,
    heightTrait: .fixed(headerHeight),
    axialAlignment: .center,
    children: [.init(content: errorsBlock)]
  )
}

private func makeHeaderBlock(errorsCount: Int) -> Block {
  try! ContainerBlock(
    layoutDirection: .horizontal,
    widthTrait: .intrinsic,
    heightTrait: .fixed(headerHeight),
    axialAlignment: .leading,
    gaps: [0, 20, 20, 0],
    children: [
      .init(content: makeCloseButtonBlock()),
      .init(content: makeCopyButtonBlock()),
      .init(content: makeTitleBlock(errorsCount: errorsCount)),
    ]
  )
}

private func makeErrorListBlock(errors: [String]) -> Block {
  let errorBlocks = errors.map {
    TextBlock(
      widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: 350),
      heightTrait: .intrinsic,
      text: $0.with(typo: errorsTypo),
      verticalAlignment: .leading,
      minNumberOfHiddenLines: 0
    )
  }

  return try! GalleryBlock(
    gaps: [0] + Array.init(repeating: 10, count: errorBlocks.count),
    children: errorBlocks,
    path: rootPath,
    direction: .vertical,
    crossAlignment: .leading,
    widthTrait: .intrinsic,
    heightTrait: .intrinsic(constrained: true, minSize: 0, maxSize: 500)
  ).addingDecorations(forceWrapping: true)
}

private let closeActionURL = URL(string: "errorList://close")!
private let copyActionURL = URL(string: "errorList://copy")!
private let rootPath: UIElementPath = "ErrorList"

private let headerHeight: CGFloat = 56
private let headerTypo = Typo(size: .textL, weight: .medium).with(height: .textL)
private let errorsTypo = Typo(size: .textM, weight: .regular)
  .with(height: FontLineHeight(rawValue: 18))
