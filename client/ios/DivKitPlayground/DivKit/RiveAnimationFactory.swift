import Foundation

import DivKit
import DivKitExtensions
import LayoutKit
import RiveRuntime
import VGSL

final class RiveAnimationFactory: DivCustomBlockFactory {
  private let requester: URLResourceRequesting

  init(requester: URLResourceRequesting) {
    self.requester = requester
  }

  func makeBlock(data: DivCustomData, context _: DivBlockModelingContext) -> Block {
    do {
      let riveData = try data.toRiveDivCustomData()
      let animationHolder = RemoteAnimationHolder(
        url: riveData.url,
        animationType: .rive,
        requester: requester,
        localDataProvider: nil
      )

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
