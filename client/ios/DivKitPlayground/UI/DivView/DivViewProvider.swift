import DivKit
import SwiftUI

final class DivViewProvider {
  public let jsonProvider = ObservableJsonProvider()

  private var blockProvider: DivBlockProvider!
  private var divKitComponents: DivKitComponents!
  private var layoutDirection: UIUserInterfaceLayoutDirection

  init(layoutDirection: UIUserInterfaceLayoutDirection = .system) {
    self.layoutDirection = layoutDirection

    let urlHandler = PlaygroundUrlHandler(
      loadJsonUrl: { [weak self] url in
        self?.jsonProvider.load(url: url)
      }
    )
    divKitComponents = AppComponents.makeDivKitComponents(
      layoutDirection: layoutDirection,
      urlHandler: urlHandler
    )

    blockProvider = DivBlockProvider(
      json: jsonProvider.signal,
      divKitComponents: divKitComponents,
      shouldResetOnDataChange: true
    )
  }

  func makeDivView(_ url: URL) -> some View {
    DivView(
      blockProvider: blockProvider,
      divKitComponents: divKitComponents
    )
    .onAppear { [weak self] in
      self?.jsonProvider.load(url: url)
    }
  }
}

extension DivActionURLHandler.UpdateReason {
  var patch: DivPatch? {
    switch self {
    case let .patch(_, patch):
      return patch
    case .timer, .variable, .state:
      return nil
    }
  }
}
