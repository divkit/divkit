import SwiftUI

import DivKit

final class DivViewProvider {
  public let jsonProvider = ObservableJsonProvider()
  
  private var blockProvider: DivBlockProvider!
  private var divKitComponents: DivKitComponents!
  
  init() {
    divKitComponents = AppComponents.makeDivKitComponents(
      updateCardAction: { [weak self] _, reason in
        switch reason {
        case let .patch(patch):
          self?.blockProvider.update(patch: patch)
        case .timer:
          self?.blockProvider.update(patch: nil)
        }
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
