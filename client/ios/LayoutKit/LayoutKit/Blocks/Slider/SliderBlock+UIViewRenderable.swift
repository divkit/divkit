import CommonCorePublic

extension SliderBlock: UIViewRenderable {
  public static func makeBlockView() -> BlockView {
    SliderView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is SliderView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let sliderView = view as! SliderView
    sliderView.setSliderModel(
      sliderModel,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}
