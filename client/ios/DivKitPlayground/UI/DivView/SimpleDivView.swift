import SwiftUI
import UIKit

import BaseUIPublic
import CommonCorePublic
import DivKit
import LayoutKit

struct SimpleDivView: UIViewRepresentable {
  let cardId: DivCardID
  let jsonData: Data?
  let divKitComponents: DivKitComponents

  func makeUIView(context _: Context) -> UIView {
    SimpleDivUIView(
      cardId: cardId,
      jsonData: jsonData,
      divKitComponents: divKitComponents
    )
  }

  func updateUIView(_: UIView, context _: Context) {}
}

private final class SimpleDivUIView: UIView {
  private let cardId: DivCardID
  private let jsonData: Data?
  private let divKitComponents: DivKitComponents

  private var divData: DivData?
  private var block: Block = noDataBlock

  private var blockView: BlockView! {
    didSet {
      if blockView !== oldValue {
        oldValue?.removeFromSuperview()
        addSubview(blockView)
      }
    }
  }

  init(
    cardId: DivCardID,
    jsonData: Data?,
    divKitComponents: DivKitComponents
  ) {
    self.cardId = cardId
    self.jsonData = jsonData
    self.divKitComponents = divKitComponents

    super.init(frame: .zero)

    if let jsonData = jsonData {
      do {
        divData = try divKitComponents
          .parseDivDataWithTemplates(jsonData, cardId: cardId)
          .unwrap()
      } catch {
        block = makeErrorBlock("\(error)")
      }
    }

    update()
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    let blockSize = block.size(forResizableBlockSize: bounds.size)
    blockView.frame = CGRect(origin: .zero, size: blockSize)

    if bounds.size != blockSize {
      invalidateIntrinsicContentSize()
    }
  }

  override var intrinsicContentSize: CGSize {
    block.size(forResizableBlockSize: bounds.size)
  }

  private func update() {
    if let divData = divData {
      do {
        block = try divData.makeBlock(
          context: divKitComponents.makeContext(
            cardId: cardId,
            cachedImageHolders: block.getImageHolders(),
            debugParams: AppComponents.debugParams
          )
        )
      } catch {
        block = makeErrorBlock("\(error)")
      }
    }

    let elementStateObserver = divKitComponents.blockStateStorage
    if let blockView = blockView, block.canConfigureBlockView(blockView) {
      block.configureBlockView(
        blockView,
        observer: elementStateObserver,
        overscrollDelegate: nil,
        renderingDelegate: nil
      )
    } else {
      blockView = block.makeBlockView(observer: elementStateObserver)
    }

    setNeedsLayout()
  }
}

extension SimpleDivUIView: UIActionEventPerforming {
  func perform(uiActionEvent event: UIActionEvent, from sender: AnyObject) {
    perform(uiActionEvents: [event], from: sender)
  }

  func perform(uiActionEvents events: [UIActionEvent], from _: AnyObject) {
    events.map { $0.payload }.forEach { handle($0) }
    update()
  }

  private func handle(_ payload: UserInterfaceAction.Payload) {
    switch payload {
    case let .divAction(params):
      divKitComponents.handleActions(params: params)
    case .composite, .empty, .json, .menu, .url:
      break
    }
  }
}

private let noDataBlock = EmptyBlock.zeroSized

private func makeErrorBlock(_ text: String) -> Block {
  TextBlock(
    widthTrait: .resizable,
    text: text.with(typo: Typo(size: 18, weight: .regular))
  ).addingEdgeGaps(20)
}
