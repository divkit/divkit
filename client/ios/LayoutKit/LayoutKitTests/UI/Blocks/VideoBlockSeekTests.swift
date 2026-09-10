#if os(iOS)
@testable import LayoutKit
import CoreMedia
import UIKit
import VGSL
import XCTest

final class VideoBlockSeekTests: XCTestCase {
  func test_twoRapidSeeks_timeEventBetweenCompletions_doesNotUpdateElapsedTime() throws {
    let mockPlayer = MockPlayer()
    let context = VideoBlockSeekTestContext(player: mockPlayer)

    context.configure(elapsedMs: 3000)
    context.configure(elapsedMs: 1000)

    XCTAssertEqual(mockPlayer.pendingSeeks.map(\.position.value), [3000, 1000])
    let completion1 = try XCTUnwrap(mockPlayer.pendingSeeks.first).completion
    let completion2 = try XCTUnwrap(mockPlayer.pendingSeeks.last).completion

    mockPlayer.eventPipe.send(.currentTimeUpdate(2500))
    XCTAssertEqual(context.elapsedWrites, [])
    XCTAssertEqual(context.storedElapsed, 1000)

    completion1()

    mockPlayer.eventPipe.send(.currentTimeUpdate(2200))
    XCTAssertEqual(context.elapsedWrites, [])
    XCTAssertEqual(context.storedElapsed, 1000)

    completion2()

    mockPlayer.eventPipe.send(.currentTimeUpdate(1100))
    XCTAssertEqual(context.elapsedWrites, [1100])
    XCTAssertEqual(context.storedElapsed, 1100)
  }

  func test_seekWithoutCompletion_targetTimeEventResumesElapsedTimeUpdates() {
    let mockPlayer = MockPlayer()
    let context = VideoBlockSeekTestContext(player: mockPlayer)

    context.configure(elapsedMs: 4000)
    XCTAssertEqual(mockPlayer.pendingSeeks.map(\.position.value), [4000])

    mockPlayer.eventPipe.send(.currentTimeUpdate(4000))
    XCTAssertEqual(context.elapsedWrites, [4000])

    context.configure(elapsedMs: 1000)
    XCTAssertEqual(mockPlayer.pendingSeeks.map(\.position.value), [4000, 1000])

    mockPlayer.eventPipe.send(.currentTimeUpdate(3000))
    XCTAssertEqual(context.elapsedWrites, [4000])
    XCTAssertEqual(context.storedElapsed, 1000)

    mockPlayer.eventPipe.send(.currentTimeUpdate(1050))
    mockPlayer.eventPipe.send(.currentTimeUpdate(1200))
    XCTAssertEqual(context.elapsedWrites, [4000, 1050, 1200])
    XCTAssertEqual(context.storedElapsed, 1200)
  }

  func test_seekWithoutCompletion_pauseResumesElapsedTimeUpdates() {
    let mockPlayer = MockPlayer()
    let context = VideoBlockSeekTestContext(player: mockPlayer)

    context.configure(elapsedMs: 4000)
    mockPlayer.eventPipe.send(.currentTimeUpdate(1000))
    XCTAssertEqual(context.elapsedWrites, [])

    mockPlayer.eventPipe.send(.pause)
    mockPlayer.eventPipe.send(.currentTimeUpdate(1200))
    XCTAssertEqual(context.elapsedWrites, [1200])
    XCTAssertEqual(context.storedElapsed, 1200)
  }

  func test_defaultSeekCompletion_staleTimeEventDoesNotOverwriteTarget() {
    let legacyPlayer = LegacyMockPlayer()
    let context = VideoBlockSeekTestContext(player: legacyPlayer)

    context.configure(elapsedMs: 4000)
    XCTAssertEqual(legacyPlayer.seekPositions.map(\.value), [4000])

    legacyPlayer.eventPipe.send(.currentTimeUpdate(1000))
    XCTAssertEqual(context.elapsedWrites, [])
    XCTAssertEqual(context.storedElapsed, 4000)

    legacyPlayer.eventPipe.send(.currentTimeUpdate(4050))
    XCTAssertEqual(context.elapsedWrites, [4050])
    XCTAssertEqual(context.storedElapsed, 4050)
  }
}

private final class MockPlayer: Player {
  struct PendingSeek {
    let position: CMTime
    let completion: () -> Void
  }

  let eventPipe = SignalPipe<PlayerEvent>()

  var signal: Signal<PlayerEvent> { eventPipe.signal }

  private(set) var pendingSeeks: [PendingSeek] = []

  func set(data _: VideoData, config _: PlaybackConfig) {}
  func play() {}
  func pause() {}
  func set(isMuted _: Bool) {}

  func seek(to position: CMTime) {
    seek(to: position, completion: {})
  }

  func seek(to position: CMTime, completion: @escaping () -> Void) {
    pendingSeeks.append(PendingSeek(position: position, completion: completion))
  }
}

private final class VideoBlockSeekTestContext {
  private(set) var storedElapsed = 0
  private(set) var elapsedWrites: [Int] = []

  private let player: Player

  private lazy var elapsedProperty = Property<Int>(
    getter: { [unowned self] in storedElapsed },
    setter: { [unowned self] in
      storedElapsed = $0
      elapsedWrites.append($0)
    }
  )

  private lazy var elapsedBinding = Binding(name: "elapsed", value: elapsedProperty)
  private lazy var factory = MockPlayerFactory(player: player)
  private let view = VideoBlock.makeBlockView()

  init(player: Player) {
    self.player = player
  }

  func configure(elapsedMs: Int) {
    storedElapsed = elapsedMs
    VideoBlock(
      widthTrait: .fixed(100),
      heightTrait: .fixed(100),
      model: VideoBlockViewModel(
        videoData: VideoData(videos: []),
        playbackConfig: .default,
        elapsedTime: elapsedBinding,
        path: UIElementPath("test_video")
      ),
      state: VideoBlockViewState(state: .playing),
      playerFactory: factory
    ).configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)
  }
}

private final class LegacyMockPlayer: Player {
  let eventPipe = SignalPipe<PlayerEvent>()

  var signal: Signal<PlayerEvent> { eventPipe.signal }

  private(set) var seekPositions: [CMTime] = []

  func set(data _: VideoData, config _: PlaybackConfig) {}
  func play() {}
  func pause() {}
  func set(isMuted _: Bool) {}

  func seek(to position: CMTime) {
    seekPositions.append(position)
  }
}

private final class MockPlayerFactory: PlayerFactory {
  private let player: Player

  init(player: Player) {
    self.player = player
  }

  func makePlayer(data _: VideoData?, config _: PlaybackConfig?) -> Player {
    player
  }

  func makePlayerView() -> PlayerView {
    MockPlayerView()
  }
}

private final class MockPlayerView: UIView, PlayerView {
  var videoRatio: CGFloat? { nil }

  func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}
  func attach(player _: Player) {}
}

#endif
