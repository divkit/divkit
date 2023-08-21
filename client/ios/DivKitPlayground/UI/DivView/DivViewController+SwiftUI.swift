import CommonCorePublic
import DivKit
import SwiftUI

struct DivViewControllerSwiftUIAdapter: UIViewControllerRepresentable {
  let jsonProvider: Signal<[String: Any]>
  let divKitComponents: DivKitComponents
  let debugParams: DebugParams

  func makeUIViewController(context _: Context) -> UIViewController {
    DivViewController(
      jsonProvider: jsonProvider,
      divKitComponents: divKitComponents,
      debugParams: debugParams
    )
  }

  func updateUIViewController(_: UIViewController, context _: Context) {}
}
