import SwiftUI

struct UrlInputView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  @State
  private var urlString = ""
  
  let divViewProvider: DivViewProvider
  
  var body: some View {
    ViewWithHeader(
      "Playground",
      background: ThemeColor.divKit,
      presentationMode: presentationMode
    ) {
      Text("Enter URL to see your result")
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
          .padding(EdgeInsets(top: 36, leading: 20, bottom: 36, trailing: 20))
          .frame(maxHeight: .infinity, alignment: .top)
          .background(Color.white)
          .cornerRadius(ThemeSize.cornerRadius)
      }
      if let url = URL(string: urlString) {
        NavigationLink("load") {
          DivOnlineView(url: url, divViewProvider: divViewProvider)
        }
        .padding(20)
        .buttonStyle(LoadButtonStyle())
      }
    }
    .onAppear {
      urlString = UserPreferences.lastUrl
    }
    .onDisappear {
      UserPreferences.lastUrl = urlString
    }
  }
}

private struct LoadButtonStyle: ButtonStyle {
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
