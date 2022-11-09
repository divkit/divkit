// Copyright 2015 Yandex LLC. All rights reserved.

import UIKit

import Base

public final class RemoteImageViewContainer: UIView {
  public var contentView: RemoteImageViewContentProtocol {
    didSet {
      oldValue.removeFromSuperview()
      addSubview(contentView)
    }
  }

  var imageRequest: Cancellable?
  public var imageHolder: ImageHolder? {
    didSet {
      imageRequest?.cancel()
      contentView.setImage(nil, animated: false)
      switch imageHolder?.placeholder {
      case let .color(color)?:
        backgroundColor = color.systemColor
      case .none, .image?:
        backgroundColor = nil
      }

      let newValue = imageHolder
      imageRequest = imageHolder?.requestImageWithSource { [weak self] result in
        guard let self = self,
              newValue === self.imageHolder else {
          return
        }

        self.contentView.setImage(result?.0, animated: result?.1.shouldAnimate)
        self.backgroundColor = nil
      }
    }
  }

  public init(contentView: RemoteImageViewContentProtocol = RemoteImageView()) {
    self.contentView = contentView
    super.init(frame: .zero)
    addSubview(contentView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    contentView.frame = bounds
  }

  deinit {
    imageRequest?.cancel()
  }
}
