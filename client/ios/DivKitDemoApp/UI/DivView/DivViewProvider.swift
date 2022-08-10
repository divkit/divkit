import SwiftUI

import CommonCore
import DivKit

final class DivViewProvider {
  public let jsonDataProvider = DataProvider()

  private var blockProvider: DivBlockProvider!
  private var divKitComponents: DivKitComponents!
  private let urlOpener = DemoUrlOpener()
  
  init() {
    divKitComponents = DivKitComponents(
      divCustomBlockFactory: DemoDivCustomBlockFactory(),
      flagsInfo: DivFlagsInfo(isTextSelectingEnabled: true, appendVariablesEnabled: true),
      patchProvider: DemoPatchProvider(),
      updateCardAction: { [weak self] _, patch in
        self?.blockProvider.update(patch: patch)
      },
      urlOpener: urlOpener.openUrl(_:),
      variablesStorage: globalVaribalesStorage
    )

    blockProvider = DivBlockProvider(
      jsonDataProvider: jsonDataProvider.currentAndNewValues,
      divKitComponents: divKitComponents
    )
  }

  func makeDivView(_ url: URL) -> some View {
    DivView(
      blockProvider: blockProvider,
      divKitComponents: divKitComponents,
      urlOpener: urlOpener
    )
    .onAppear { [weak self] in
      self?.jsonDataProvider.set(url: url)
    }
    .onDisappear { [weak self] in
      self?.jsonDataProvider.set(url: nil)
      self?.urlOpener.onUnhandledUrl = { _ in }
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
