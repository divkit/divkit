import VGSL

protocol DelayedVisibilityActionView where Self: BlockView {
  var visibilityAction: Action? { get set }
}

extension DelayedVisibilityActionView {
  func applyVisibilityAction() {
    visibilityAction?()
    visibilityAction = nil
  }
}
