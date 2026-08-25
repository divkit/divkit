#if os(iOS)
import Foundation
import UIKit

final class AncestorFrameObserver {
  private var observations: [NSKeyValueObservation] = []

  init(view: UIView, onChange: @escaping () -> Void) {
    var currentView: UIView? = view
    while let observedView = currentView, !(observedView is UIWindow) {
      observations.append(observedView.observe(\.center) { _, _ in onChange() })
      observations.append(observedView.observe(\.bounds) { _, _ in onChange() })
      observations.append(observedView.observe(\.frame) { _, _ in onChange() })
      if let scrollView = observedView as? UIScrollView {
        observations.append(scrollView.observe(\.contentOffset) { _, _ in onChange() })
      }
      currentView = observedView.superview
    }
  }

  deinit {
    invalidate()
  }

  func invalidate() {
    observations.forEach { $0.invalidate() }
    observations.removeAll()
  }
}
#endif
