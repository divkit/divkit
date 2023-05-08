import BaseTinyPublic
import UIKit

public struct VideoBlockViewModel: Equatable {
  public let videoData: VideoData
  public let playbackConfig: PlaybackConfig
  public var elapsedTime: Binding<Int>?
  public let preview: Image?
  public let resumeActions: [UserInterfaceAction]
  public let bufferingActions: [UserInterfaceAction]
  public let endActions: [UserInterfaceAction]
  public let path: UIElementPath

  public init(
    videoData: VideoData,
    playbackConfig: PlaybackConfig,
    preview: Image? = nil,
    elapsedTime: Binding<Int>? = nil,
    resumeActions: [UserInterfaceAction] = [],
    bufferingActions: [UserInterfaceAction] = [],
    endActions: [UserInterfaceAction] = [],
    path: UIElementPath
  ) {
    self.videoData = videoData
    self.playbackConfig = playbackConfig
    self.preview = preview
    self.elapsedTime = elapsedTime
    self.resumeActions = resumeActions
    self.bufferingActions = bufferingActions
    self.endActions = endActions
    self.path = path
  }

  public static func ==(lhs: VideoBlockViewModel, rhs: VideoBlockViewModel) -> Bool {
    lhs.videoData == rhs.videoData
  }
}
