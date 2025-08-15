#if os(iOS)
import UIKit

@objc(ComposeDivKitFactory) public class ComposeDivKitFactory: NSObject {
  @objc public static func makeDivKitView(_ jsonString: String) -> UIView {
    let view = DivView(divKitComponents: DivKitComponents())
    Task {
      await view.setSource(DivViewSource(kind: .data(Data(jsonString.utf8)), cardId: "Sample"))
    }
    return view
  }
}
#endif
