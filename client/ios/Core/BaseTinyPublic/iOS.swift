// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import UIKit

public typealias Color = RGBAColor
public typealias SystemColor = UIColor

public typealias SystemShadow = NSShadow

public typealias EdgeInsets = UIEdgeInsets

extension NSShadow {
  public var cgColor: CGColor? { (shadowColor as? UIColor)?.cgColor }
}

public typealias Font = UIFont

public typealias UnderlineStyle = NSUnderlineStyle
public typealias TextAttachment = NSTextAttachment
public typealias TextAlignment = NSTextAlignment
public typealias LineBreakMode = NSLineBreakMode
public typealias WritingDirection = NSWritingDirection
public typealias TextTab = NSTextTab

public typealias NSParagraphStyle = UIKit.NSParagraphStyle
public typealias NSMutableParagraphStyle = UIKit.NSMutableParagraphStyle

public typealias Image = UIImage

extension Image {
  public func binaryRepresentation() -> Data? {
    pngData()
  }
}

extension UIImage {
  public class func imageOfSize(
    _ size: CGSize,
    opaque: Bool = false,
    scale: CGFloat = 0,
    orientation: UIImage.Orientation = .up,
    transformForUIKitCompatibility: Bool = true,
    drawingHandler: (CGContext) -> Void
  ) -> UIImage? {
    let actualScale = scale.isZero ? UIScreen.main.scale : scale
    let width = Int(size.width * actualScale)
    let height = Int(size.height * actualScale)
    let alphaInfo = opaque ? CGImageAlphaInfo.noneSkipFirst : CGImageAlphaInfo.premultipliedFirst
    let bitmapInfo = CGBitmapInfo.byteOrder32Little
    guard let ctx = CGContext(
      data: nil,
      width: width,
      height: height,
      bitsPerComponent: 8,
      bytesPerRow: width * 4,
      space: CGColorSpaceCreateDeviceRGB(),
      bitmapInfo: alphaInfo.rawValue | bitmapInfo.rawValue
    ) else {
      return nil
    }

    if transformForUIKitCompatibility {
      ctx.translateBy(x: 0, y: CGFloat(height))
      ctx.scaleBy(x: actualScale, y: -actualScale)
    } else {
      ctx.scaleBy(x: actualScale, y: actualScale)
    }

    ctx.textMatrix = CGAffineTransform(scaleX: -1, y: 1)
    drawingHandler(ctx)
    guard let image = ctx.makeImage() else {
      return nil
    }

    return UIImage(cgImage: image, scale: actualScale, orientation: orientation)
  }
}

extension Image {
  public var cgImg: CGImage? {
    cgImage
  }
}

public enum PlatformDescription {
  public static func screenScale() -> CGFloat {
    UIScreen.main.scale
  }
}

extension Font {
  public class func systemFontWithDefaultSize() -> Font {
    defaultFont
  }

  public func defaultLineHeight() -> CGFloat {
    self.lineHeight
  }

  public static let emojiFontName = "AppleColorEmoji"
}

extension SystemColor {
  public var rgba: RGBAColor {
    var red: CGFloat = 0
    var green: CGFloat = 0
    var blue: CGFloat = 0
    var alpha: CGFloat = 0
    guard getRed(&red, green: &green, blue: &blue, alpha: &alpha) else {
      preconditionFailure()
    }

    return RGBAColor(red: red, green: green, blue: blue, alpha: alpha)
  }
}

extension RGBAColor {
  public var white: CGFloat {
    var white: CGFloat = 0
    systemColor.getWhite(&white, alpha: nil)
    return white
  }
}

extension URL {
  public static let applicationOpenSettingsURL = URL(string: UIApplication.openSettingsURLString)!
}

#if os(iOS)
private let defaultFont = Font.systemFont(ofSize: Font.systemFontSize)
#else
private let defaultFont = Font.systemFont(ofSize: 12)
#endif
