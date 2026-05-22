import LayoutKit
import VGSL
import XCTest

final class GradientStopsPremultipliedTests: XCTestCase {
  func test_EmptyArray_ReturnsEmpty() {
    let stops: [Gradient.Point] = []
    XCTAssertEqual(stops.premultipliedSubdivided(), [])
  }

  func test_SingleStop_ReturnedUnchanged() {
    let stops: [Gradient.Point] = [
      (.argb(0xFF_FF_00_00), 0),
    ]
    XCTAssertEqual(stops.premultipliedSubdivided(), stops)
  }

  func test_CanonicalTransparentToOpaque_RewritesTransparentRGB_NoSubdivision() {
    // #0000 → #FFFFFFFF: step 1 inherits white into the transparent stop, then
    // step 2 sees equal RGB on both sides and skips subdivision.
    let stops: [Gradient.Point] = [
      (.argb(0x00_00_00_00), 0),
      (.argb(0xFF_FF_FF_FF), 1),
    ]
    let expected: [Gradient.Point] = [
      (.argb(0x00_FF_FF_FF), 0),
      (.argb(0xFF_FF_FF_FF), 1),
    ]
    XCTAssertEqual(stops.premultipliedSubdivided(), expected)
  }

  func test_EqualAlpha_NoSubdivision() {
    let stops: [Gradient.Point] = [
      (.argb(0x80_FF_00_00), 0),
      (.argb(0x80_00_00_FF), 1),
    ]
    XCTAssertEqual(stops.premultipliedSubdivided(), stops)
  }

  func test_EqualRGBDifferentAlpha_NoSubdivision() {
    let stops: [Gradient.Point] = [
      (.argb(0x40_FF_00_00), 0),
      (.argb(0xFF_FF_00_00), 1),
    ]
    XCTAssertEqual(stops.premultipliedSubdivided(), stops)
  }

  func test_PartialAlphaDifferentRGB_Subdivides() {
    // lhs = argb(0x40FF0000) → r=1, g=0, b=0, a=64/255.
    // rhs = argb(0xFF0000FF) → r=0, g=0, b=1, a=1.
    let stops: [Gradient.Point] = [
      (.argb(0x40_FF_00_00), 0),
      (.argb(0xFF_00_00_FF), 1),
    ]
    let expected: [Gradient.Point] = [
      (.argb(0x40_FF_00_00), 0),
      (.argb(0x4B_CC_00_33), 1.0 / 17.0),
      (.argb(0x56_A7_00_58), 2.0 / 17.0),
      (.argb(0x62_8A_00_75), 3.0 / 17.0),
      (.argb(0x6D_73_00_8C), 4.0 / 17.0),
      (.argb(0x78_60_00_9F), 5.0 / 17.0),
      (.argb(0x83_50_00_AF), 6.0 / 17.0),
      (.argb(0x8F_43_00_BC), 7.0 / 17.0),
      (.argb(0x9A_38_00_C7), 8.0 / 17.0),
      (.argb(0xA5_2F_00_D0), 9.0 / 17.0),
      (.argb(0xB0_26_00_D9), 10.0 / 17.0),
      (.argb(0xBC_1F_00_E0), 11.0 / 17.0),
      (.argb(0xC7_18_00_E7), 12.0 / 17.0),
      (.argb(0xD2_12_00_ED), 13.0 / 17.0),
      (.argb(0xDD_0D_00_F2), 14.0 / 17.0),
      (.argb(0xE9_08_00_F7), 15.0 / 17.0),
      (.argb(0xF4_04_00_FB), 16.0 / 17.0),
      (.argb(0xFF_00_00_FF), 1),
    ]
    XCTAssertEqual(stops.premultipliedSubdivided(), expected)
  }

  func test_SubdivisionMidpointMatchesPremultipliedMath() {
    // lhs = argb(0x80FF0000) → r=1, g=0, b=0, a=128/255.
    // rhs = argb(0xFF0000FF) → r=0, g=0, b=1, a=1.
    // For each sample s in 1...16 with p = s/17:
    //   pmR = a1 * (1 - p);  pmB = p;  a = a1 * (1 - p) + p
    //   color bytes = (round(255·pmR/a), 0, round(255·pmB/a), round(255·a))
    let stops: [Gradient.Point] = [
      (.argb(0x80_FF_00_00), 0),
      (.argb(0xFF_00_00_FF), 1),
    ]
    let expected: [Gradient.Point] = [
      (.argb(0x80_FF_00_00), 0),
      (.argb(0x87_E3_00_1C), 1.0 / 17.0),
      (.argb(0x8F_C9_00_36), 2.0 / 17.0),
      (.argb(0x96_B3_00_4C), 3.0 / 17.0),
      (.argb(0x9E_9E_00_61), 4.0 / 17.0),
      (.argb(0xA5_8B_00_74), 5.0 / 17.0),
      (.argb(0xAD_7A_00_85), 6.0 / 17.0),
      (.argb(0xB4_6A_00_95), 7.0 / 17.0),
      (.argb(0xBC_5C_00_A3), 8.0 / 17.0),
      (.argb(0xC3_4F_00_B0), 9.0 / 17.0),
      (.argb(0xCB_42_00_BD), 10.0 / 17.0),
      (.argb(0xD2_37_00_C8), 11.0 / 17.0),
      (.argb(0xDA_2C_00_D3), 12.0 / 17.0),
      (.argb(0xE1_22_00_DD), 13.0 / 17.0),
      (.argb(0xE9_19_00_E6), 14.0 / 17.0),
      (.argb(0xF0_10_00_EF), 15.0 / 17.0),
      (.argb(0xF8_08_00_F7), 16.0 / 17.0),
      (.argb(0xFF_00_00_FF), 1),
    ]
    XCTAssertEqual(stops.premultipliedSubdivided(), expected)
  }

