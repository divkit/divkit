import SwiftUI

import CommonCore
import DivKit

final class DivViewGraph {
  public let jsonDataProvider = DataProvider()

  private var blockProvider: DivBlockProvider!
  private var divKitComponents: DivKitComponents!

  init(
    urlOpener: @escaping UrlOpener
  ) {
    divKitComponents = DivKitComponents(
      divCustomBlockFactory: DemoDivCustomBlockFactory(),
      flagsInfo: DivFlagsInfo(isTextSelectingEnabled: true, appendVariablesEnabled: true),
      patchProvider: SamplePatchProvider(),
      updateCardAction: { [weak self] _, patch in
        self?.blockProvider.update(patch: patch)
      },
      urlOpener: urlOpener,
      variablesStorage: globalVaribalesStorage
    )

    blockProvider = DivBlockProvider(
      jsonDataProvider: jsonDataProvider.currentAndNewValues,
      divKitComponents: divKitComponents
    )
  }

  func makeDivView() -> some View {
    DivView(
      blockProvider: blockProvider,
      divKitComponents: divKitComponents
    ).onDisappear { [weak self] in
      self?.jsonDataProvider.set(url: nil)
    }
  }
}

final class DataProvider {
  typealias Factory = () throws -> Data?

  private let urlObservable: ObservableProperty<URL?> = ObservableProperty()

  var currentAndNewValues: Signal<Factory> {
    urlObservable.currentAndNewValues.map(factory)
  }

  func set(url: String) {
    set(url: URL(string: url))
  }

  func set(url: URL?) {
    urlObservable.value = url
  }

  private func factory(url: URL?) -> Factory {
    { try url.flatMap { try Data(contentsOf: $0) } }
  }
}

let globalVaribalesStorage: DivVariablesStorage = {
  let storage = DivVariablesStorage()
  storage.set(
    variables: [
      "global_var": .string("global_value"),
    ],
    triggerUpdate: false
  )
  return storage
}()
