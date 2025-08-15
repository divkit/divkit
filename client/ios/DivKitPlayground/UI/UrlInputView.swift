import SwiftUI

struct UrlInputView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  @AppStorage(UserPreferences.lastUrlKey)
  private var urlString = ""
  @AppStorage(UserPreferences.isQrScannerEnabledKey)
  private var isQrScannerEnabled = UserPreferences.isQrScannerEnabledDefault

  let divViewProvider: DivViewProvider

  var body: some View {
    let url = URL(string: urlString)
    return ViewWithHeader(
      "Playground",
      background: ThemeColor.divKit,
      presentationMode: presentationMode
    ) {
      Text("Enter link or scan QR code to see your result")
        .font(ThemeFont.text)
        .foregroundColor(Color(red: 1, green: 1, blue: 1, opacity: 0.69))
        .padding(EdgeInsets(top: 6, leading: 16, bottom: 26, trailing: 16))
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(ThemeColor.divKit)

      ZStack(alignment: .top) {
        ThemeColor.divKit
          .frame(height: ThemeSize.cornerRadius)
        TextEditor(text: $urlString)
          .font(ThemeFont.text)
          .keyboardType(.URL)
          .padding(EdgeInsets(top: 36, leading: 20, bottom: 20, trailing: 20))
          .frame(maxHeight: .infinity, alignment: .top)
          .background(Color.white)
          .cornerRadius(ThemeSize.cornerRadius)
      }

      if isQrScannerEnabled {
        ScannerView(result: $urlString)
          .cornerRadius(ThemeSize.cornerRadius)
          .frame(height: 260)
          .padding(EdgeInsets(top: 0, leading: 20, bottom: 20, trailing: 20))
      }

      HStack(spacing: 10) {
        Link(title: "load json", url: url) {
          PlaygroundView(url: $0, divViewProvider: divViewProvider)
        }
        Link(title: "web preview", url: url) {
          WebPreviewView(url: $0)
        }
      }
      .padding(EdgeInsets(top: 0, leading: 20, bottom: 20, trailing: 20))
    }.onTapGesture {
      hideKeyboard()
    }
  }
}

private struct Link<Destination>: View where Destination: View {
  let title: String
  let url: URL?
  let destination: (URL) -> Destination

  var body: some View {
    NavigationLink(title) {
      if let url {
        destination(url)
      }
    }
    .disabled(url == nil)
    .buttonStyle(LinkStyle())
  }
}

private struct LinkStyle: ButtonStyle {
  func makeBody(configuration: Configuration) -> some View {
    configuration.label
      .font(ThemeFont.button)
      .foregroundColor(.black)
      .frame(maxWidth: .infinity, minHeight: 80)
      .background(Color(red: 0xEB / 255, green: 0xEB / 255, blue: 0xEB / 255))
      .cornerRadius(ThemeSize.cornerRadius)
      .scaleEffect(configuration.isPressed ? 0.9 : 1.0)
  }
}

extension View {
  fileprivate func hideKeyboard() {
    let resign = #selector(UIResponder.resignFirstResponder)
    UIApplication.shared.sendAction(resign, to: nil, from: nil, for: nil)
  }
}
