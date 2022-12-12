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
        animatableView: Lazy(value: RiveContainerView(
          fit: riveData.fit,
          alignment: riveData.alignment,
          loop: riveData.loop
        )),
        widthTrait: data.widthTrait,
        heightTrait: data.heightTrait
      )
    } catch {
      assertionFailure("Failed to make block with RiveAnimationFactory")
      return SeparatorBlock()
    }
  }
}

