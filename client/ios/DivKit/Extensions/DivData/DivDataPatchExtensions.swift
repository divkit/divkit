import VGSL

protocol DivPatchCallbacks {
  var elementChanged: (String) -> Void { get set }
}

struct Callbacks: DivPatchCallbacks {
  var elementChanged: (String) -> Void
}

extension Callbacks {
  static var empty: Self {
    .init(elementChanged: { _ in })
  }
}

extension DivData {
  public func applyPatch(_ patch: DivPatch) -> DivData {
    applyPatch(patch, callbacks: Callbacks.empty)
  }

  func applyPatch(_ patch: DivPatch, callbacks: DivPatchCallbacks) -> DivData {
    let states = states.map {
      State(div: $0.div.applySingleItemPatch(patch, callbacks: callbacks), stateId: $0.stateId)
    }
    return DivData(
      logId: logId,
      states: states,
      timers: timers,
      transitionAnimationSelector: transitionAnimationSelector,
      variableTriggers: variableTriggers,
      variables: variables
    )
  }
}

extension Div {
  fileprivate func applySingleItemPatch(_ patch: DivPatch, callbacks: DivPatchCallbacks) -> Div {
    if let id, let change = patch.getChange(id: id) {
      guard let items = change.items, items.count == 1 else {
        DivKitLogger.error("Patch contains multiple items, but single item is expected: \(id)")
        return self
      }
      callbacks.elementChanged(id)
      return items[0]
    }
    return applyPatchToChildren(patch, callbacks: callbacks)
  }

  fileprivate func applyOptionalItemPatch(_ patch: DivPatch, callbacks: DivPatchCallbacks) -> Div? {
    if let id, let change = patch.getChange(id: id) {
      guard let items = change.items else {
        callbacks.elementChanged(id)
        return nil
      }
      if items.count == 1 {
        callbacks.elementChanged(id)
        return items[0]
      }
      DivKitLogger.error("Patch contains multiple items, but single item is expected: \(id)")
      return self
    }
    return applyPatchToChildren(patch, callbacks: callbacks)
  }

  fileprivate func applyMultipleItemsPatch(
    _ patch: DivPatch,
    callbacks: DivPatchCallbacks
  ) -> [Div] {
    if let id, let change = patch.getChange(id: id) {
      callbacks.elementChanged(id)
      return change.items ?? []
    }
    return [applyPatchToChildren(patch, callbacks: callbacks)]
  }

  private func applyPatchToChildren(_ patch: DivPatch, callbacks: DivPatchCallbacks) -> Div {
    switch self {
    case .divCustom,
         .divGifImage,
         .divInput,
         .divImage,
         .divIndicator,
         .divSelect,
         .divSeparator,
         .divSlider,
         .divSwitch,
         .divVideo,
         .divText:
      // no children
      self
    case let .divContainer(value):
      .divContainer(value.applyPatch(patch, callbacks: callbacks))
    case let .divGallery(value):
      .divGallery(value.applyPatch(patch, callbacks: callbacks))
    case let .divGrid(value):
      .divGrid(value.applyPatch(patch, callbacks: callbacks))
    case let .divPager(value):
      .divPager(value.applyPatch(patch, callbacks: callbacks))
    case let .divState(value):
      .divState(value.applyPatch(patch, callbacks: callbacks))
    case let .divTabs(value):
      .divTabs(value.applyPatch(patch, callbacks: callbacks))
    }
  }
}

