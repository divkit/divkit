import Foundation
import UIKit

import BaseUIPublic
import LayoutKit

final class ErrorListView: UIView {
  private let listView: UIView
  private let errorString: String

  init(errors: [String]) {
    errorString = errors.joined(separator: "\n")
    self.listView = makeErrorListView(errorsString: errorString, errorsCount: errors.count)
    super.init(frame: .zero)

    backgroundColor = .white
    addSubview(listView)
  }

  public override func layoutSubviews() {
    self.listView.frame = self.bounds.inset(by: safeAreaInsets)
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

private func makeErrorListView(errorsString: String, errorsCount: Int) -> UIView {
  try! ContainerBlock(
    layoutDirection: .vertical,
    widthTrait: .resizable,
    heightTrait: .resizable,
    children: [
      .init(content: makeHeaderBlock(errorsCount: errorsCount)),
      .init(content: makeErrorListBlock(errorString: errorsString)),
    ]
  ).addingDecorations(backgroundColor: .white).makeBlockView()
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
    widthTrait: .resizable,
    heightTrait: .fixed(headerHeight),
    axialAlignment: .center,
    children: [.init(content: errorsBlock)]
  )
}

private func makeHeaderBlock(errorsCount: Int) -> Block {
  try! ContainerBlock(
    layoutDirection: .horizontal,
    widthTrait: .resizable,
    heightTrait: .fixed(headerHeight),
    axialAlignment: .leading,
    gaps: [16, 20, 20, 16],
    children: [
      .init(content: makeCloseButtonBlock()),
      .init(content: makeCopyButtonBlock()),
      .init(content: makeTitleBlock(errorsCount: errorsCount)),
    ]
  )
}

private func makeErrorListBlock(errorString: String) -> Block {
  let textBlock = TextBlock(
    widthTrait: .resizable,
    heightTrait: .intrinsic,
    text: errorString.with(typo: errorsTypo),
    verticalAlignment: .leading,
    minNumberOfHiddenLines: 0
  )

  return try! GalleryBlock(
    gaps: [.zero, .zero],
    children: [textBlock],
    path: rootPath,
    direction: .vertical,
    crossAlignment: .leading,
    widthTrait: .resizable,
    heightTrait: .resizable
  ).addingEdgeInsets(insets, clipsToBounds: true, forceWrapping: true)
}

private let closeActionURL = URL(string: "errorList://close")!
private let copyActionURL = URL(string: "errorList://copy")!
private let rootPath: UIElementPath = "ErrorList"

private let insets = EdgeInsets(vertical: 0, horizontal: 16)
private let headerHeight: CGFloat = 56
private let headerTypo = Typo(size: .textL, weight: .medium).with(height: .textL)
private let errorsTypo = Typo(size: .textM, weight: .regular)
  .with(height: FontLineHeight(rawValue: 18))
