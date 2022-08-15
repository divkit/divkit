import BaseUI
import CommonCore
import Serialization

@_implementationOnly import DivKit
@_implementationOnly import LayoutKit

final class BlockProvider {
  private let urlOpener: UrlOpener
  private let disposePool = AutodisposePool()
  private var divKitComponents: DivKitComponents!

  private var json: [String: Any]? {
    didSet {
      update()
    }
  }

  @ObservableProperty
  private(set) var block: Block? = nil
  @ObservableProperty
  private(set) var errors: UIStatePayloadFactory.Errors? = nil

  var elementStateObserver: ElementStateObserver {
    divKitComponents.blockStateStorage
  }

  init(
    json: Signal<[String: Any]>,
    urlOpener: @escaping UrlOpener
  ) {
    self.urlOpener = urlOpener

    divKitComponents = DivKitComponents(
      updateCardAction: { [weak self] _, patch in
        self?.update(patch: patch)
      },
      urlOpener: urlOpener
    )

    json.addObserver { [weak self] in self?.json = $0 }.dispose(in: disposePool)
  }

  func update(patch: DivPatch? = nil) {
    guard let json = json else { return }
    do {
      let divData = try RawDivData(dictionary: json).resolve()

      let patchedDivData: DivData?
      if let patch = patch {
        patchedDivData = divData.value?.applyPatch(patch)
      } else {
        patchedDivData = divData.value
      }

      let variablesStorage = divKitComponents.variablesStorage
      if variablesStorage.makeVariables(for: cardId).isEmpty {
        let palette: [String: Any]? = try json.getOptionalField("palette")
        let globalVariables = (palette?.makePalette(forThemeName: "light") ?? [:])
          .mapToDivVariables()
        variablesStorage.set(variables: globalVariables, triggerUpdate: false)
        variablesStorage.set(
          cardId: cardId,
          variables: divData.value?.variables?.extractDivVariableValues() ?? [:]
        )
      }

      divKitComponents.triggersStorage.set(
        cardId: cardId,
        triggers: divData.value?.variableTriggers ?? []
      )

      block = try patchedDivData?.makeBlock(
        context: divKitComponents.makeContext(
          cardId: cardId,
          cachedImageHolders: block?.getImageHolders() ?? []
        )
      )

      errors = divData.errorsOrWarnings.map {
        $0.map {
          let traceback = $0.traceback
          return (message: traceback.last, stack: traceback.asArray())
        }
      }
    } catch {
      block = nil
      errors = [(message: error.localizedDescription, stack: nil)]
    }
  }

  func reset() {
    divKitComponents.reset()
  }

  func handleDivAction(params: UserInterfaceAction.DivActionParams) {
    divKitComponents.actionHandler.handle(params: params, urlOpener: urlOpener)
  }
}

extension Dictionary where Key == String, Value == Any {
  fileprivate func makePalette(forThemeName themeName: String) -> [String: Color]? {
    guard let palette = try? self.getArray(themeName) as? [[String: String]] else { return nil }
    var result: [String: Color] = [:]
    palette.forEach {
      guard let name = $0["name"],
            let colorStr = $0["color"],
            let color = Color.color(withHexString: colorStr) else { return }
      result[name] = color
    }
    return result
  }
}

private let cardId: DivCardID = "LivePreview"
