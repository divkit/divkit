import CoreMedia
@testable @_spi(Internal) import DivKit
import LayoutKit
import UIKit
import VGSL
import XCTest

@MainActor
final class VideoPagerPlaybackTests: XCTestCase {
  func test_SmallCancelledSwipe_KeepsCurrentVideoPlaying() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render()

    try await fixture.cancelSmallSwipe()

    XCTAssertEqual(fixture.players.playingVideoIds, ["first"])
  }

  func test_NextVideoBecomesVisible_OnlyNextVideoPlays() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render()

    try await fixture.revealNextVideo()

    XCTAssertEqual(fixture.players.playingVideoIds, ["second"])
  }

  func test_ScrollEnds_NextVideoKeepsPlaying() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render()
    try await fixture.revealNextVideo()

    try await fixture.finishSwipe()

    XCTAssertEqual(fixture.players.playingVideoIds, ["second"])
  }

  func test_ReturnToPreviousPage_PreviousVideoPlaysAgain() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render()
    try await fixture.revealNextVideo()
    try await fixture.finishSwipe()

    try await fixture.returnToFirstVideo()

    XCTAssertEqual(fixture.players.playingVideoIds, ["first"])
  }

  func test_UrlStartAction_PausesOtherVideoInPager() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render()

    await fixture.startVideo(id: "second", usingUrl: true)

    XCTAssertEqual(fixture.players.playingVideoIds, ["second"])
  }

  func test_TypedStartAction_PausesOtherVideoInPager() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render()

    await fixture.startVideo(id: "second", usingUrl: false)

    XCTAssertEqual(fixture.players.playingVideoIds, ["second"])
  }

  func test_RapidStartActionsInWindow_LastSelectedVideoKeepsPlaying() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render()
    fixture.attachToWindow()

    fixture.startVideoImmediately(id: "second")
    fixture.startVideoImmediately(id: "first")
    await fixture.drainPlayerEvents()

    XCTAssertEqual(fixture.players.playingVideoIds, ["first"])
  }

  func test_RapidStartActionsInWindow_PlaybackStopsChanging() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render()
    fixture.attachToWindow()
    fixture.startVideoImmediately(id: "second")
    fixture.startVideoImmediately(id: "first")
    await fixture.drainPlayerEvents()
    let commandCount = fixture.players.playbackCommandCount

    await fixture.drainPlayerEvents()

    XCTAssertEqual(fixture.players.playbackCommandCount, commandCount)
  }

  func test_PlayerStartsInWindow_PausesOtherVideoInPager() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render()
    fixture.attachToWindow()

    try fixture.players.startVideo(id: "second")
    await fixture.drainPlayerEvents()

    XCTAssertEqual(fixture.players.playingVideoIds, ["second"])
  }

  func test_StartInOtherPager_DoesNotPauseFirstPager() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render(div: makeVideoColumn([
      makeVideoPager(ids: ["first", "second"]),
      makeVideoPager(ids: ["other_first", "other_second"]),
    ]))

    await fixture.startVideo(id: "other_second", usingUrl: false)

    XCTAssertEqual(fixture.players.playingVideoIds, ["first", "other_second"])
  }

  func test_StartInPager_PausesAutostartedVideoWithoutPlayerEvents() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render(div: makeVideoPager(items: [
      makeAutostartVideo(id: "first"), makeVideoWithoutVisibilityActions(id: "second"),
    ]))

    await fixture.startVideo(id: "second", usingUrl: false)

    XCTAssertEqual(fixture.players.playingVideoIds, ["second"])
  }

  func test_StartInPager_DoesNotPauseVideoOutsidePager() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render(div: makeVideoColumn([
      makeVideoPager(ids: ["first", "second"]),
      makePagerVideo(id: "outside"),
    ]))

    await fixture.startVideo(id: "second", usingUrl: false)

    XCTAssertEqual(fixture.players.playingVideoIds, ["outside", "second"])
  }

  func test_StartInGallery_DoesNotPauseOtherVideo() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render(div: makeVideoGallery(ids: ["first", "second"]))

    await fixture.startVideo(id: "second", usingUrl: false)

    XCTAssertEqual(fixture.players.playingVideoIds, ["first", "second"])
  }

  func test_StartInOuterPager_DoesNotPauseNestedPager() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render(div: makeVideoPager(items: [
      makeVideoPager(ids: ["first", "second"]), makePagerVideo(id: "outer_next"),
    ]))

    await fixture.startVideo(id: "outer_next", usingUrl: false)

    XCTAssertEqual(fixture.players.playingVideoIds, ["first", "outer_next"])
  }

  func test_CardReset_RemovingPagerKeepsVideosIndependent() async throws {
    let fixture = VideoPagerFixture()
    try await fixture.render()

    try await fixture.resetAndRenderContainer()

    XCTAssertEqual(fixture.players.playingVideoIds, ["first", "second"])
  }
}

