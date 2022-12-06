import Foundation

import Base
import LayoutKit
import DivKit
import DivKitExtensions
import Networking
import RiveRuntime

final class RiveAnimationFactory: DivCustomBlockFactory {
  private let requester: URLResourceRequesting

  init(requester: URLResourceRequesting) {
    self.requester = requester
  }

  func makeBlock(data: DivCustomData, context: DivBlockModelingContext) -> Block {
    do {
      let riveData = try data.toRiveDivCustomData()
      let animationHolder = RemoteAnimationHolder(
        url: riveData.url,
        animationType: .rive,
        requester: requester,
        localDataProvider: nil)

      return RiveAnimationBlock(
        animationHolder: animationHolder,
        animatableView: Lazy(value: RiveContainerView()),
        widthTrait: data.widthTrait,
        heightTrait: data.heightTrait
      )
    } catch {
      assertionFailure("Failed to make block with RiveAnimationFactory")
      return SeparatorBlock()
    }
  }
}

private final class RiveContainerView: UIView {
  private var riveViewModel: RiveViewModel?
  private var riveView: RiveView? {
    didSet {
      if let riveView = riveView {
        addSubview(riveView)
        riveView.frame = bounds
      }
    }
  }

  init() {
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  override func layoutSubviews() {
    super.layoutSubviews()
    if let riveView = riveView {
      riveView.frame = bounds
    }
  }
}

extension RiveContainerView: AnimatableView {
  func play() {
    riveViewModel?.play()
  }

  func setSource(_ source: AnimationSourceType) {
    if let source = source as? RiveAnimationSourceType {
      switch source {
      case .data(let data):
        if let riveFile = try? RiveFile(byteArray: [UInt8](data)) {
          let model = RiveModel(riveFile: riveFile)
          riveViewModel = RiveViewModel(model)
          riveView = riveViewModel?.createRiveView()
        }
      }
    }
  }
}

struct RiveDivCustomData {
  enum Error: String, Swift.Error {
    case wrongDivCustomType
    case noValidURL
  }

  static let divCustomType = "rive_animation"
  let url: URL

  init(name: String, data: [String: Any]) throws {
    guard name == RiveDivCustomData.divCustomType else {
      throw Error.wrongDivCustomType
    }
    guard let urlStr = data["url"] as? String, let url = URL(string: urlStr) else {
      throw Error.noValidURL
    }
    self.url = url
  }
}

private extension DivCustomData {
  func toRiveDivCustomData() throws -> RiveDivCustomData {
    try RiveDivCustomData(name: name, data: data)
  }
}
