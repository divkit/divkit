#if os(iOS)
import SwiftUI
import VGSL

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

  /// Answers a SwiftUI size proposal for a card of the given size.
  ///
  /// SwiftUI proposes `infinity` to ask for the maximum size of a view. A card with a
  /// `match_parent` dimension can occupy all the space offered along it, so such a dimension is
  /// answered with the card's maximum size instead of the content size that
  /// ``DivViewSize/sizeFor(parentViewSize:)`` returns for an unbounded parent. The width is
  /// resolved first, and a height that depends on the width is computed for the width reported to
  /// SwiftUI.
  static func fittingSize(
    cardSize: DivViewSize,
    proposal: (width: CGFloat?, height: CGFloat?)
  ) -> CGSize? {
    guard let proposedWidth = proposal.width, let proposedHeight = proposal.height else {
      return nil
    }

    let width = maximumSize(
      proposedDimension: proposedWidth,
      dimension: cardSize.width,
      minSize: cardSize.minWidth,
      maxSize: cardSize.maxWidth
    ) ?? cardSize.resolveWidth(parentWidth: proposedWidth)
    let height = maximumSize(
      proposedDimension: proposedHeight,
      dimension: cardSize.height,
      minSize: cardSize.minHeight,
      maxSize: cardSize.maxHeight
    ) ?? cardSize.resolveHeight(width: width, parentHeight: proposedHeight)
    return CGSize(width: width, height: height)
  }

  @available(iOS 16.0, *)
  public func sizeThatFits(
    _ proposal: ProposedViewSize,
    uiView _: UIView,
    context _: Context
  ) -> CGSize? {
    guard let cardSize = divView.cardSize else { return nil }
    return Self.fittingSize(
      cardSize: cardSize,
      proposal: (width: proposal.width, height: proposal.height)
    )
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

/// Returns the maximum size of a `match_parent` dimension when SwiftUI asks for it with an
/// `infinity` proposal, and `nil` for any other query.
private func maximumSize(
  proposedDimension: CGFloat,
  dimension: DivViewSize.DivDimension,
  minSize: CGFloat,
  maxSize: CGFloat
) -> CGFloat? {
  guard proposedDimension == .infinity, dimension == .matchParent else {
    return nil
  }
  return clamp(.infinity, min: minSize, max: maxSize)
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
