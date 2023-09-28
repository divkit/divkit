import Foundation
import UIKit

final class CopyToClipboardActionHandler {
  func handle(_ action: DivActionCopyToClipboard) {
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
  }
}
