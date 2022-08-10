import SwiftUI

struct SettingsView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>
  
  var body: some View {
    ViewWithHeader(
      "Settings",
      background: ThemeColor.settings,
      presentationMode: presentationMode
    ) {
      Text("Not implemented")
        .font(ThemeFont.text)
        .padding()
    }
  }
}
