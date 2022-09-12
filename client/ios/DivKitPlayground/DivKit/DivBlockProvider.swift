import UIKit

import BaseUI
import CommonCore
import DivKit
import LayoutKit
import Serialization

typealias JsonProvider = () throws -> [String: Any]

struct ObservableJsonProvider {
  @ObservableProperty
  private var provider: JsonProvider = { [:] }

  var signal: Signal<JsonProvider> {
    $provider.currentAndNewValues
  }

  func load(url: URL) {
    provider = {
      try Data(contentsOf: url).asJsonDictionary()
    }
  }
}

extension Data {
  func asJsonDictionary() throws -> [String: Any] {
    return try JSONSerialization.jsonObject(with: self, options: []) as! [String: Any]
  }
}

final class DivBlockProvider {
  private let divKitComponents: DivKitComponents
  private let disposePool = AutodisposePool()

  private var divData: DivData?
  public weak var parentScrollView: ScrollView?

  @ObservableProperty
  private(set) var block: Block = noDataBlock
  @ObservableProperty
  private(set) var errors: UIStatePayloadFactory.Errors? = nil

  init(
    json: Signal<JsonProvider>,
    divKitComponents: DivKitComponents,
    shouldResetOnDataChange: Bool
  ) {
    self.divKitComponents = divKitComponents

    json
      .addObserver { [weak self] in
        if shouldResetOnDataChange {
          self?.divKitComponents.reset()
        }
        self?.update(jsonProvider: $0)
      }
      .dispose(in: disposePool)
  }

  func update(patch: DivPatch? = nil) {
    guard var divData = divData else {
      return
    }

    if let patch = patch {
      divData = divData.applyPatch(patch)
      self.divData = divData
    }
    
    do {
      block = try divData.makeBlock(
        context: divKitComponents.makeContext(
          cardId: cardId,
          cachedImageHolders: block.getImageHolders(),
          parentScrollView: parentScrollView
        )
      )
    } catch {
      block = makeErrorBlock("\(error)")
      errors = [(message: error.localizedDescription, stack: nil)]
      DemoAppLogger.error("Failed to build block: \(error)")
    }
  }

  private func update(jsonProvider: JsonProvider) {
    divData = nil
    block = noDataBlock
    errors = []
    
    do {
      let json = try jsonProvider()
      if json.isEmpty {
        return
      }
      
      let palette = Palette(json: try json.getOptionalField("palette") ?? [:])
      divKitComponents.variablesStorage
        .set(variables: palette.makeVariables(theme: UserPreferences.playgroundTheme), triggerUpdate: false)

      let result = try divKitComponents.parseDivDataWithTemplates(json, cardId: cardId)
      divData = result.value
      errors = result.errorsOrWarnings.map {
        $0.map {
          let traceback = $0.traceback
          return (message: traceback.last, stack: traceback.asArray())
        }
      }
    } catch {
      block = makeErrorBlock("\(error)")
      errors = [(message: error.localizedDescription, stack: nil)]
      DemoAppLogger.error("Failed to parse DivData: \(error)")
      return
    }

    update(patch: nil)
  }
}

private let cardId: DivCardID = "sample_card"

private let noDataBlock = SeparatorBlock()

private func makeErrorBlock(_ text: String) -> Block {
  TextBlock(
    widthTrait: .resizable,
    text: text.with(typo: Typo(size: 18, weight: .regular))
  ).addingEdgeGaps(20)
}
