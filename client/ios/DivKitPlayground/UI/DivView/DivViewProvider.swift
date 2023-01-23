import SwiftUI

import DivKit

final class DivViewProvider {
  public let jsonProvider = ObservableJsonProvider()

  private var blockProvider: DivBlockProvider!
  private var divKitComponents: DivKitComponents!

  init() {
    divKitComponents = AppComponents.makeDivKitComponents(
      updateCardAction: { [weak self] reason in
        self?.blockProvider.update(patches: reason.compactMap(\.patch))
      }
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
