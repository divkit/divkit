import SwiftUI

struct PlaygroundView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  @Environment(\.colorScheme) var colorScheme

  let url: URL
  let divViewProvider: DivViewProvider

  var body: some View {
    ViewWithHeader(
      "Playground",
      background: ThemeColor.divKit,
      presentationMode: presentationMode
    ) {
      divViewProvider.makeDivView(url)
    }
    .overlay(reloadButton, alignment: .topTrailing)
    .onChange(of: colorScheme) { _ in
      divViewProvider.jsonProvider.refreshPalette()
    }
  }

  private var reloadButton: some View {
    Button(action: reload) {
      Image(systemName: "arrow.triangle.2.circlepath")
        .applyHeaderButtonStyle()
    }
  }

  private func reload() {
    divViewProvider.jsonProvider.load(url: url)
  }
}