@MainActor
private final class VideoPagerFixture {
  let players = PagerPlayerFactory()

  private lazy var components = DivKitComponents(playerFactory: players)
  private lazy var view = DivView(divKitComponents: components)
  private var window: UIWindow?

  func attachToWindow() {
    let window = UIWindow(frame: view.frame)
    window.addSubview(view)
    self.window = window
  }

  func startVideoImmediately(id: String) {
    let action = DivAction(logId: .value("start"), typed: .divActionVideo(DivActionVideo(
      action: .value(.start), id: .value(id)
    )))
    components.actionHandler.handle(action, path: "videos", source: .tap, sender: nil)
    components.forceUpdate()
    view.forceLayout()
  }

  func drainPlayerEvents() async {
    await withCheckedContinuation { continuation in
      DispatchQueue.main.async {
        continuation.resume()
      }
    }
    components.forceUpdate()
    view.forceLayout()
    await withCheckedContinuation { continuation in
      DispatchQueue.main.async {
        continuation.resume()
      }
    }
  }

  func render(div: [String: Any] = makeVideoPager(ids: ["first", "second"])) async throws {
    await view.setSource(.init(kind: .json([
      "card": ["log_id": "videos", "states": [["state_id": 0, "div": div]]],
    ]), cardId: "videos"))
    view.frame = CGRect(x: 0, y: 0, width: 300, height: 300)
    await settleRendering()
    view.onVisibleBoundsChanged(to: view.bounds)
    await settleRendering()
    XCTAssertTrue(players.playingVideoIds.contains("first"))
  }

  func resetAndRenderContainer() async throws {
    components.reset(cardId: "videos")
    try await render(div: makeVideoColumn([
      makePagerVideo(id: "first"), makePagerVideo(id: "second"),
    ]))
  }

  func cancelSmallSwipe() async throws {
    try await moveToScrollFraction(0.1)
    try await moveToScrollFraction(0)
    try await finishSwipe()
  }

  func revealNextVideo() async throws {
    try await moveToScrollFraction(0.45)
    try await moveToScrollFraction(0.6)
  }

  func returnToFirstVideo() async throws {
    try await moveToScrollFraction(0.6)
    try await moveToScrollFraction(0.45)
    try await moveToScrollFraction(0)
    try await finishSwipe()
  }

  func finishSwipe() async throws {
    let collection = try XCTUnwrap(findVideoCollection(in: view))
    collection.delegate?.scrollViewDidEndDecelerating?(collection)
    await settleRendering()
  }

  func startVideo(id: String, usingUrl: Bool) async {
    let action = if usingUrl {
      DivAction(logId: .value("start"), url: .value(
        URL(string: "div-action://video?id=\(id)&action=start")!
      ))
    } else {
      DivAction(logId: .value("start"), typed: .divActionVideo(DivActionVideo(
        action: .value(.start), id: .value(id)
      )))
    }
    components.actionHandler.handle(action, path: "videos", source: .tap, sender: nil)
    await settleRendering()
  }

  private func moveToScrollFraction(_ fraction: CGFloat) async throws {
    let collection = try XCTUnwrap(findVideoCollection(in: view))
    let range = collection.contentSize.width - collection.bounds.width
    collection.setContentOffset(CGPoint(x: range * fraction, y: 0), animated: false)
    await settleRendering()
  }

  private func settleRendering() async {
    view.forceLayout()
    await skipMainRunLoopCycles(view.visibilityHierarchyDepth() + 2)
    components.forceUpdate()
    view.forceLayout()
    await skipMainRunLoopCycles(view.visibilityHierarchyDepth() + 2)
  }
}

