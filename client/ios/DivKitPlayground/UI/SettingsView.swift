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
      Toggle(isOn: UserPreferences.isQrScannerEnabledBinding) {
        Text("QR Scanner")
          .font(ThemeFont.text)
      }
      .padding(20)
    }
    .toggleStyle(DefaultToggleStyle())
  }
}

private struct DefaultToggleStyle: ToggleStyle {
  func makeBody(configuration: Configuration) -> some View {
    Toggle(configuration)
      .padding(20)
      .frame(height: 68)
      .background(
        RoundedRectangle(cornerRadius: 22)
          .stroke(Color.black.opacity(0.12))
      )
  }
}
