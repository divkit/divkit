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
    Task {
      await (view as? DivView)?.setSource(
        DivViewSource(kind: .data(jsonData), cardId: cardId),
        shouldResetPreviousCardData: true
      )
    }
  }
}
