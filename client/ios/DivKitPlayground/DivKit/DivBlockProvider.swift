import UIKit

import BaseUI
import CommonCore
import DivKit
import LayoutKit
import Serialization

typealias JsonProvider = () throws -> [String: Any]

struct ObservableJsonProvider {
  @ObservableProperty
  private var provider: JsonProvider = { [:] }

  var signal: Signal<JsonProvider> {
    $provider.currentAndNewValues
  }

  func load(url: URL) {
    provider = {
      try Data(contentsOf: url).asJsonDictionary()
    }
  }
}

extension Data {
  func asJsonDictionary() throws -> [String: Any] {
    try JSONSerialization.jsonObject(with: self, options: []) as! [String: Any]
  }
}

final class DivBlockProvider {
  private let divKitComponents: DivKitComponents
  private let disposePool = AutodisposePool()

  private var divData: DivData?
  public weak var parentScrollView: ScrollView?
  let divRenderTime = TimeMeasure()
  let divDataParsingTime = TimeMeasure()
  let divTemplateParsingTime = TimeMeasure()

  @ObservableProperty
  private(set) var block: Block = noDataBlock
  @ObservableProperty
  private(set) var errors: UIStatePayloadFactory.Errors? = nil

  init(
    json: Signal<JsonProvider>,
    divKitComponents: DivKitComponents,
    shouldResetOnDataChange: Bool
  ) {
    self.divKitComponents = divKitComponents

    json
      .addObserver { [weak self] in
        if shouldResetOnDataChange {
          self?.divKitComponents.reset()
        }
        self?.update(jsonProvider: $0)
      }
      .dispose(in: disposePool)
  }

  func update(patch: DivPatch? = nil) {
    guard var divData = divData else {
      return
    }

    if let patch = patch {
      divData = divData.applyPatch(patch)
      self.divData = divData
    }

    do {
      divRenderTime.start()
      let newBlock = try divData.makeBlock(
        context: divKitComponents.makeContext(
          cardId: cardId,
          cachedImageHolders: block.getImageHolders(),
          debugParams: AppComponents.debugParams,
          parentScrollView: parentScrollView
        )
      )
      divRenderTime.stop()
      block = newBlock.addingTime(
        dataParsing: divDataParsingTime.time,
        templateParsing: divTemplateParsingTime.time,
        render: divRenderTime.time
      )
    } catch {
      block = makeErrorBlock("\(error)")
      errors = [(message: error.localizedDescription, stack: nil)]
      DemoAppLogger.error("Failed to build block: \(error)")
    }
  }

  private func update(jsonProvider: JsonProvider) {
    divData = nil
    block = noDataBlock
    errors = []

    do {
      let json = try jsonProvider()
      if json.isEmpty {
        return
      }

      let palette = Palette(json: try json.getOptionalField("palette") ?? [:])
      divKitComponents.variablesStorage
        .set(
          variables: palette.makeVariables(theme: UserPreferences.playgroundTheme),
          triggerUpdate: false
        )

      let result = try parseDivDataWithTemplates(json, cardId: cardId)

      divData = result.value
      errors = result.errorsOrWarnings.map {
        $0.map {
          let traceback = $0.traceback
          return (message: traceback.last!, stack: traceback)
        }
      }
    } catch {
      block = makeErrorBlock("\(error)")
      errors = [(message: error.localizedDescription, stack: nil)]
      DemoAppLogger.error("Failed to parse DivData: \(error)")
      return
    }

    update(patch: nil)
  }

  private func parseDivDataWithTemplates(
    _ jsonDict: [String: Any],
    cardId: DivCardID
  ) throws -> DeserializationResult<DivData> {
    let rawDivData = try RawDivData(dictionary: jsonDict)
    divTemplateParsingTime.start()
    let templates = DivTemplates(dictionary: rawDivData.templates)
    divTemplateParsingTime.stop()
    divDataParsingTime.start()
    let result = templates.parseValue(type: DivDataTemplate.self, from: rawDivData.card)
    if let divData = result.value {
      divKitComponents.setVariablesAndTriggers(divData: divData, cardId: cardId)
      divKitComponents.setTimers(divData: divData, cardId: cardId)
    }
    divDataParsingTime.stop()
    return result
  }
}

private let cardId: DivCardID = "sample_card"

private let noDataBlock = EmptyBlock.zeroSized

private func makeErrorBlock(_ text: String) -> Block {
  TextBlock(
    widthTrait: .resizable,
    text: text.with(typo: Typo(size: 18, weight: .regular))
  ).addingEdgeGaps(20)
}

extension Block {
  func addingTime(
    dataParsing: TimeMeasure.Time?,
    templateParsing: TimeMeasure.Time?,
    render: TimeMeasure.Time?
  ) -> Block {
    guard UserPreferences.showRenderingTime,
          let render = render,
          let dataParsing = dataParsing,
          let templateParsing = templateParsing else {
      return self
    }

    let text =
      """
      Rendering time:

      - Div.Render.Total.\(render.description) ms
      - Div.Parsing.Data.\(dataParsing.description) ms
      - Div.Parsing.Templates.\(templateParsing.description) ms
      """

    let textBlock = TextBlock(
      widthTrait: .resizable,
      text: text.with(typo: Typo(size: 18, weight: .regular))
    ).addingEdgeGaps(20)

    let block = try? ContainerBlock(
      layoutDirection: .vertical,
      children: [self, textBlock]
    )

    return block ?? self
  }
}

extension TimeMeasure.Time {
  fileprivate var description: String {
    "\(status.rawValue.capitalized): \(value)"
  }
}

extension DeserializationError {
  fileprivate var traceback: [String] {
    var result: [String] = []
    switch self {
    case let .nestedObjectError(fieldName, error):
      result.append("Error in nested object in field '\(fieldName)'")
      result.append(contentsOf: error.traceback)
    default:
      result.append(description)
    }
    return result
  }
}
