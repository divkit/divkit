import DivKit
import SwiftUI

struct DivViewSwiftUIAdapter: UIViewRepresentable {
  let cardId: DivCardID
  let jsonData: Data
  let divKitComponents: DivKitComponents

  func makeUIView(context _: Context) -> UIView {
    let view = DivView(divKitComponents: divKitComponents)
    view.setSource(
      .init(kind: .data(jsonData), cardId: cardId, parentScrollView: nil),
      shouldResetPreviousCardData: true
    )
    return view
  }

  func updateUIView(_: UIView, context _: Context) {}
}
