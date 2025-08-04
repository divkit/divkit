#if os(iOS)
import SwiftUI

/// ``DivHostingView`` is a `UIViewRepresentable` adapter for using `DivKit` in `SwiftUI`.
/// You should use it by passing in the initializer `DivViewSource` you want to render in this view.
public struct DivHostingView: UIViewRepresentable {
  private let divkitComponents: DivKitComponents
  private let divViewPreloader: DivViewPreloader?

  private let source: DivViewSource
  private let debugParams: DebugParams
  private let shouldResetPreviousCardData: Bool

  public init(
    divkitComponents: DivKitComponents = DivKitComponents(),
    divViewPreloader: DivViewPreloader? = nil,
    source: DivViewSource,
    debugParams: DebugParams = DebugParams(),
    shouldResetPreviousCardData: Bool = false
  ) {
    self.divkitComponents = divkitComponents
    self.divViewPreloader = divViewPreloader
    self.source = source
    self.debugParams = debugParams
    self.shouldResetPreviousCardData = shouldResetPreviousCardData
  }

  public func makeUIView(context _: Context) -> UIView {
    let divView = DivView(
      divKitComponents: divkitComponents,
      divViewPreloader: divViewPreloader
    )
    let view = VisibilityTrackingView(divView: divView)
    return view
  }

  public func updateUIView(_ uiView: UIView, context _: Context) {
    Task {
      guard let view = uiView as? VisibilityTrackingView else { return }
      await view.divView.setSource(
        source,
        debugParams: debugParams,
        shouldResetPreviousCardData: shouldResetPreviousCardData
      )
      view.invalidateIntrinsicContentSize()
    }
  }
}

private class VisibilityTrackingView: UIView {
  override var intrinsicContentSize: CGSize {
    divView.intrinsicContentSize
  }

  let divView: DivView

  init(divView: DivView) {
    self.divView = divView

    super.init(frame: .zero)
    addSubview(divView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    divView.frame = bounds
    divView.onVisibleBoundsChanged(to: bounds)
  }
}
#endif
