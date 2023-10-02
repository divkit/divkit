import Foundation

#if os(iOS)
import UIKit
#endif

final class CopyToClipboardActionHandler {
  func handle(_ action: DivActionCopyToClipboard) {
    #if os(iOS)
    let pasteboard = UIPasteboard.general

    switch action.content {
    case let .contentText(text):
      if let text = text.value.rawValue {
        pasteboard.string = text
      }
    case let .contentUrl(url):
      if let url = url.value.rawValue {
        pasteboard.url = url
      }
    }
    #endif
  }
}
