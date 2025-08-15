import Combine
import DivKit
import SwiftUI

struct DivViewControllerSwiftUIAdapter: UIViewControllerRepresentable {
  let jsonPublisher: JsonPublisher
  let divKitComponents: DivKitComponents
  let debugParams: DebugParams

  func makeUIViewController(context _: Context) -> UIViewController {
    DivViewController(
      jsonPublisher: jsonPublisher,
      divKitComponents: divKitComponents,
      debugParams: debugParams
    )
  }

  func updateUIViewController(_: UIViewController, context _: Context) {}
}
