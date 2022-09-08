import SwiftUI

struct SettingsView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>
  @AppStorage(UserPreferences.isQrScannerEnabledKey)
  var isQrScannerEnabledKeymeMode: Bool = UserPreferences.isQrScannerEnabledDefault
  @AppStorage(UserPreferences.playgroundThemeKey)
  var playgroundTheme: String = UserPreferences.playgroundThemeDefault.rawValue

  var body: some View {
    ViewWithHeader(
      "Settings",
      background: ThemeColor.settings,
      presentationMode: presentationMode
    ) {
      Toggle(isOn: $isQrScannerEnabledKeymeMode) {
        Text("QR Scanner")
          .font(ThemeFont.text)
      }
      .padding(20)

      Text("Playground theme")
        .font(ThemeFont.text)
      RadioButtonsView(
        options: Theme.allCases.map { $0.rawValue },
        selected: $playgroundTheme
      )
        .padding(20)
        .background(
          RoundedRectangle(cornerRadius: 22)
            .stroke(Color.black.opacity(0.12))
        )
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
