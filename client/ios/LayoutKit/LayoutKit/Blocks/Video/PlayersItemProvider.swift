import AVKit
import Foundation
import NetworkingPublic

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
    if let localURL = self.getLocalResourceURL(with: url) {
      completion(AVPlayerItem(url: localURL))
    } else {
      let _ = self.getDataWithSource(from: url) { [weak self] _ in
        if let localURL = self?.getLocalResourceURL(with: url) {
          completion(AVPlayerItem(url: localURL))
        }
      }
    }
  }
}
