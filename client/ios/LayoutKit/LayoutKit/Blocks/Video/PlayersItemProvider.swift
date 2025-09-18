import AVKit
import Foundation
import VGSL

public protocol PlayerItemsProvider: AnyObject {
  func getAVPlayerItem(from url: URL, completion: @escaping (AVPlayerItem) -> Void)
}

public class DefaultPlayerItemsProvider: PlayerItemsProvider {
  public init() {}

  public func getAVPlayerItem(from url: URL, completion: @escaping (AVPlayerItem) -> Void) {
    completion(AVPlayerItem(url: url))
  }
}

extension CachedURLResourceRequester: PlayerItemsProvider {
  public func getAVPlayerItem(from url: URL, completion: @escaping (AVPlayerItem) -> Void) {
    let streamingVideoExtensions = ["m3u8", "m3u"]
    guard !streamingVideoExtensions.contains(url.pathExtension) else {
      completion(AVPlayerItem(url: url)) // Cannot cache "m3u8", load online
      return
    }

    if let localURL = self.getLocalResourceURL(with: url) {
      completion(AVPlayerItem(url: localURL))
    } else {
      _ = self.getDataWithSource(from: url) { [weak self] _ in
        if let localURL = self?.getLocalResourceURL(with: url) {
          completion(AVPlayerItem(url: localURL))
        } else {
          completion(AVPlayerItem(url: url)) // Unable to cache file, invalid URL assumed
        }
      }
    }
  }
}
