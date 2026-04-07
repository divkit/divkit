#if os(iOS)
import AVFoundation
import Foundation
import LayoutKit
import UIKit
import VGSL

final class PlaygroundPlayerFactory: PlayerFactory {
  private let itemsProvider: PlayerItemsProvider

  init(itemsProvider: PlayerItemsProvider) {
    self.itemsProvider = itemsProvider
  }

  func makePlayer(data: VideoData?, config: PlaybackConfig?) -> Player {
    let player = PlaygroundPlayer(
      defaultPlayer: DefaultPlayerFactory(itemsProvider: itemsProvider).makePlayer(
        data: nil,
        config: nil
      )
    )
    guard let config, let data else { return player }
    player.set(data: data, config: config)
    return player
  }

  func makePlayerView() -> PlayerView {
    PlaygroundPlayerView(
      defaultView: DefaultPlayerFactory(itemsProvider: itemsProvider)
        .makePlayerView() as! DefaultPlayerView
    )
  }
}

final class PlaygroundPlayerView: UIView, PlayerView {
  private let defaultView: DefaultPlayerView

  var videoRatio: CGFloat? {
    defaultView.videoRatio
  }

  init(defaultView: DefaultPlayerView) {
    self.defaultView = defaultView
    super.init(frame: .zero)
    addSubview(defaultView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    defaultView.frame = bounds
  }

  func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}

  func attach(player: Player) {
    guard let playgroundPlayer = player as? PlaygroundPlayer else {
      defaultView.attach(player: player)
      return
    }
    defaultView.attach(player: playgroundPlayer.defaultPlayer)
  }

  func set(scale: VideoScale) {
    defaultView.set(scale: scale)
  }
}

final class PlaygroundPlayer: Player {
  fileprivate let defaultPlayer: Player

  var signal: Signal<PlayerEvent> {
    defaultPlayer.signal
  }

  init(defaultPlayer: Player) {
    self.defaultPlayer = defaultPlayer
  }

  func set(data: VideoData, config: PlaybackConfig) {
    let resolvedData: VideoData = if data.videos.isEmpty,
                                     let urlString = config.settingsPayload["url"] as? String,
                                     let url = URL(string: urlString) {
      VideoData(videos: [Video(url: url, mimeType: "video/mp4")])
    } else {
      data
    }
    defaultPlayer.set(data: resolvedData, config: config)
  }

  func play() {
    defaultPlayer.play()
  }

  func pause() {
    defaultPlayer.pause()
  }

  func set(isMuted: Bool) {
    defaultPlayer.set(isMuted: isMuted)
  }

  func seek(to position: CMTime) {
    defaultPlayer.seek(to: position)
  }
}
#endif
