import unittest
import io
import json
import tempfile
from contextlib import redirect_stdout

from json_validator import main as validate


class TestTemplateValidator(unittest.TestCase):
    def setUp(self):
        self.held_output = io.StringIO()

    def run_main_with_json(self, data):
        with tempfile.NamedTemporaryFile('w', delete=False) as temp_json_file:
            json.dump(data, temp_json_file)
            temp_json_file_path = temp_json_file.name

        with redirect_stdout(self.held_output):
            validate(temp_json_file_path)

    def test_missing_type_in_template(self):
        data = {
            "card": {
                "states": [
                    {
                        "state_id": 1,
                        "div": {
                            "type": "element"
                        }
                    }
                ]
            },
            "templates": {
                "element": {
                    # No type
                    "text": "123456."
                }
            }
        }
        self.run_main_with_json(data)
        output = self.held_output.getvalue()
        print(f"test_missing_type_in_template output:\n{output}")
        self.assertIn("ERROR: 'type' is not specified. template='element'.", output)

    def test_container_with_only_one_element(self):
        data = {
            "card": {
                "states": [
                    {
                        "state_id": "state_1",
                        "div": {
                            "type": "container",
                            "items": [
                                # Only one child
                                {
                                    "type": "text"
                                }
                            ]
                        }
                    }
                ]
            }
        }
        self.run_main_with_json(data)
        output = self.held_output.getvalue()
        print(f"test_state_id_not_specified output:\n{output}")
        self.assertIn("Warning: container with only one element. Please avoid unnecessary nesting",
                      output)

    def test_state_id_not_specified(self):
        data = {
            "card": {
                "states": [
                    {
                        # No state_id.
                        "div": {
                            "type": "element"
                        }
                    }
                ]
            }
        }
        self.run_main_with_json(data)
        output = self.held_output.getvalue()
        print(f"test_state_id_not_specified output:\n{output}")
        self.assertIn("ERROR: state_id not specified.", output)

    def test_string_field_too_long(self):
        data = {
            "card": {
                "states": [
                    {
                        "state_id": "state_1",
                        "div": {
                            "type": "element",
                            "id": "test_card",
                            # String with length more than 200 symbols
                            "text": "a" * 201
                        }
                    }
                ]
            }
        }
        self.run_main_with_json(data)
        output = self.held_output.getvalue()
        print(f"test_string_field_too_long output:\n{output}")
        self.assertIn("Warning: string 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa..' "
                      "longer than 200 characters. It will be better for performance if you store "
                      "long strings as variables. id='test_card'", output)

    def tes_evaluable_expression_warning(self):
        data = {
            "card": {
                "states": [
                    {
                        "state_id": "state_1",
                        "div": {
                            "type": "container",
                            "items": [
                                {
                                    "type": "text",
                                    # Raw value
                                    "id": "test_card1",
                                    "text": "@{'12345'}"
                                },
                                {
                                    "type": "text",
                                    # Raw value
                                    "id": "test_card2",
                                    "text": "@{'4.5'}"
                                }
                            ]
                        }
                    }
                ]
            }
        }
        self.run_main_with_json(data)
        output = self.held_output.getvalue()
        print(f"tes_evaluable_expression_warning output:\n{output}")
        self.assertIn("Warning: evaluable expression @{'12345'} wrapping raw value. Use a raw "
                      "value instead. id='test_card1'", output)
        self.assertIn("Warning: evaluable expression @{'4.5'} wrapping raw value. Use a raw "
                      "value instead. id='test_card2'", output)

    def test_useless_template(self):
        data = {
            "card": {
                "states": [
                    {
                        "state_id": 1,
                        "div": {
                            "type": "text",
                            "text": "12345"
                        }
                    }
                ]
            },
            "templates": {
                # Useless template
                "element": {
                    "type": "ordinary_text",
                    "text": "12345678"
                },
                "ordinary_text": {
                    "type": "text",
                    "text": "12345678"
                }
            }
        }
        self.run_main_with_json(data)
        output = self.held_output.getvalue()
        print(f"test_useless_template output:\n{output}")
        self.assertIn("Warning: template named 'element' is never used.", output)
        self.assertNotIn("Warning: template named 'ordinary_text' is never used.", output)

    def test_useless_substitution(self):
        data = {
            "card": {
                "states": [
                    {
                        "state_id": 1,
                        "div": {
                            "id": "test_card",
                            "type": "element",
                            # `text` specified
                            "text": "12345"
                        }
                    }
                ]
            },
            "templates": {
                "element": {
                    "type": "text",
                    # Useless substitution
                    "$text": "header"
                }
            }
        }
        self.run_main_with_json(data)
        output = self.held_output.getvalue()
        print(f"test_useless_substitution output:\n{output}")
        self.assertIn("Warning: Unnecessary substitution field '$text': 'header' in "
                      "template=element", output)

    def test_wrap_content_child_in_match_parent_container(self):
        data = {
            "card": {
                "states": [
                    {
                        "state_id": 1,
                        "div": {
                            "id": "test_container",
                            "type": "container",
                            "orientation": "vertical",
                            # height is not specified, will be "wrap_content"
                            "items": [
                                {
                                    "id": "template_test_card",
                                    # height is specified in template and equals "match_parent"
                                    "type": "match_parent_height_text"
                                },
                                {
                                    "id": "correct_size",
                                    "type": "text",
                                    "text": "text"
                                },
                            ]
                        }
                    }
                ]
            },
            "templates": {
                "match_parent_height_text": {
                    "type": "text",
                    "text": "text",
                    "height": {
                        "type": "match_parent"
                    }
                }
            }
        }
        self.run_main_with_json(data)
        output = self.held_output.getvalue()
        print(f"test_wrap_content_child_in_match_parent_container output:\n{output}")
        self.assertIn("Warning: horizontal container with 'wrap_content' height contains item "
                      "with 'match_parent' width. child_id='template_test_card'. "
                      "id='test_container'.", output)
        self.assertNotIn("correct_size", output)

    def test_field_is_substituted(self):
        data = {
            "card": {
                "states": [
                    {
                        "state_id": 1,
                        "div": {
                            "id": "test_container",
                            "type": "container_with_elements",
                            # elements should become 'items'
                            "elements": [
                                {
                                    "type": "header"
                                }
                            ]
                        }
                    }
                ]
            },
            "templates": {
                "container_with_elements": {
                    "type": "container",
                    "orientation": "vertical",
                    "$items": "elements"
                },
                "header": {
                    "type": "text",
                    "text": "text"
                }
            }
        }
        self.run_main_with_json(data)
        output = self.held_output.getvalue()
        print(f"test_field_is_substituted output:\n{output}")
        self.assertIn("Warning: container with only one element. Please avoid unnecessary "
                      "nesting. id='test_container'.", output)

    def test_field_in_dict_substituted(self):
        data = {
            "card": {
                "states": [
                    {
                        "state_id": 1,
                        "div": {
                            "id": "test_container",
                            "type": "container",
                            "items": [
                                {
                                    "type": "header",
                                    "id": "test_card",
                                    # should replace "height": { "$type": "height_type" }
                                    "height_type": "match_parent"
                                },
                                {
                                    "type": "text",
                                    "text": "text"
                                }
                            ]
                        }
                    }
                ]
            },
            "templates": {
                "header": {
                    "type": "text",
                    "text": "text",
                    "height": {
                        "$type": "height_type"
                    }
                }
            }
        }
        self.run_main_with_json(data)
        output = self.held_output.getvalue()
        print(f"test_field_in_dict_substituted output:\n{output}")
        self.assertIn("Warning: horizontal container with 'wrap_content' height contains item "
                      "with 'match_parent' width. child_id='test_card'. id='test_container'.", output)
        self.assertNotIn("NO_ID", output)


if __name__ == '__main__':
    unittest.main()
