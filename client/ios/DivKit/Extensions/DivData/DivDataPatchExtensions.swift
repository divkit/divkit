import CommonCorePublic

extension DivData {
  public func applyPatch(_ patch: DivPatch) -> DivData {
    let states = states.map {
      State(div: $0.div.applySingleItemPatch(patch), stateId: $0.stateId)
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
  fileprivate func applySingleItemPatch(_ patch: DivPatch) -> Div {
    if let id = id, let change = patch.getChange(id: id) {
      guard let items = change.items, items.count == 1 else {
        DivKitLogger.error("Patch contains multiple items, but single item is expected: \(id)")
        return self
      }
      return items[0]
    }
    return applyPatchToChildren(patch)
  }

  fileprivate func applyOptionalItemPatch(_ patch: DivPatch) -> Div? {
    if let id = id, let change = patch.getChange(id: id) {
      guard let items = change.items else {
        return nil
      }
      if items.count == 1 {
        return items[0]
      }
      DivKitLogger.error("Patch contains multiple items, but single item is expected: \(id)")
      return self
    }
    return applyPatchToChildren(patch)
  }

  fileprivate func applyMultipleItemsPatch(_ patch: DivPatch) -> [Div] {
    if let id = id, let change = patch.getChange(id: id) {
      return change.items ?? []
    }
    return [applyPatchToChildren(patch)]
  }

  private func applyPatchToChildren(_ patch: DivPatch) -> Div {
    switch self {
    case .divCustom,
         .divGifImage,
         .divInput,
         .divImage,
         .divIndicator,
         .divSelect,
         .divSeparator,
         .divSlider,
         .divText:
      // no children
      return self
    case let .divContainer(value):
      return .divContainer(value.applyPatch(patch))
    case let .divGallery(value):
      return .divGallery(value.applyPatch(patch))
    case let .divGrid(value):
      return .divGrid(value.applyPatch(patch))
    case let .divPager(value):
      return .divPager(value.applyPatch(patch))
    case let .divState(value):
      return .divState(value.applyPatch(patch))
    case let .divTabs(value):
      return .divTabs(value.applyPatch(patch))
    }
  }
}

extension DivContainer {
  fileprivate func applyPatch(_ patch: DivPatch) -> DivContainer {
    let patchedItems = items.flatMap { $0.applyMultipleItemsPatch(patch) }
    return DivContainer(
      accessibility: accessibility,
      action: action,
      actionAnimation: actionAnimation,
      actions: actions,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      aspect: aspect,
      background: background,
      border: border,
      columnSpan: columnSpan,
      contentAlignmentHorizontal: contentAlignmentHorizontal,
      contentAlignmentVertical: contentAlignmentVertical,
      doubletapActions: doubletapActions,
      extensions: extensions,
      focus: focus,
      height: height,
      id: id,
      items: patchedItems,
      layoutMode: layoutMode,
      lineSeparator: lineSeparator,
      longtapActions: longtapActions,
      margins: margins,
      orientation: orientation,
      paddings: paddings,
      rowSpan: rowSpan,
      selectedActions: selectedActions,
      separator: separator,
      tooltips: tooltips,
      transform: transform,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivGallery {
  fileprivate func applyPatch(_ patch: DivPatch) -> DivGallery {
    let patchedItems = items.flatMap { $0.applyMultipleItemsPatch(patch) }
    return DivGallery(
      accessibility: accessibility,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      background: background,
      border: border,
      columnCount: columnCount,
      columnSpan: columnSpan,
      crossContentAlignment: crossContentAlignment,
      crossSpacing: crossSpacing,
      defaultItem: defaultItem,
      extensions: extensions,
      focus: focus,
      height: height,
      id: id,
      itemSpacing: itemSpacing,
      items: patchedItems,
      margins: margins,
      orientation: orientation,
      paddings: paddings,
      restrictParentScroll: restrictParentScroll,
      rowSpan: rowSpan,
      scrollMode: scrollMode,
      selectedActions: selectedActions,
      tooltips: tooltips,
      transform: transform,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivGrid {
  fileprivate func applyPatch(_ patch: DivPatch) -> DivGrid {
    let patchedItems = items.flatMap { $0.applyMultipleItemsPatch(patch) }
    return DivGrid(
      accessibility: accessibility,
      action: action,
      actionAnimation: actionAnimation,
      actions: actions,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      background: background,
      border: border,
      columnCount: columnCount,
      columnSpan: columnSpan,
      contentAlignmentHorizontal: contentAlignmentHorizontal,
      contentAlignmentVertical: contentAlignmentVertical,
      doubletapActions: doubletapActions,
      extensions: extensions,
      focus: focus,
      height: height,
      id: id,
      items: patchedItems,
      longtapActions: longtapActions,
      margins: margins,
      paddings: paddings,
      rowSpan: rowSpan,
      selectedActions: selectedActions,
      tooltips: tooltips,
      transform: transform,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivPager {
  fileprivate func applyPatch(_ patch: DivPatch) -> DivPager {
    let patchedItems = items.flatMap { $0.applyMultipleItemsPatch(patch) }
    return DivPager(
      accessibility: accessibility,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      background: background,
      border: border,
      columnSpan: columnSpan,
      defaultItem: defaultItem,
      extensions: extensions,
      focus: focus,
      height: height,
      id: id,
      itemSpacing: itemSpacing,
      items: patchedItems,
      layoutMode: layoutMode,
      margins: margins,
      orientation: orientation,
      paddings: paddings,
      restrictParentScroll: restrictParentScroll,
      rowSpan: rowSpan,
      selectedActions: selectedActions,
      tooltips: tooltips,
      transform: transform,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivState {
  fileprivate func applyPatch(_ patch: DivPatch) -> DivState {
    let patchedStates = states.map {
      DivState.State(
        animationIn: $0.animationIn,
        animationOut: $0.animationOut,
        div: $0.div?.applyOptionalItemPatch(patch),
        stateId: $0.stateId,
        swipeOutActions: $0.swipeOutActions
      )
    }
    return DivState(
      accessibility: accessibility,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      background: background,
      border: border,
      columnSpan: columnSpan,
      defaultStateId: defaultStateId,
      divId: divId,
      extensions: extensions,
      focus: focus,
      height: height,
      id: id,
      margins: margins,
      paddings: paddings,
      rowSpan: rowSpan,
      selectedActions: selectedActions,
      states: patchedStates,
      tooltips: tooltips,
      transform: transform,
      transitionAnimationSelector: transitionAnimationSelector,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
      visibility: visibility,
      visibilityAction: visibilityAction,
      visibilityActions: visibilityActions,
      width: width
    )
  }
}

extension DivTabs {
  fileprivate func applyPatch(_ patch: DivPatch) -> DivTabs {
    let patchedItems = items.map {
      DivTabs.Item(
        div: $0.div.applySingleItemPatch(patch),
        title: $0.title,
        titleClickAction: $0.titleClickAction
      )
    }
    return DivTabs(
      accessibility: accessibility,
      alignmentHorizontal: alignmentHorizontal,
      alignmentVertical: alignmentVertical,
      alpha: alpha,
      background: background,
      border: border,
      columnSpan: columnSpan,
      dynamicHeight: dynamicHeight,
      extensions: extensions,
      focus: focus,
      hasSeparator: hasSeparator,
      height: height,
      id: id,
      items: patchedItems,
      margins: margins,
      paddings: paddings,
      restrictParentScroll: restrictParentScroll,
      rowSpan: rowSpan,
      selectedActions: selectedActions,
      selectedTab: selectedTab,
      separatorColor: separatorColor,
      separatorPaddings: separatorPaddings,
      switchTabsByContentSwipeEnabled: switchTabsByContentSwipeEnabled,
      tabTitleStyle: tabTitleStyle,
      titlePaddings: titlePaddings,
      tooltips: tooltips,
      transform: transform,
      transitionChange: transitionChange,
      transitionIn: transitionIn,
      transitionOut: transitionOut,
      transitionTriggers: transitionTriggers,
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
