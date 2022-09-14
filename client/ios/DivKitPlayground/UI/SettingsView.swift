import SwiftUI

import DivKit

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
      VStack(alignment: .leading, spacing: 0) {
        Toggle(isOn: $isQrScannerEnabledKeymeMode) {
          Text("QR Scanner")
            .font(ThemeFont.text)
        }

        Text("Playground theme")
          .font(ThemeFont.text)
          .padding(EdgeInsets(top: 20, leading: 0, bottom: 10, trailing: 0))
        RadioButtonsView(
          options: Theme.allCases.map { $0.rawValue },
          selected: $playgroundTheme
        )
        
        Spacer()

        Text("DivKit version \(DivKitInfo.version)")
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
