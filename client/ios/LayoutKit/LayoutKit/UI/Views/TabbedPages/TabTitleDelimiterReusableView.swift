import UIKit

final class TabTitleDelimiterReusableView: UICollectionReusableView {
  static let reuseID = "TabTitleDelimiterReusableView"
  private let delimiterImageView = UIImageView()

  override init(frame: CGRect) {
    super.init(frame: frame)
    setupView()
  }

  required init?(coder: NSCoder) {
    super.init(coder: coder)
    setupView()
  }

  private func setupView() {
    addSubview(delimiterImageView)
    delimiterImageView.contentMode = .scaleAspectFit
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    delimiterImageView.frame = bounds
  }

  func configure(with image: UIImage?) {
    delimiterImageView.image = image
  }
}
