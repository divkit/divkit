import UIKit

import BaseUI
import CommonCore
import DivKit
import LayoutKit
import Serialization

final class DivBlockProvider {
  private let divKitComponents: DivKitComponents
  private let disposePool = AutodisposePool()

  private var divData: DivData?
  public weak var parentScrollView: ScrollView?

  let block: ObservableProperty<Block> = ObservableProperty(initialValue: noDataBlock)

  init(
    jsonDataProvider: Signal<DataProvider.Factory>,
    divKitComponents: DivKitComponents
  ) {
    self.divKitComponents = divKitComponents

    jsonDataProvider
      .addObserver(update)
      .dispose(in: disposePool)
  }

  func update(patch: DivPatch?) {
    guard var divData = divData else {
      return
    }

    if let patch = patch {
      divData = divData.applyPatch(patch)
      self.divData = divData
    }

    do {
      block.value = try divData.makeBlock(
        context: divKitComponents.makeContext(
          cardId: cardId,
          cachedImageHolders: block.value.getImageHolders(),
          parentScrollView: parentScrollView
        )
      )
    } catch {
      block.value = makeFallbackBlock("\(error)")
    }
  }

  private func update(jsonData: DataProvider.Factory) {
    divData = nil
    divKitComponents.reset()

    do {
      guard let jsonData = try jsonData() else {
        block.value = noDataBlock
        return
      }

      divData = try divKitComponents.parseDivData(jsonData, cardId: cardId)
    } catch {
      block.value = makeFallbackBlock("\(error)")
      return
    }

    update(patch: nil)
  }
}

private let cardId: DivCardID = "sample_card"

private let noDataBlock = makeFallbackBlock("No data")

private func makeFallbackBlock(_ text: String) -> Block {
  TextBlock(
    widthTrait: .resizable,
    text: text.with(typo: Typo(size: 16, weight: .medium))
  ).addingHorizontalGaps(8)
}
