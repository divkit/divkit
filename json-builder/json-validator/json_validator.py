import argparse
import json
import re

used_to_build_card_template_field = "USED_TO_BUILD_CARD"


def print_warning(message, template_name=None, id=None, path=None):
    if template_name is not None:
        print(f"Warning: {message}. template='{template_name}'.")
    elif id is not None:
        print(f"Warning: {message}. id='{id}'.")
    elif path is not None:
        print(f"Warning: {message}. path='{path}'.")
    else:
        print(f"Warning: {message}.")


def print_error(message, template_name=None, id=None, path=None):
    if template_name is not None:
        print(f"ERROR: {message}. template='{template_name}'.")
    elif id is not None:
        print(f"ERROR: {message}. id='{id}'.")
    elif path is not None:
        print(f"ERROR: {message}. path='{path}'.")
    else:
        print(f"ERROR: {message}.")


def validate_no_unused_templates(templates_map):
    for key, value in templates_map.items():
        if used_to_build_card_template_field not in value:
            print_warning(f"template named '{key}' is never used")


def validate_string_field(s, template_name=None, id=None):
    evaluable_pattern = r'@{([^}]*)}'
    evaluable_match = re.search(evaluable_pattern, s)

    if evaluable_match:
        raw_value = evaluable_match.group(1)
        if (re.fullmatch(r'\d+', raw_value)
                or re.fullmatch(r'\'[^\']+\'', raw_value)
                or re.fullmatch(r'[+-]? *(?:\d+(?:\.\d*)?|\.\d+)(?:[eE][+-]?\d+)?', raw_value)):
            print_warning(
                f"evaluable expression {s} wrapping raw value. Use a raw value instead",
                template_name=template_name,
                id=id
            )
        quoted_values = re.findall(r'\'([^\']{200,})\'', raw_value)
        if quoted_values:
            print_warning(
                f"evaluable expression '{s[:50]}..' with a string longer than 200 "
                f"characters. It will be better for performance if you store long strings as "
                f"variables",
                template_name=template_name,
                id=id
            )
            for value in quoted_values:
                print(f"\tLong string: '{value[:50]}..'")
    elif len(s) >= 200:
        print_warning(
            f"string '{s[:50]}..' longer than 200 characters. It will be better for "
            f"performance if you store long strings as variables",
            template_name=template_name,
            id=id
        )


def get_size(item, name, default, id=None):
    if name in item:
        size_block = item[name]
        if 'type' in size_block:
            type = size_block['type']
            if type == 'match_parent':
                return 'match_parent'
            elif type == 'wrap_content':
                return 'wrap_content'
            elif type == 'fixed':
                return 'fixed'
            else:
                print_error(
                    f"{name} with 'type'={type}. Use 'wrap_content', 'match_parent' or 'fixed'", id
                )
    return default


def validate_size(item, parent, path):
    match_parent = "match_parent"
    wrap_content = "wrap_content"

    id = item.get('id', None)
    parent_id = parent.get('id', None)

    width = get_size(item, 'width', match_parent, id)
    height = get_size(item, 'height', wrap_content, id)
    parent_width = get_size(parent, 'width', match_parent, parent_id)
    parent_height = get_size(parent, 'height', wrap_content)

    orientation = 'horizontal'
    if orientation in parent:
        orientation = parent['orientation']

    if width == match_parent and parent_width == wrap_content:
        print_warning(
            f"{orientation} container with 'wrap_content' width contains item with 'match_parent' "
            f"width. child_id='{item.get('id', 'NO_ID')}'",
            id=parent_id,
            path=path
        )

    if height == match_parent and parent_height == wrap_content:
        print_warning(
            f"{orientation} container with 'wrap_content' height contains item with "
            f"'match_parent' width. child_id='{item.get('id', 'NO_ID')}'",
            id=parent_id,
            path=path
        )


def validate_item_raw(div):
    for key, value in div.items():
        if isinstance(value, str):
            if 'id' in div:
                validate_string_field(value, id=div['id'])
            else:
                validate_string_field(value)


def validate_raw_template(template, name):
    # checking fields before joining all the templates to avoid warning duplicates.
    for key, value in template.items():
        if isinstance(value, str):
            validate_string_field(value, name)

    if 'type' not in template:
        print_error("'type' is not specified", template_name=name)

    if 'items' in template:
        # checking items inside the container
        for item in template['items']:
            validate_raw_template(item, name)


