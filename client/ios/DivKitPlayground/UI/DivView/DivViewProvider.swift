import DivKit
import LayoutKit
import SwiftUI

final class DivViewProvider {
  let jsonProvider: PlaygroundJsonProvider

  private let divKitComponents: DivKitComponents
  private let layoutDirection: UIUserInterfaceLayoutDirection

  init(layoutDirection: UIUserInterfaceLayoutDirection = .system) {
    self.layoutDirection = layoutDirection
    let jsonProvider = PlaygroundJsonProvider()
    let urlHandler = PlaygroundUrlHandler(
      loadJsonUrl: jsonProvider.load(url:)
    )
    divKitComponents = AppComponents.makeDivKitComponents(
      layoutDirection: layoutDirection,
      urlHandler: urlHandler,
      variableStorage: jsonProvider.paletteVariableStorage
    )
    self.jsonProvider = jsonProvider
  }

  func makeDivView(_ url: URL) -> some View {
    DivViewControllerSwiftUIAdapter(
      jsonPublisher: jsonProvider.jsonPublisher,
      divKitComponents: divKitComponents,
      debugParams: AppComponents.debugParams
    )
    .onAppear { [weak self] in
      self?.jsonProvider.load(url: url)
    }
    .onDisappear { [weak self] in
      self?.divKitComponents.reset(cardId: cardId)
    }
  }
}

let cardId: DivCardID = "DivViewCard"
