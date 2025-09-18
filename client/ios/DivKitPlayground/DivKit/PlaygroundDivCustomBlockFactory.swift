import DivKit
import LayoutKit
import VGSL

struct PlaygroundDivCustomBlockFactory: DivCustomBlockFactory {
  private let requester: URLResourceRequesting

  init(requester: URLResourceRequesting) {
    self.requester = requester
  }

  func makeBlock(
    data: DivCustomData,
    context: DivBlockModelingContext
  ) -> Block {
    if data.name == RiveDivCustomData.divCustomType {
      RiveAnimationFactory(requester: requester).makeBlock(data: data, context: context)
    } else {
      TextBlock(
        widthTrait: .intrinsic,
        text: "DivCustom(custom_type = \(data.name))".withTypo(size: 16, weight: .regular)
      )
    }
  }
}
