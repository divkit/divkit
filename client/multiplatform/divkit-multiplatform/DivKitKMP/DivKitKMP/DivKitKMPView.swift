#if os(iOS)
import UIKit

@_spi(Legacy) import DivKit

final class DivKitKMPView: UIView {
  override var intrinsicContentSize: CGSize {
    divView.intrinsicContentSize
  }

  override func sizeThatFits(_ size: CGSize) -> CGSize {
    divView.sizeThatFits(size)
  }

  private let divView: DivView
  private var current: (cardId: String, json: String) = ("", "")

  init(divView: DivView) {
    self.divView = divView
    super.init(frame: .zero)

    addSubview(divView)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func updateSourceIfNeeded(jsonString: String, cardId: String) {
    guard cardId != current.cardId || jsonString != current.json else { return }
    current = (cardId, jsonString)
    divView.setSource(
      DivViewSource(
        kind: .data(Data(jsonString.utf8)),
        cardId: DivCardID(rawValue: cardId)
      ),
      shouldResetPreviousCardData: true
    )
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    divView.frame = bounds
    divView.onVisibleBoundsChanged(to: bounds)
  }
}
#endif
