#if os(iOS)
import SwiftUI

/// ``DivHostingView`` is a `UIViewRepresentable` adapter for using `DivKit` in `SwiftUI`.
/// You should use it by passing in the initializer `DivViewSource` you want to render in this view.
public struct DivHostingView: UIViewRepresentable {
  private let divView: DivView

  public init(
    divkitComponents: DivKitComponents = DivKitComponents(),
    divViewPreloader: DivViewPreloader? = nil,
    source: DivViewSource,
    debugParams: DebugParams = DebugParams(),
    shouldResetPreviousCardData: Bool = false
  ) {
    self.divView = DivView(
      divKitComponents: divkitComponents,
      divViewPreloader: divViewPreloader
    )
    divView.setSource(
      source,
      debugParams: debugParams,
      shouldResetPreviousCardData: shouldResetPreviousCardData
    )
  }

  @available(iOS 16.0, *)
  public func sizeThatFits(
    _ proposal: ProposedViewSize,
    uiView _: UIView,
    context _: Context
  ) -> CGSize? {
    guard let width = proposal.width, let height = proposal.height else { return nil }
    return divView.cardSize?.sizeFor(parentViewSize: CGSize(width: width, height: height))
  }

  public func makeUIView(context _: Context) -> UIView {
    VisibilityTrackingView(divView: divView)
  }

  public func updateUIView(_ uiView: UIView, context _: Context) {
    Task {
      uiView.invalidateIntrinsicContentSize()
    }
  }
}

private class VisibilityTrackingView: UIView {
  override var intrinsicContentSize: CGSize {
    divView.intrinsicContentSize
  }

  private let divView: DivView

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
