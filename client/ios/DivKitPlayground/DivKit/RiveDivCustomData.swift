import DivKit
import Foundation

struct RiveDivCustomData {
  enum Fit: String {
    case cover = "Cover"
    case contain = "Contain"
    case fill = "Fill"
    case fitWidth = "FitWidth"
    case fitHeight = "FitHeight"
    case none = "None"
    case scaleDown = "ScaleDown"
  }

  enum Alignment: String {
    case center = "Center"
    case topLeft = "TopLeft"
    case topCenter = "TopCenter"
    case topRight = "TopRight"
    case centerLeft = "CenterLeft"
    case centerRight = "CenterRight"
    case bottomLeft = "BottomLeft"
    case bottomCenter = "BottomCenter"
    case bottomRight = "BottomRight"
  }

  enum Loop: String {
    case oneShot = "OneShot"
    case loop = "Loop"
    case pingPong = "PingPong"
    case auto = "Auto"
  }

  enum Error: String, Swift.Error {
    case wrongDivCustomType
    case noValidURL
  }

  static let divCustomType = "rive_animation"
  let url: URL
  let fit: Fit
  let alignment: Alignment
  let loop: Loop

  init(name: String, data: [String: Any]) throws {
    guard name == RiveDivCustomData.divCustomType else {
      throw Error.wrongDivCustomType
    }
    guard let urlStr = data[riveFileURLKey] as? String, let url = URL(string: urlStr) else {
      throw Error.noValidURL
    }
    self.alignment = (data[riveAlignmentKey] as? String)
      .flatMap(Alignment.init(rawValue:)) ?? .center
    self.fit = (data[riveFitKey] as? String).flatMap(Fit.init(rawValue:)) ?? .contain
    self.loop = (data[riveLoopKey] as? String).flatMap(Loop.init(rawValue:)) ?? .auto
    self.url = url
  }
}

extension DivCustomData {
  func toRiveDivCustomData() throws -> RiveDivCustomData {
    try RiveDivCustomData(name: name, data: data)
  }
}

private let riveFileURLKey = "url"
private let riveFitKey = "fit"
private let riveAlignmentKey = "alignment"
private let riveLoopKey = "loop"
