import Foundation

final class ClearFocusActionHandler {
  func handle(context: DivActionHandlingContext) {
    context.blockStateStorage.clearFocus()
  }
}
