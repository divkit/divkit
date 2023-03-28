import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic

@_implementationOnly import CoreText

public final class TextBlock: BlockWithTraits {
  public struct InlineImage: Equatable {
    public let size: CGSize
    public let holder: ImageHolder
    public let location: Int
    public let tintColor: Color?

    public init(size: CGSize, holder: ImageHolder, location: Int, tintColor: Color? = nil) {
      self.size = size
      self.holder = holder
      self.location = location
      self.tintColor = tintColor
    }
  }

  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let text: NSAttributedString
  public let textGradient: Gradient?
  public let verticalAlignment: Alignment
  public let maxIntrinsicNumberOfLines: Int
  public let minNumberOfHiddenLines: Int
  public let images: [InlineImage]
  public let accessibilityElement: AccessibilityElement?
  public let canSelect: Bool

  let attachments: [TextAttachment]
  let truncationToken: NSAttributedString?
  let truncationImages: [InlineImage]
  let truncationAttachments: [TextAttachment]

  private var cachedIntrinsicWidth: CGFloat?
  private var cachedIntrinsicHeight: (width: CGFloat, height: CGFloat)?

  public init(
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait = .intrinsic,
    text: NSAttributedString,
    textGradient: Gradient? = nil,
    verticalAlignment: Alignment = .center,
    maxIntrinsicNumberOfLines: Int = .max,
    minNumberOfHiddenLines: Int = 0,
    images: [InlineImage] = [],
    accessibilityElement: AccessibilityElement?,
    truncationToken: NSAttributedString? = nil,
    truncationImages: [TextBlock.InlineImage] = [],
    canSelect: Bool = false
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    (self.text, attachments) = setImagePlaceholders(for: images, to: text)
    self.textGradient = textGradient
    self.verticalAlignment = verticalAlignment
    self.maxIntrinsicNumberOfLines = maxIntrinsicNumberOfLines
    self.minNumberOfHiddenLines = minNumberOfHiddenLines
    self.images = images
    self.accessibilityElement = accessibilityElement
    self.canSelect = canSelect
    self.truncationImages = truncationImages
    if let truncationToken = truncationToken {
      (self.truncationToken, self.truncationAttachments) = setImagePlaceholders(
        for: truncationImages, to: truncationToken
      )
    } else {
      self.truncationToken = nil
      self.truncationAttachments = []
    }
  }

  public convenience init(
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait = .intrinsic,
    text: NSAttributedString,
    textGradient: Gradient? = nil,
    verticalAlignment: Alignment = .center,
    maxIntrinsicNumberOfLines: Int = .max,
    minNumberOfHiddenLines: Int = 0,
    images: [InlineImage] = [],
    truncationToken: NSAttributedString? = nil,
    truncationImages: [TextBlock.InlineImage] = [],
    canSelect: Bool = false
  ) {
    self.init(
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      text: text,
      textGradient: textGradient,
      verticalAlignment: verticalAlignment,
      maxIntrinsicNumberOfLines: maxIntrinsicNumberOfLines,
      minNumberOfHiddenLines: minNumberOfHiddenLines,
      images: images,
      accessibilityElement: .staticText(label: text.string),
      truncationToken: truncationToken,
      truncationImages: truncationImages,
      canSelect: canSelect
    )
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .intrinsic(_, minSize, maxSize):
      if let cached = cachedIntrinsicWidth {
        return cached
      }

      let width = ceil(text.sizeForWidth(.infinity).width)
      let result = clamp(width, min: minSize, max: maxSize)
      cachedIntrinsicWidth = result
      return result
    case let .fixed(value):
      return value
    case .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .intrinsic(constrained, minSize, maxSize):
      if let cached = cachedIntrinsicHeight,
         cached.width.isApproximatelyEqualTo(width) {
        return cached.height
      }

      let height = ceil(
        text.heightForWidth(
          width,
          maxNumberOfLines: maxIntrinsicNumberOfLines,
          minNumberOfHiddenLines: minNumberOfHiddenLines
        )
      )
      let result = constrained ? height : clamp(height, min: minSize, max: maxSize)
      cachedIntrinsicHeight = (width: width, height: result)
      return result
    case let .fixed(value):
      return value
    case .weighted:
      return 0
    }
  }

  public func ascent(forWidth width: CGFloat) -> CGFloat? {
    text.ascent(forWidth: width)
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? TextBlock else {
      return false
    }

    return self == other
  }

  public func getImageHolders() -> [ImageHolder] {
    images.map { $0.holder }
  }

  public static func ==(lhs: TextBlock, rhs: TextBlock) -> Bool {
    lhs.widthTrait == rhs.widthTrait
      && lhs.heightTrait == rhs.heightTrait
      && lhs.text == rhs.text
      && lhs.textGradient == rhs.textGradient
      && lhs.maxIntrinsicNumberOfLines == rhs.maxIntrinsicNumberOfLines
      && lhs.minNumberOfHiddenLines == rhs.minNumberOfHiddenLines
      && lhs.images == rhs.images
      && lhs.accessibilityElement == rhs.accessibilityElement
  }
}

