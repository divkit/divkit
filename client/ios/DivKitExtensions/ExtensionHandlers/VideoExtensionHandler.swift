import Foundation

import BasePublic
import DivKit
import LayoutKit

public final class VideoExtensionHandler: DivExtensionHandler {
  public var id: String = extensionID

  public init() { }

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard let block = block as? VideoBlock else {
      return block
    }
    let extensionParams = getExtensionParams(div)
    let durationVariableName: DivVariableName? = try? extensionParams.getOptionalField(durationKey)
    guard let durationVariableName else {
      DivKitLogger.error("No valid params for VideoExtensionHandler")
      return block
    }
    return VideoBlock(
      widthTrait: block.widthTrait,
      heightTrait: block.heightTrait,
      model: block.model.copy(with: context.makeBinding(variableName: durationVariableName)),
      state: block.state,
      playerFactory: block.playerFactory
    )
  }
}

extension VideoBlockViewModel {
  fileprivate func copy(with duration: Binding<Int>) -> VideoBlockViewModel {
    VideoBlockViewModel(
      videoData: videoData,
      playbackConfig: playbackConfig,
      preview: preview,
      elapsedTime: elapsedTime,
      duration: duration,
      resumeActions: resumeActions,
      pauseActions: pauseActions,
      bufferingActions: bufferingActions,
      endActions: endActions,
      fatalActions: fatalActions,
      path: path,
      scale: scale
    )
  }
}

extension DivBlockModelingContext {
  fileprivate func makeBinding(variableName: DivVariableName) -> Binding<Int> {
    let valueProp = Property<Int>(
      getter: {
        variablesStorage.getVariableValue(cardId: cardId, name: variableName) ?? 0
      },
      setter: { value in
        self.variablesStorage.update(
          cardId: cardId,
          name: variableName,
          value: String(value)
        )
      }
    )
    return Binding(name: variableName.rawValue, value: valueProp)
  }
}

private let extensionID = "videoDuration"
private let durationKey = "variable"
