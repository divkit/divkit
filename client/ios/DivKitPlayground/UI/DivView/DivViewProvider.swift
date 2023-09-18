import BasePublic
import DivKit
import LayoutKit
import SwiftUI

final class DivViewProvider {
  public let jsonProvider: PlaygroundJsonProvider
  private let divKitComponents: DivKitComponents
  private let layoutDirection: UIUserInterfaceLayoutDirection

  init(layoutDirection: UIUserInterfaceLayoutDirection = .system) {
    self.layoutDirection = layoutDirection
    let variablesStorage = DivVariablesStorage()
    let jsonProvider = PlaygroundJsonProvider(variablesStorage: variablesStorage)
    let urlHandler = PlaygroundUrlHandler(
      loadJsonUrl: { url in
        jsonProvider.load(url: url)
      }
    )
    divKitComponents = AppComponents.makeDivKitComponents(
      layoutDirection: layoutDirection,
      urlHandler: urlHandler,
      variablesStorage: variablesStorage
    )
    self.jsonProvider = jsonProvider
  }

  func makeDivView(_ url: URL) -> some View {
    DivViewControllerSwiftUIAdapter(
      jsonProvider: jsonProvider.$json.newValues,
      divKitComponents: divKitComponents,
      debugParams: AppComponents.debugParams
    )
    .onAppear { [weak self] in
      self?.jsonProvider.load(url: url)
    }
  }
}
