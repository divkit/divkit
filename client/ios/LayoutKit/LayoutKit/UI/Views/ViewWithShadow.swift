import Foundation
import UIKit

import CommonCorePublic

open class ViewWithShadow: UIView {
  public var contentView: UIView {
    didSet {
      oldValue.removeFromSuperview()
      addSubview(contentView)
      contentView.layer.masksToBounds = true
    }
  }

  public var shadow: BlockShadow? {
    didSet {
      guard oldValue != nil || shadow != oldValue else {
        return
      }

      if let shadow = shadow {
        layer.shadowRadius = shadow.blurRadius
        layer.shadowOffset = CGSize(width: shadow.offset.x, height: shadow.offset.y)
        layer.shadowOpacity = shadow.opacity
        layer.shadowColor = shadow.color.cgColor
      } else {
        layer.shadowOpacity = 0
      }

      setNeedsLayout()
    }
  }

  public override init(frame: CGRect) {
    contentView = UIView(frame: frame)
    contentView.layer.masksToBounds = true
    super.init(frame: frame)
    layer.masksToBounds = false
    addSubview(contentView)
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  open override func layoutSubviews() {
    super.layoutSubviews()
    guard !bounds.isEmpty else {
      return
    }

    if let shadow = shadow {
      layer.shadowPath = .roundedRect(
        size: bounds.size,
        cornerRadii: shadow.cornerRadii
      )
    } else {
      layer.shadowPath = nil
    }

    contentView.frame = bounds
  }

  open override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result === self ? nil : result
  }
}
