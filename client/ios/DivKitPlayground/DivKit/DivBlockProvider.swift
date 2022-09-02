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
    divKitComponents: DivKitComponents
  ) {
    self.divKitComponents = divKitComponents

    json.addObserver { [weak self] in self?.update(jsonProvider: $0) }
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
    divKitComponents.reset()
    
    do {
      let json = try jsonProvider()
      if json.isEmpty {
        return
      }
      
      let palette: [String: Any]? = try json.getOptionalField("palette")
      let paletteVariables = palette?.makePalette(themeName: "light") ?? [:]
      divKitComponents.variablesStorage
        .set(variables: paletteVariables, triggerUpdate: false)

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

extension Dictionary where Key == String, Value == Any {
  fileprivate func makePalette(themeName: String) -> DivVariables {
    guard let palette = try? self.getArray(themeName) as? [[String: String]] else {
      return [:]
    }

    var result: [String: Color] = [:]
    palette.forEach {
      guard let name = $0["name"],
            let colorStr = $0["color"],
            let color = Color.color(withHexString: colorStr) else { return }
      result[name] = color
    }
    return result.mapToDivVariables()
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