extension DivContainer {
  fileprivate func applyPatch(_ patch: DivPatch, callbacks: DivPatchCallbacks) -> DivContainer {
    let patchedItems = nonNilItems
      .flatMap { $0.applyMultipleItemsPatch(patch, callbacks: callbacks) }
    return DivContainer(
      accessibility: accessibility,
      action: action,
      actionAnimation: actionAnimation,
      actions: actions,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      animators: animators,
      aspect: aspect,
      background: background,
      border: border,
      clipToBounds: clipToBounds,
      columnSpan: columnSpan,
      contentAlignmentHorizontal: contentAlignmentHorizontal,
      contentAlignmentVertical: contentAlignmentVertical,
      disappearActions: disappearActions,
      doubletapActions: doubletapActions,
      extensions: extensions,
      focus: focus,
      functions: functions,
      height: height,
      id: id,
      itemBuilder: itemBuilder,
      items: patchedItems,
      layoutMode: layoutMode,
      layoutProvider: layoutProvider,
      lineSeparator: lineSeparator,
      longtapActions: longtapActions,
      margins: margins,
      orientation: orientation,
      paddings: paddings,
      reuseId: reuseId,
      rowSpan: rowSpan,
      selectedActions: selectedActions,
      separator: separator,
      tooltips: tooltips,
      transform: transform,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      variableTriggers: variableTriggers,
      variables: variables,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivGallery {
  fileprivate func applyPatch(_ patch: DivPatch, callbacks: DivPatchCallbacks) -> DivGallery {
    let patchedItems = nonNilItems
      .flatMap { $0.applyMultipleItemsPatch(patch, callbacks: callbacks) }
    return DivGallery(
      accessibility: accessibility,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      animators: animators,
      background: background,
      border: border,
      columnCount: columnCount,
      columnSpan: columnSpan,
      crossContentAlignment: crossContentAlignment,
      crossSpacing: crossSpacing,
      defaultItem: defaultItem,
      disappearActions: disappearActions,
      extensions: extensions,
      focus: focus,
      functions: functions,
      height: height,
      id: id,
      itemBuilder: itemBuilder,
      itemSpacing: itemSpacing,
      items: patchedItems,
      layoutProvider: layoutProvider,
      margins: margins,
      orientation: orientation,
      paddings: paddings,
      restrictParentScroll: restrictParentScroll,
      reuseId: reuseId,
      rowSpan: rowSpan,
      scrollMode: scrollMode,
      scrollbar: scrollbar,
      selectedActions: selectedActions,
      tooltips: tooltips,
      transform: transform,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      variableTriggers: variableTriggers,
      variables: variables,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivGrid {
  fileprivate func applyPatch(_ patch: DivPatch, callbacks: DivPatchCallbacks) -> DivGrid {
    let patchedItems = nonNilItems
      .flatMap { $0.applyMultipleItemsPatch(patch, callbacks: callbacks) }
    return DivGrid(
      accessibility: accessibility,
      action: action,
      actionAnimation: actionAnimation,
      actions: actions,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      animators: animators,
      background: background,
      border: border,
      columnCount: columnCount,
      columnSpan: columnSpan,
      contentAlignmentHorizontal: contentAlignmentHorizontal,
      contentAlignmentVertical: contentAlignmentVertical,
      disappearActions: disappearActions,
      doubletapActions: doubletapActions,
      extensions: extensions,
      focus: focus,
      functions: functions,
      height: height,
      id: id,
      items: patchedItems,
      layoutProvider: layoutProvider,
      longtapActions: longtapActions,
      margins: margins,
      paddings: paddings,
      reuseId: reuseId,
      rowSpan: rowSpan,
      selectedActions: selectedActions,
      tooltips: tooltips,
      transform: transform,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      variableTriggers: variableTriggers,
      variables: variables,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivPager {
  fileprivate func applyPatch(_ patch: DivPatch, callbacks: DivPatchCallbacks) -> DivPager {
    let patchedItems = nonNilItems
      .flatMap { $0.applyMultipleItemsPatch(patch, callbacks: callbacks) }
    return DivPager(
      accessibility: accessibility,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      animators: animators,
      background: background,
      border: border,
      columnSpan: columnSpan,
      defaultItem: defaultItem,
      disappearActions: disappearActions,
      extensions: extensions,
      focus: focus,
      functions: functions,
      height: height,
      id: id,
      infiniteScroll: infiniteScroll,
      itemBuilder: itemBuilder,
      itemSpacing: itemSpacing,
      items: patchedItems,
      layoutMode: layoutMode,
      layoutProvider: layoutProvider,
      margins: margins,
      orientation: orientation,
      paddings: paddings,
      pageTransformation: pageTransformation,
      restrictParentScroll: restrictParentScroll,
      reuseId: reuseId,
      rowSpan: rowSpan,
      selectedActions: selectedActions,
      tooltips: tooltips,
      transform: transform,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      variableTriggers: variableTriggers,
      variables: variables,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivState {
  fileprivate func applyPatch(_ patch: DivPatch, callbacks: DivPatchCallbacks) -> DivState {
    let patchedStates = states.map {
      DivState.State(
        animationIn: $0.animationIn,
        animationOut: $0.animationOut,
        div: $0.div?.applyOptionalItemPatch(patch, callbacks: callbacks),
        stateId: $0.stateId,
        swipeOutActions: $0.swipeOutActions
      )
    }
    return DivState(
      accessibility: accessibility,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      animators: animators,
      background: background,
      border: border,
      columnSpan: columnSpan,
      defaultStateId: defaultStateId,
      disappearActions: disappearActions,
      divId: divId,
      extensions: extensions,
      focus: focus,
      functions: functions,
      height: height,
      id: id,
      layoutProvider: layoutProvider,
      margins: margins,
      paddings: paddings,
      reuseId: reuseId,
      rowSpan: rowSpan,
      selectedActions: selectedActions,
      stateIdVariable: stateIdVariable,
      states: patchedStates,
      tooltips: tooltips,
      transform: transform,
      transitionAnimationSelector: transitionAnimationSelector,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      variableTriggers: variableTriggers,
      variables: variables,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivTabs {
  fileprivate func applyPatch(_ patch: DivPatch, callbacks: DivPatchCallbacks) -> DivTabs {
    let patchedItems = items.map {
      DivTabs.Item(
        div: $0.div.applySingleItemPatch(patch, callbacks: callbacks),
        title: $0.title,
        titleClickAction: $0.titleClickAction
      )
    }
    return DivTabs(
      accessibility: accessibility,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      animators: animators,
      background: background,
      border: border,
      columnSpan: columnSpan,
      disappearActions: disappearActions,
      dynamicHeight: dynamicHeight,
      extensions: extensions,
      focus: focus,
      functions: functions,
      hasSeparator: hasSeparator,
      height: height,
      id: id,
      items: patchedItems,
      layoutProvider: layoutProvider,
      margins: margins,
      paddings: paddings,
      restrictParentScroll: restrictParentScroll,
      reuseId: reuseId,
      rowSpan: rowSpan,
      selectedActions: selectedActions,
      selectedTab: selectedTab,
      separatorColor: separatorColor,
      separatorPaddings: separatorPaddings,
      switchTabsByContentSwipeEnabled: switchTabsByContentSwipeEnabled,
      tabTitleDelimiter: tabTitleDelimiter,
      tabTitleStyle: tabTitleStyle,
      titlePaddings: titlePaddings,
      tooltips: tooltips,
      transform: transform,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      variableTriggers: variableTriggers,
      variables: variables,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivPatch {
  fileprivate func getChange(id: String) -> Change? {
    changes.first { $0.id == id }
  }
}
