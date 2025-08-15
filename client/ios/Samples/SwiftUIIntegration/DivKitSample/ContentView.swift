import DivKit
import SwiftUI

struct ContentView: View {
  @State private var isPresented: Bool = false
  @State private var text: String = ""

  var body: some View {
    DivHostingView(
      divkitComponents: DivKitComponents(
        divCustomBlockFactory: SampleDivCustomBlockFactory(),
        urlHandler: SampleDivActionHandler(
          isPresented: $isPresented,
          text: $text
        )
      ),
      source: DivViewSource(kind: .data(sampleData), cardId: "Sample"),
      debugParams: DebugParams(isDebugInfoEnabled: true)
    )
    .alert("\(text)", isPresented: $isPresented) {}
  }
}

private let sampleData: Data = try! Data(
  contentsOf: Bundle.main.url(
    forResource: "Sample",
    withExtension: "json"
  )!
)

#Preview {
  ContentView()
}
