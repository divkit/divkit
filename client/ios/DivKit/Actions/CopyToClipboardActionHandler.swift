import Foundation

#if os(iOS)
import UIKit
#endif

final class CopyToClipboardActionHandler {
  func handle(
    _ action: DivActionCopyToClipboard,
    context: DivActionHandlingContext
  ) {
    #if os(iOS)
    let pasteboard = UIPasteboard.general
    switch action.content {
    case let .contentText(text):
      if let text = text.resolveValue(context.expressionResolver) {
        pasteboard.string = text
      }
    case let .contentUrl(url):
      if let url = url.resolveValue(context.expressionResolver) {
        pasteboard.url = url
      }
    }
    #endif
  }
}