private func findVideoCollection(in view: UIView) -> UICollectionView? {
  if let collection = view as? UICollectionView {
    return collection
  }
  for child in view.subviews {
    if let collection = findVideoCollection(in: child) {
      return collection
    }
  }
  return nil
}

private func makeVideoColumn(_ items: [[String: Any]]) -> [String: Any] {
  ["type": "container", "items": items, "height": ["type": "wrap_content"]]
}

private func makeVideoPager(ids: [String]) -> [String: Any] {
  makeVideoPager(items: ids.map { makePagerVideo(id: $0) })
}

private func makeVideoPager(items: [[String: Any]]) -> [String: Any] {
  [
    "type": "pager",
    "layout_mode": ["type": "fixed", "neighbour_page_width": ["type": "fixed", "value": 10]],
    "paddings": ["left": 4, "right": 4],
    "item_spacing": ["type": "fixed", "value": 4],
    "items": items,
  ]
}

private func makeVideoGallery(ids: [String]) -> [String: Any] {
  ["type": "gallery", "items": ids.map { makeGalleryVideo(id: $0) }]
}

private func makeGalleryVideo(id: String) -> [String: Any] {
  var video = makePagerVideo(id: id)
  video["width"] = ["type": "fixed", "value": 100]
  return video
}

private func makePagerVideo(id: String) -> [String: Any] {
  [
    "type": "video", "id": id, "autostart": false,
    "height": ["type": "fixed", "value": 100],
    "video_sources": [["url": "https://video.test/\(id).mp4", "mime_type": "video/mp4"]],
    "visibility_actions": [[
      "log_id": "\(id)_shown", "visibility_percentage": 50, "visibility_duration": 0,
      "log_limit": 0, "url": "div-action://video?id=\(id)&action=start",
    ]],
    "disappear_actions": [[
      "log_id": "\(id)_hidden", "visibility_percentage": 50, "disappear_duration": 0,
      "log_limit": 0, "url": "div-action://video?id=\(id)&action=pause",
    ]],
  ]
}

private func makeVideoWithoutVisibilityActions(id: String) -> [String: Any] {
  var video = makePagerVideo(id: id)
  video.removeValue(forKey: "visibility_actions")
  video.removeValue(forKey: "disappear_actions")
  return video
}

private func makeAutostartVideo(id: String) -> [String: Any] {
  var video = makeVideoWithoutVisibilityActions(id: id)
  video["autostart"] = true
  return video
}

private final class PagerPlayerFactory: PlayerFactory {
  private var players: [PagerTestPlayer] = []

  var playingVideoIds: [String] {
    players.filter(\.isPlaying).map(\.videoId).sorted()
  }

  var playbackCommandCount: Int {
    players.reduce(0) { $0 + $1.playbackCommandCount }
  }

  func startVideo(id: String) throws {
    try XCTUnwrap(players.first { $0.videoId == id }).play()
  }

  func makePlayer(data: VideoData?, config: PlaybackConfig?) -> Player {
    let player = PagerTestPlayer()
    if let data, let config {
      player.set(data: data, config: config)
    }
    players.append(player)
    return player
  }

  func makePlayerView() -> PlayerView {
    PagerTestPlayerView()
  }
}

private final class PagerTestPlayer: Player {
  private(set) var videoId = ""
  private(set) var isPlaying = false
  private(set) var playbackCommandCount = 0

  private let events = SignalPipe<PlayerEvent>()

  var signal: Signal<PlayerEvent> { events.signal }

  func set(data: VideoData, config: PlaybackConfig) {
    videoId = data.videos.first?.url.deletingPathExtension().lastPathComponent ?? ""
    isPlaying = config.autoPlay
  }

  func play() {
    playbackCommandCount += 1
    isPlaying = true
    events.send(.play)
  }

  func pause() {
    playbackCommandCount += 1
    isPlaying = false
    events.send(.pause)
  }

  func seek(to _: CMTime) {}
  func set(isMuted _: Bool) {}
}

private final class PagerTestPlayerView: UIView, PlayerView {
  var videoRatio: CGFloat? { nil }

  func attach(player _: Player) {}
  func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}
}
