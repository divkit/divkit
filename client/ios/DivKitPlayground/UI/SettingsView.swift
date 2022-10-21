import SwiftUI

import DivKit

struct SettingsView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>
  @AppStorage(UserPreferences.isQrScannerEnabledKey)
  var isQrScannerEnabledKeymeMode: Bool = UserPreferences.isQrScannerEnabledDefault
  @AppStorage(UserPreferences.showRenderingTimeKey)
  var showRenderingTime: Bool = UserPreferences.showRenderingTimeDefault
  @AppStorage(UserPreferences.playgroundThemeKey)
  var playgroundTheme: String = UserPreferences.playgroundThemeDefault.rawValue

  var body: some View {
    ViewWithHeader(
      "Settings",
      background: ThemeColor.settings,
      presentationMode: presentationMode
    ) {
      VStack(alignment: .leading, spacing: 20) {
        Toggle(isOn: $isQrScannerEnabledKeymeMode) {
          Text("QR Scanner")
            .font(ThemeFont.text)
        }
        Toggle(isOn: $showRenderingTime) {
          Text("Show rendering time")
            .font(ThemeFont.text)
        }
        VStack(alignment: .leading, spacing: 10) {
          Text("Playground theme")
            .font(ThemeFont.text)
          RadioButtonsView(
            options: Theme.allCases.map { $0.rawValue },
            selected: $playgroundTheme
          )
        }
        
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
