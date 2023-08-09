import DivKit
import SwiftUI

struct DivViewSwiftUIAdapter: UIViewRepresentable {
  let cardId: DivCardID
  let jsonData: Data
  let divKitComponents: DivKitComponents

  func makeUIView(context _: Context) -> UIView {
    DivView(divKitComponents: divKitComponents)
  }

  func updateUIView(_ view: UIView, context _: Context) {
    (view as? DivView)?.setSource(
      DivBlockProvider.Source(kind: .data(jsonData), cardId: cardId),
      shouldResetPreviousCardData: true
    )
  }
}
