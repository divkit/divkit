@testable import DivKit
import DivKitTestsSupport
import Foundation
import Testing

@Suite
struct DivActionIntentTests {
  @Test
  func download() {
    #expect(
      intent("div-action://download?url=https://download.url") ==
        .download(patchUrl: url("https://download.url"))
    )
  }

  @Test
  func hideTooltip() {
    #expect(
      intent("div-action://hide_tooltip?id=123") ==
        .hideTooltip(id: "123")
    )
  }

  @Test
  func showTooltip() {
    #expect(
      intent("div-action://show_tooltip?id=123") ==
        .showTooltip(id: "123", multiple: false)
    )
  }

  @Test
  func showTooltip_InvalidParams() {
    #expect(intent("div-action://show_tooltip") == nil)
  }

  @Test
  func showTooltip_InvalidScheme() {
    #expect(intent("divaction://show_tooltip?id=123") == nil)
  }

  @Test
  func setState() {
    #expect(
      intent("div-action://set_state?state_id=0/state/second") ==
        .setState(
          divStatePath: path("0/state/second"),
          lifetime: .short
        )
    )
  }

  @Test
  func setState_TemporaryIsTrue() {
    #expect(
      intent("div-action://set_state?state_id=0/state/second&temporary=true") ==
        .setState(
          divStatePath: path("0/state/second"),
          lifetime: .short
        )
    )
  }

  @Test
  func setState_TemporaryIsFalse() {
    #expect(
      intent("div-action://set_state?state_id=0/state/second&temporary=false") ==
        .setState(
          divStatePath: path("0/state/second"),
          lifetime: .long
        )
    )
  }

  @Test
  func setVariable() {
    #expect(
      intent("div-action://set_variable?name=var1&value=newvalue") ==
        .setVariable(name: "var1", value: "newvalue")
    )
  }

  @Test
  func setVariable_InvalidParams() {
    #expect(intent("div-action://set_variable?name=var1") == nil)
  }

  @Test
  func setCurrentItem() {
    #expect(
      intent("div-action://set_current_item?id=div_id&item=10") ==
        .setCurrentItem(id: "div_id", index: 10)
    )
  }

  @Test
  func setCurrentItem_InvalidItem() {
    #expect(intent("set_current_item?id=0/div_id&item=abc") == nil)
  }

  @Test
  func setNextItem() {
    #expect(
      intent("div-action://set_next_item?id=div_id&step=3&overflow=ring") ==
        .setNextItem(id: "div_id", step: 3, overflow: .ring)
    )
  }

  @Test
  func setPreviousItem() {
    #expect(
      intent("div-action://set_previous_item?id=div_id&step=3&overflow=clamp") ==
        .setPreviousItem(id: "div_id", step: 3, overflow: .clamp)
    )
  }

  @Test
  func setStoredValue_String() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=value&type=string&lifetime=100") ==
        .setStoredValue(DivStoredValue(
          name: "var",
          value: "value",
          type: .string,
          lifetimeInSec: 100
        ))
    )
  }

  @Test
  func setStoredValue_Boolean() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=true&type=boolean&lifetime=100") ==
        .setStoredValue(DivStoredValue(
          name: "var",
          value: "true",
          type: .boolean,
          lifetimeInSec: 100
        ))
    )
  }

  @Test
  func setStoredValue_Bool() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=true&type=bool&lifetime=100") ==
        .setStoredValue(DivStoredValue(
          name: "var",
          value: "true",
          type: .bool,
          lifetimeInSec: 100
        ))
    )
  }

  @Test
  func setStoredValue_WithoutValueReturnsNil() {
    #expect(
      intent("div-action://set_stored_value?name=var&type=string&lifetime=100") == nil
    )
  }

  @Test
  func setStoredValue_WithoutTypeReturnsNil() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=value&lifetime=100") == nil
    )
  }

  @Test
  func setStoredValue_WithoutLifetimeReturnsNil() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=value&type=string") == nil
    )
  }
}

private func intent(_ url: String) -> DivActionIntent? {
  DivActionIntent(url: URL(string: url)!)
}

private func path(_ path: String) -> DivStatePath {
  DivStatePath.makeDivStatePath(from: path)
}