def build_template(template, checked_types, templates_map):
    type = template.get("type")

    if type in checked_types:
        # some of the templates can inherit others
        # in this case templates would be resolved recursively
        return
    checked_types.add(type)

    if type in templates_map:
        original_template = templates_map[type]

        build_template(original_template, checked_types, templates_map)

        for key, value in original_template.items():
            if key not in template and key != used_to_build_card_template_field:
                template[key] = value
            if key == 'type':
                template[key] = value

        original_template[used_to_build_card_template_field] = 1


def process_templates(templates, templates_map):
    for template_id, template in templates.items():
        templates_map[template_id] = template
        validate_raw_template(template, template_id)

    checked_types = set()
    for key in templates_map:
        build_template(templates_map[key], checked_types, templates_map)


def substitute_fields(template_name, template, div, to_modify_div, path):
    if to_modify_div is None:
        return

    substitutions = []

    for key, value in template.items():
        if isinstance(value, dict):
            substitute_fields(template_name, value, div, to_modify_div.get(key, None), path)
        elif isinstance(value, list):
            for index, item in enumerate(value):
                if isinstance(item, dict):
                    to_modify_item = to_modify_div.get(key, [{}] * len(value))[index]
                    substitute_fields(template_name, item, div, to_modify_item, f"{path}[{index}]")
        elif key.startswith("$"):
            real_field = value
            if real_field in div:
                substitutions.append((key[1:], div.pop(real_field)))
            elif key[1:] in to_modify_div:
                print_warning(
                    f"Unnecessary substitution field '{key}': '{real_field}' in "
                    f"template={template_name}. Field '{key[1:]}' already exists",
                    id=div.get('id', None), path=path
                )

    for new_key, new_value in substitutions:
        to_modify_div[new_key] = new_value


def process_item(div, templates_map, path):
    if 'type' not in div and 'div' not in div:
        print_error("'type' is not specified", id=div.get('id', None), path=path)
        return
    if 'div' in div:
        process_item(div['div'], templates_map, path)
        return

    type = div['type']

    # validate fields before merge with template data
    validate_item_raw(div)

    if type in templates_map:
        template = templates_map[type]
        template[used_to_build_card_template_field] = 1

        for key, value in template.items():
            if key not in div and key != used_to_build_card_template_field:
                div[key] = value
            if key == 'type':
                div[key] = value
        substitute_fields(type, template, div, div, path)

    if type == 'state':
        process_state(type, templates_map, path)
    elif 'items' in div:
        process_container(div, templates_map, path)


def process_container(container, templates_map, path):
    items = container['items']

    if len(items) == 0:
        print_error("container without items", id=container.get("id", None), path=path)
        return

    if len(items) == 1:
        print_warning(
            "container with only one element. Please avoid unnecessary nesting",
            id=container.get("id", None), path=path
        )

    for div in items:
        if 'id' in div:
            item_path = f"{path}.id={div['id']}"
        else:
            item_path = f"{path}.item#{items.index(div)}"

        process_item(div, templates_map, item_path)
        if container['type'] == 'container':
            validate_size(div, container, item_path)


def process_state(states, templates_map, path):
    for state in states:
        state_id = state.get("state_id", None)
        if state_id is None:
            print_error("state_id not specified", path=path)
            return

        path = f"{path}.state_id={state_id}"
        process_item(state['div'], templates_map, path)


def process_card(card, templates_map):
    states = card['states']
    process_state(states, templates_map, "")


def load_json(file_path):
    try:
        with open(file_path, 'r') as file:
            data = json.load(file)
        return data
    except Exception as e:
        print(f"Error loading JSON file: {e}")
        return None


def main(json_file_path):
    data = load_json(json_file_path)
    if data is None:
        return
    templates_map = {}
    if 'card' not in data:
        process_card(data, templates_map)
    else:
        cards = data['card']
        if 'templates' in data:
            process_templates(data['templates'], templates_map)
        process_card(cards, templates_map)

    validate_no_unused_templates(templates_map)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('file_name', type=str, help='Path to the JSON file to check')
    args = parser.parse_args()

    main(args.file_name)