  func test_ShimmerPattern_BothEndsTransparent_InheritFromMiddle() {
    let stops: [Gradient.Point] = [
      (.argb(0x00_00_00_00), 0),
      (.argb(0xCC_FF_FF_FF), 0.5),
      (.argb(0x00_00_00_00), 1),
    ]
    let expected: [Gradient.Point] = [
      (.argb(0x00_FF_FF_FF), 0),
      (.argb(0xCC_FF_FF_FF), 0.5),
      (.argb(0x00_FF_FF_FF), 1),
    ]
    XCTAssertEqual(stops.premultipliedSubdivided(), expected)
  }

  func test_DuplicateLocations_NoSubdivision() {
    let stops: [Gradient.Point] = [
      (.argb(0x80_FF_00_00), 0.5),
      (.argb(0xFF_00_00_FF), 0.5),
    ]
    XCTAssertEqual(stops.premultipliedSubdivided(), stops)
  }

  func test_AllTransparent_LeftUntouched() {
    let stops: [Gradient.Point] = [
      (.argb(0x00_FF_00_00), 0),
      (.argb(0x00_00_FF_00), 1),
    ]
    XCTAssertEqual(stops.premultipliedSubdivided(), stops)
  }

  func test_TransparentMiddleStop_InheritsFromNearestNonTransparentNeighbor() {
    // [opaque white, transparent black, opaque red]. The interior transparent
    // stop inherits white from the nearest-by-index non-transparent neighbor
    // (stop 0 — same distance both ways, scan order picks left first). After
    // normalization the left segment has equal RGB (skip) and the right
    // segment subdivides (transparent white → opaque red); premultiplication
    // zeroes the irrelevant white RGB, so samples are pure red fading in.
    let stops: [Gradient.Point] = [
      (.argb(0xFF_FF_FF_FF), 0),
      (.argb(0x00_00_00_00), 0.5),
      (.argb(0xFF_FF_00_00), 1),
    ]
    let expected: [Gradient.Point] = [
      (.argb(0xFF_FF_FF_FF), 0),
      (.argb(0x00_FF_FF_FF), 0.5),
      (.argb(0x0F_FF_00_00), 0.5 + 0.5 * (1.0 / 17.0)),
      (.argb(0x1E_FF_00_00), 0.5 + 0.5 * (2.0 / 17.0)),
      (.argb(0x2D_FF_00_00), 0.5 + 0.5 * (3.0 / 17.0)),
      (.argb(0x3C_FF_00_00), 0.5 + 0.5 * (4.0 / 17.0)),
      (.argb(0x4B_FF_00_00), 0.5 + 0.5 * (5.0 / 17.0)),
      (.argb(0x5A_FF_00_00), 0.5 + 0.5 * (6.0 / 17.0)),
      (.argb(0x69_FF_00_00), 0.5 + 0.5 * (7.0 / 17.0)),
      (.argb(0x78_FF_00_00), 0.5 + 0.5 * (8.0 / 17.0)),
      (.argb(0x87_FF_00_00), 0.5 + 0.5 * (9.0 / 17.0)),
      (.argb(0x96_FF_00_00), 0.5 + 0.5 * (10.0 / 17.0)),
      (.argb(0xA5_FF_00_00), 0.5 + 0.5 * (11.0 / 17.0)),
      (.argb(0xB4_FF_00_00), 0.5 + 0.5 * (12.0 / 17.0)),
      (.argb(0xC3_FF_00_00), 0.5 + 0.5 * (13.0 / 17.0)),
      (.argb(0xD2_FF_00_00), 0.5 + 0.5 * (14.0 / 17.0)),
      (.argb(0xE1_FF_00_00), 0.5 + 0.5 * (15.0 / 17.0)),
      (.argb(0xF0_FF_00_00), 0.5 + 0.5 * (16.0 / 17.0)),
      (.argb(0xFF_FF_00_00), 1),
    ]
    XCTAssertEqual(stops.premultipliedSubdivided(), expected)
  }
}

private func XCTAssertEqual(
  _ actual: [Gradient.Point],
  _ expected: [Gradient.Point],
  file: StaticString = #filePath,
  line: UInt = #line
) {
  guard actual == expected else {
    XCTFail("expected \(expected), got \(actual)", file: file, line: line)
    return
  }
}

extension [(color: Color, location: CGFloat)] {
  fileprivate static func ==(lhs: Self, rhs: Self) -> Bool {
    guard lhs.count == rhs.count else { return false }
    return zip(lhs, rhs).allSatisfy { $0.color == $1.color && $0.location == $1.location }
  }
}
