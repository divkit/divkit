import DivKit
import SwiftUI

final class SampleDivActionHandler: DivUrlHandler {
  private let isPresented: Binding<Bool>
  private let text: Binding<String>

  init(isPresented: Binding<Bool>, text: Binding<String>) {
    self.isPresented = isPresented
    self.text = text
  }

  func handle(_ url: URL, info _: DivActionInfo, sender _: AnyObject?) {
    guard let components = url.components, components.scheme == scheme else {
      return
    }

    switch components.host {
    case "toast":
      if let text = components.queryItems?.first?.name {
        self.text.wrappedValue = text
        self.isPresented.wrappedValue = true
      }
    default:
      return
    }
  }
}

private let scheme = "sample-action"
