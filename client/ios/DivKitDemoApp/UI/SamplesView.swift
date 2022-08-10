import SwiftUI

struct SamplesView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>
  
  var body: some View {
    ViewWithHeader(
      "Samples",
      background: ThemeColor.samples,
      presentationMode: presentationMode
    ) {
      Text("Not implemented")
        .font(ThemeFont.text)
        .padding()
    }
  }
}
