@testable import DivKit
import XCTest

final class TemplateToTypeTests: XCTestCase {
  private func performTestCase(
    json: String,
    templateToType: [TemplateName: String]
  ) throws {
    let dict = try JSONSerialization.jsonObject(
      with: json.data(using: .utf8)!
    ) as! [String: Any]
    XCTAssertEqual(calculateTemplateToType(in: dict), templateToType)
  }

  func test_IndependentTypes() throws {
    let json = """
    {
      "template_name1": {
        "type": "final_type1"
      },
      "template_name2": {
        "type": "final_type2"
      }
    }
    """
    let templateToType = [
      "template_name1": "final_type1",
      "template_name2": "final_type2",
    ]
    try performTestCase(json: json, templateToType: templateToType)
  }

  func test_DerivedTypes() throws {
    let json = """
    {
      "parent_template_name": {
        "type": "final_type1"
      },
      "derived_template_name1": {
        "type": "parent_template_name"
      },
      "derived_template_name2": {
        "type": "derived_template_name1"
      },
      "independed_template": {
        "type": "final_type2"
      }
    }
    """
    let templateToType = [
      "parent_template_name": "final_type1",
      "derived_template_name1": "final_type1",
      "derived_template_name2": "final_type1",
      "independed_template": "final_type2",
    ]
    try performTestCase(json: json, templateToType: templateToType)
  }

  func test_CircularReferences() throws {
    let json = """
    {
      "circular_template_name1": {
        "type": "circular_template_name2"
      },
      "circular_template_name2": {
        "type": "circular_template_name3"
      },
      "circular_template_name3": {
        "type": "circular_template_name1"
      },
      "circular_template_name4": {
        "type": "circular_template_name4"
      },
      "circular_template_name5": {
        "type": "circular_template_name6"
      },
      "circular_template_name6": {
        "type": "circular_template_name5"
      },
      "circular_template_name7": {
        "type": "circular_template_name1"
      },
      "circular_template_name8": {
        "type": "circular_template_name7"
      },
      "valid_template_name": {
        "type": "final_type1"
      },
      "derived_valid_template_name": {
        "type": "valid_template_name"
      },
      "independent_template": {
        "type": "final_type2"
      }
    }
    """
    let templateToType = [
      "valid_template_name": "final_type1",
      "derived_valid_template_name": "final_type1",
      "independent_template": "final_type2",
    ]
    try performTestCase(json: json, templateToType: templateToType)
  }
}
