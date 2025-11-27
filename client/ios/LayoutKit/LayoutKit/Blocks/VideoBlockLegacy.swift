import AVFoundation
import CoreGraphics
import Foundation
import VGSL

public final class VideoBlockLegacy: BlockWithTraits {
  public struct VideoAssetHolder {
    let url: URL
    let playerItem: Future<AVPlayerItem>

    public init(
      url: URL,
      playerItem: Future<AVPlayerItem>
    ) {
      self.url = url
      self.playerItem = playerItem
    }
  }

  public let videoAssetHolder: VideoAssetHolder
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let preview: ImageHolder?

  public let intrinsicContentWidth: CGFloat = 0

  let autoplayAllowed: ObservableVariable<Bool>

  public var debugDescription: String {
    "Video Block playing video with url \(videoAssetHolder.url)"
  }

  public init(
    videoAssetHolder: VideoAssetHolder,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait,
    autoplayAllowed: ObservableVariable<Bool>,
    preview: ImageHolder? = nil
  ) {
    self.videoAssetHolder = videoAssetHolder
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.autoplayAllowed = autoplayAllowed
    self.preview = preview
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat { 0 }

  public func getImageHolders() -> [ImageHolder] {
    preview.asArray()
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? VideoBlockLegacy else {
      return false
    }

    return self == other
  }
}

extension VideoBlockLegacy: LayoutCachingDefaultImpl {}
extension VideoBlockLegacy: ElementStateUpdatingDefaultImpl {}

public func ==(rhs: VideoBlockLegacy, lhs: VideoBlockLegacy) -> Bool {
  rhs.videoAssetHolder.url == lhs.videoAssetHolder.url
}