extension TextBlock.InlineImage {
  public static func ==(lhs: TextBlock.InlineImage, rhs: TextBlock.InlineImage) -> Bool {
    lhs.size == rhs.size
      && lhs.location == rhs.location
      && lhs.holder == rhs.holder
  }
}

extension TextBlock: LayoutCachingDefaultImpl {}
extension TextBlock: ElementStateUpdatingDefaultImpl {}

private func setImagePlaceholders(
  for images: [TextBlock.InlineImage],
  to string: NSAttributedString
) -> (string: NSAttributedString, attachments: [TextAttachment]) {
  guard !images.isEmpty else {
    return (string, [])
  }

  let result = string.mutableCopy() as! NSMutableAttributedString
  let attachments = images.map { _ in TextAttachment() }
  let reverseOrderImages = Array(images.enumerated()).stableSort(isLessOrEqual: {
    $0.element.location >= $1.element.location
  })
  for (offset, image) in reverseOrderImages {
    let attachment = attachments[offset]
    attachment.bounds.size = image.size
    let neighbouringParagraphStyle: NSParagraphStyle?
    if !string.isEmpty {
      let neighbouringCharPosition = max(0, image.location - 1)
      let fontAttributeValue = string.attribute(
        .font,
        at: neighbouringCharPosition,
        effectiveRange: nil
      )
      let neighbouringFont = (fontAttributeValue as? Font)
        ?? Font.systemFontWithDefaultSize()
      let currentMidY = attachment.bounds.midY
      let desiredMidY = (neighbouringFont.ascender + neighbouringFont.descender) / 2
      attachment.bounds.origin.y = desiredMidY - currentMidY

      neighbouringParagraphStyle = string.attribute(
        .paragraphStyle,
        at: neighbouringCharPosition,
        effectiveRange: nil
      ) as? NSParagraphStyle
    } else {
      neighbouringParagraphStyle = nil
    }
    attachment.image = image.tintColorImage

    let getAscent: CTRunDelegateGetAscentCallback = { p in
      let bounds = p.attachmentValue.takeUnretainedValue().bounds
      return bounds.height + bounds.minY
    }
    let getDescent: CTRunDelegateGetAscentCallback = { p in
      -p.attachmentValue.takeUnretainedValue().bounds.minY
    }
    let getWidth: CTRunDelegateGetWidthCallback = { p in
      p.attachmentValue.takeUnretainedValue().bounds.width
    }
    var callbacks = CTRunDelegateCallbacks(
      version: kCTRunDelegateCurrentVersion,
      dealloc: { _ in },
      getAscent: getAscent,
      getDescent: getDescent,
      getWidth: getWidth
    )
    let runDelegate = CTRunDelegateCreate(
      &callbacks,
      Unmanaged.passUnretained(attachment).toOpaque()
    )!

    // NB: inserting string already with attributes
    // would keep attributes of neighbouring subranges
    // which is undesired (for instance, in case of forced line height for text)
    result.insert(attachmentString, at: image.location)
    let paragraphAttributes = [
      NSAttributedString.Key.paragraphStyle: neighbouringParagraphStyle,
    ].compactMapValues { $0 }
    result.setAttributes(
      [.attachment: attachment, .ctRunDelegate: runDelegate] + paragraphAttributes,
      range: NSRange(location: image.location, length: attachmentString.length)
    )
  }

  return (result, attachments)
}

extension UnsafeMutableRawPointer {
  fileprivate var attachmentValue: Unmanaged<TextAttachment> {
    .fromOpaque(self)
  }
}

extension NSAttributedString.Key {
  fileprivate static let ctRunDelegate = kCTRunDelegateAttributeName as NSAttributedString.Key
}

private let attachmentStringChars = NSAttributedString(attachment: TextAttachment()).string
private let attachmentString = NSAttributedString(string: attachmentStringChars)
private let defaultFont = Font.systemFont(ofSize: Font.systemFontSize)

extension TextBlock.InlineImage {
  fileprivate var tintColorImage: Image? {
    let holderImage = holder.image
    guard let tintColor = tintColor else { return holderImage }
    return holderImage?.redrawn(withTintColor: tintColor)
  }
}
