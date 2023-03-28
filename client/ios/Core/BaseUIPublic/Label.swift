// Copyright 2021 Yandex LLC. All rights reserved.

import UIKit

import BaseTinyPublic

@objc(YCLabel)
public final class Label: UIView {
  private var useAttributesFromText = false

  private var _attributedText: NSAttributedString? {
    didSet {
      setNeedsDisplay()
    }
  }

  public var attributedText: NSAttributedString? {
    get {
      _attributedText
    }
    set {
      guard _attributedText != newValue else {
        return
      }
      _attributedText = newValue
      useAttributesFromText = true
      clearExternalTextAttributes()
      assignExternalColorFromAttributes()
    }
  }

  public var text: String? {
    get { _attributedText?.string }
    set {
      _attributedText = (newValue ?? "").with(typo: externalTypo)
      useAttributesFromText = false
    }
  }

  private var _textColor: Color?
  public var textColor: Color? {
    get {
      _textColor
    }
    set {
      guard _textColor != newValue else {
        return
      }
      _textColor = newValue
      updateText()
    }
  }

  private var _highlightedTextColor: Color?
  public var highlightedTextColor: Color? {
    get {
      _highlightedTextColor
    }
    set {
      guard _highlightedTextColor != newValue else {
        return
      }
      _highlightedTextColor = newValue
      assignExternalColorFromAttributes()
      updateText()
    }
  }

  private var _lineBreakMode: NSLineBreakMode?
  public var lineBreakMode: NSLineBreakMode? {
    get {
      _lineBreakMode
    }
    set {
      guard _lineBreakMode != newValue else {
        return
      }
      _lineBreakMode = newValue
      updateText()
    }
  }

  private var _textAlignment: NSTextAlignment?
  public var textAlignment: NSTextAlignment? {
    get {
      _textAlignment
    }
    set {
      guard _textAlignment != newValue else {
        return
      }
      _textAlignment = newValue
      updateText()
    }
  }

  private var _font: UIFont?
  public var font: UIFont? {
    get {
      _font
    }
    set {
      guard _font != newValue else {
        return
      }
      _font = newValue
      updateText()
    }
  }

  public var isHighlighted = false {
    didSet {
      assert(highlightedTextColor != nil || !isHighlighted)
      updateText()
    }
  }

  public var numberOfLines = 0
  public var adjustsFontSizeToFitWidth = false
  public var minimumScaleFactor: CGFloat = 0
  public var firstCharacterLeftOffset: CGFloat {
    let layout = attributedText?.drawAndGetLayout(inContext: nil, rect: bounds)
    return layout?.firstLineOriginX ?? 0
  }

  private func updateText() {
    guard let currentText = _attributedText else {
      return
    }

    var newTypo = useAttributesFromText ? currentText.typo : Typo()
    newTypo.append(externalTypo)
    _attributedText = currentText.string.with(typo: newTypo)
  }

  private func clearExternalTextAttributes() {
    _textColor = nil
    _lineBreakMode = nil
    _textAlignment = nil
    _font = nil
  }

  private func assignExternalColorFromAttributes() {
    if highlightedTextColor != nil, textColor == nil {
      textColor = _attributedText?.foregroundColor
    }
  }

  private var externalTypo: Typo {
    var result = Typo()
    result.append((isHighlighted ? highlightedTextColor : textColor).map { Typo(color: $0) })
    result.append(lineBreakMode.map(Typo.init))
    result.append(textAlignment.map(Typo.init))
    result.append(font.map { Typo(font: $0) })
    return result
  }

  public override init(frame: CGRect) {
    super.init(frame: frame)

    contentMode = .redraw
    backgroundColor = .clear
    isUserInteractionEnabled = false
    layer.contentsGravity = .center
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func draw(_ rect: CGRect) {
    let context = UIGraphicsGetCurrentContext()!
    let textToDraw = attributedText ?? NSAttributedString()
    textToDraw.draw(inContext: context, rect: rect)
  }

  public override func sizeThatFits(_ size: CGSize) -> CGSize {
    if let currentText = attributedText {
      let maxNumberOfLines = numberOfLines != 0 ? numberOfLines : .max
      return currentText.sizeThatFits(size, maxNumberOfLines: maxNumberOfLines)
    } else {
      return .zero
    }
  }

  public override func sizeToFit() {
    bounds.size = sizeThatFits(.infinite)
  }
}

extension NSAttributedString {
  fileprivate var typo: Typo {
    Typo(attributedString: self)
  }

  fileprivate var foregroundColor: Color {
    if length > 0, let color = attribute(
      .foregroundColor,
      at: 0,
      effectiveRange: nil
    ) as? SystemColor {
      return color.rgba
    } else {
      return .black
    }
  }
}

extension Typo {
  fileprivate mutating func append(_ other: Typo?) {
    if let other = other {
      self = self + other
    }
  }
}
