import Foundation

#if os(iOS)
import UIKit
#endif

final class HapticActionHandler {
  func handle(_ action: DivActionHaptic, context: DivActionHandlingContext) {
    #if os(iOS)
    switch action.resolveFeedback(context.expressionResolver) {
    case .light:
      UIImpactFeedbackGenerator(style: .light).impactOccurred()
    case .medium:
      UIImpactFeedbackGenerator(style: .medium).impactOccurred()
    case .heavy:
      UIImpactFeedbackGenerator(style: .heavy).impactOccurred()
    case .success:
      UINotificationFeedbackGenerator().notificationOccurred(.success)
    case .error:
      UINotificationFeedbackGenerator().notificationOccurred(.error)
    }
    #endif
  }
}
