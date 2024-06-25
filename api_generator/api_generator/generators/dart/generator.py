from typing import cast, List
import os.path

from ...config import GeneratedLanguage
from .dart_entities import (
    DartEntity,
    DartPropertyType,
    update_base, DartProperty,
)

from ..base import Generator
from ... import utils
from .utils import allowed_name, get_full_name
from ...schema.modeling.entities import EntityEnumeration, StringEnumeration, Declarable
from ...schema.modeling.text import Text, EMPTY


class DartGenerator(Generator):
    def __init__(self, config):
        super(DartGenerator, self).__init__(config)

    def filename(self, name: str) -> str:
        return f'{utils.snake_case(name)}.dart'

    def generate(self, objects: List[Declarable]):
        objects_dart: List[Declarable] = list(map(lambda obj: update_base(obj), objects))
        super(DartGenerator, self).generate(objects_dart)
        self.generate_export(objects)

    def generate_export(self, objects: List[Declarable]):
        file_content = Text()
        for obj in objects:
            file_content += f'export \'./{self.filename(obj.name)}\';'
        file_content += '\n'
        filename = os.path.join(self._output_path, 'generated_sources.dart')
        with open(filename, 'w') as file:
            file.write(file_content.__str__())
            file.close()

    def _entity_declaration(self, entity: DartEntity) -> Text:
        result = Text()

        # Equatable util import
        if entity.is_main_declaration and not entity.generate_as_protocol:
            result += "import 'package:equatable/equatable.dart';\n"

        # Protocol import
        protocols = list(filter(None, [get_full_name(entity.implemented_protocol)]))
        protocols_import = []
        if not entity.generate_as_protocol:
            protocols_import = [f"import '{utils.snake_case(protocol)}.dart';" for protocol in protocols]
        conformance = '' if not protocols else f'implements {", ".join(protocols)} '

        # Default imports
        defaults_import = [f"import '{utils.snake_case(e)}.dart';" for e in
                           filter(None, [d.get_default_import for d in entity.instance_properties])]

        # Parsing utils import
        parsing_ext_import = entity.import_parsing_utils

        # Imports section declaration
        if entity.is_main_declaration:
            imports = sorted(entity.imports + protocols_import + defaults_import + parsing_ext_import)
            if len(imports) != 0:
                for i in imports:
                    result += i
                result += EMPTY

        # Props in impl protocol, needs to add @override
        needs_override_names = []
        if entity.has_super:
            needs_override_names = list(
                map(lambda e: cast(DartProperty, e).name, entity.super_entity.instance_properties))

        # Generate protocol if needed
        if entity.generate_as_protocol:
            result += self.__declaration_as_interface(entity)
            return result

        # Class header declaration
        full_name = get_full_name(entity)
        result += f'class {full_name} with EquatableMixin {conformance}' + '{'

        # Constructor declaration
        props_decl = Text()
        for prop in entity.instance_properties:
            option = 'required ' if not prop.optional and not prop.has_default else ''
            props_decl += f"{prop.add_default_value_to(f'    {option}this.{utils.lower_camel_case(prop.name)}')},"

        constructor_props = f'  const {full_name}' + '({\n' + props_decl.__str__() + '\n  });\n' if len(
            props_decl.__str__()) != 0 else f'  const {full_name}();\n'
        result += constructor_props

        # Static props declaration
        for static_prop in entity.static_properties:
            result += f'  static const {utils.lower_camel_case(static_prop.name)} = "{entity.static_type}";'

        # Instance props declaration
        for instance_prop in entity.instance_properties:
            override = '  @override\n' if instance_prop.name in needs_override_names else ''
            property_type = cast(DartPropertyType, instance_prop.property_type)
            prop_type = property_type.declaration()
            type_decl = f'Expression<{prop_type}>' if instance_prop.supports_expressions else prop_type
            option = '?' if instance_prop.optional and not instance_prop.has_default else ''
            comment = f'{" " + instance_prop.comment}' + '\n'
            result += f'{comment}{override}  final {type_decl}{option} {utils.lower_camel_case(instance_prop.name)};'

        # Equatable declaration
        if len(entity.instance_properties) != 0:
            result += EMPTY
            result += '  @override'
            result += '  List<Object?> get props => ['
            for prop in entity.instance_properties:
                result += f'        {utils.lower_camel_case(prop.name)},'
            result += '      ];'
        else:
            result += EMPTY
            result += '  @override'
            result += '  List<Object?> get props => [];'

        # Serializable declaration
        if len(entity.instance_properties) != 0:
            result += EMPTY
            result += f'  static {full_name}? fromJson(Map<String, dynamic>? json)' + ' {'
            result += '    if (json == null) {'
            result += '      return null;'
            result += '    }'
            result += f'    return {full_name}('
            for prop in entity.instance_properties:
                decode_strategy = prop.get_parse_strategy()
                result += f"      {utils.lower_camel_case(prop.name)}: {decode_strategy},"
            result += '    );'
            result += '  }'
        else:
            result += EMPTY
            result += f'  static {full_name}? fromJson(Map<String, dynamic>? json)' + ' {'
            result += '    if (json == null) {'
            result += '      return null;'
            result += '    }'
            result += f'    return const {full_name}();'
            result += '  }'

        result += '}'

        # Recursive declaration of internal classes (since dart does not support them)
        for inner_type in entity.inner_types:
            result += EMPTY + self._main_declaration(inner_type)

        return result

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        result = Text()

        # Equatable util import
        if entity_enumeration.parent is None:
            result += "import 'package:equatable/equatable.dart';\n"

        full_name = get_full_name(entity_enumeration)
        common_interface = entity_enumeration.common_interface(GeneratedLanguage.DART)
        has_interface = common_interface is not None

        interface_name = common_interface.__str__() if has_interface else 'Object'
        interface_import = []
        if has_interface:
            interface_import.append(interface_name)

        for n in sorted(entity_enumeration.entity_names + interface_import):
            result += f'import \'{utils.snake_case(n)}.dart\';'
        result += EMPTY

        result += f'class {full_name} with EquatableMixin' + ' {'

        result += f'  final {interface_name} value;'
        result += '  final int _index;'
        result += EMPTY

        # Equatable declaration
        if len(entity_enumeration.entities) != 0:
            result += '  @override'
            result += '  List<Object?> get props => [value];'
        else:
            result += '  @override'
            result += '  List<Object?> get props => [];'
        result += EMPTY

        result += '  T map<T>(' + '{'
        for n in sorted(entity_enumeration.entity_names):
            result += f'    required T Function({utils.capitalize_camel_case(n)}) {utils.lower_camel_case(n)},'
        result += '  }) {'
        result += '    switch(_index!) {'
        for (i, n) in enumerate(sorted(entity_enumeration.entity_names)):
            result += f'      case {i}: return {utils.lower_camel_case(n)}(value as {utils.capitalize_camel_case(n)},);'
        result += '    }'
        result += '    throw Exception("Type ${value.runtimeType.toString()}' + f' is not generalized in {full_name}");'
        result += '  }'
        result += EMPTY

        result += '  T maybeMap<T>({'
        for n in sorted(entity_enumeration.entity_names):
            result += f'    T Function({utils.capitalize_camel_case(n)})? {utils.lower_camel_case(n)},'
        result += '    required T Function() orElse,'
        result += '  }) {'
        result += '    switch(_index!) {'
        for (i, n) in enumerate(sorted(entity_enumeration.entity_names)):
            result += f'    case {i}:'
            result += f'      if ({utils.lower_camel_case(n)} != null)' + ' {'
            result += f'        return {utils.lower_camel_case(n)}(value as {utils.capitalize_camel_case(n)},);'
            result += '      }'
            result += '      break;'
        result += '    }'
        result += '    return orElse();'
        result += '  }'
        result += EMPTY

        for (i, n) in enumerate(sorted(entity_enumeration.entity_names)):
            result += f'  const {full_name}.{utils.lower_camel_case(n)}('
            result += f'    {utils.capitalize_camel_case(n)} obj,'
            result += '  ) : value = obj,'
            result += f'      _index = {i};'
            result += EMPTY

        # Serializable declaration
        result += EMPTY
        result += f'  static {full_name}? fromJson(Map<String, dynamic>? json)' + ' {'
        result += '    if (json == null) {'
        result += '      return null;'
        result += '    }'
        result += '    switch (json[\'type\']) {'
        for (i, n) in enumerate(sorted(entity_enumeration.entity_names)):
            result += f'      case {utils.capitalize_camel_case(n)}.type :\n' \
                      f'        return {full_name}.{utils.lower_camel_case(n)}({utils.capitalize_camel_case(n)}.fromJson(json)!);'
        result += '    }'
        result += '    return null;'
        result += '  }'

        result += '}'

        return result

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        result = Text()

        full_name = get_full_name(string_enumeration)

        result += f'enum {full_name}' + ' {'
        cases_len = len(string_enumeration.cases)
        for i in range(cases_len):
            result += f'  {allowed_name(utils.lower_camel_case(string_enumeration.cases[i][0]))}' \
                      f'(\'{string_enumeration.cases[i][0]}\')' + (
                          ',' if i != cases_len - 1 else ';')
        result += EMPTY
        result += '  final String value;'
        result += EMPTY
        result += f'  const {full_name}(this.value);'

        result += EMPTY
        result += '  T map<T>({'
        for i in range(cases_len):
            result += f'    required T Function() {allowed_name(utils.lower_camel_case(string_enumeration.cases[i][0]))},'
        result += '  }) {'
        result += '    switch (this) {'
        for i in range(cases_len):
            result += f'      case {full_name}.{allowed_name(utils.lower_camel_case(string_enumeration.cases[i][0]))}:'
            result += f'        return {allowed_name(utils.lower_camel_case(string_enumeration.cases[i][0]))}();'
        result += '     }'
        result += '  }'
        result += EMPTY

        result += '  T maybeMap<T>({'
        for i in range(cases_len):
            result += f'    T Function()? {allowed_name(utils.lower_camel_case(string_enumeration.cases[i][0]))},'
        result += '    required T Function() orElse,'
        result += '  }) {'
        result += '    switch (this) {'
        for i in range(cases_len):
            result += f'      case {full_name}.{allowed_name(utils.lower_camel_case(string_enumeration.cases[i][0]))}:'
            result += f'        return {allowed_name(utils.lower_camel_case(string_enumeration.cases[i][0]))}?.call() ?? orElse();'
        result += '     }'
        result += '  }'
        result += EMPTY

        # Serializable declaration
        result += EMPTY
        result += f'  static {full_name}? fromJson(String? json)' + ' {'
        result += '    if (json == null) {'
        result += '      return null;'
        result += '    }'
        result += '    switch (json) {'
        for i in range(cases_len):
            result += f'      case \'{string_enumeration.cases[i][0]}\':\n        return ' \
                      f'{full_name}.{allowed_name(utils.lower_camel_case(string_enumeration.cases[i][0]))};'
        result += '    }'
        result += '    return null;'
        result += '  }'

        result += '}'

        return result

    @staticmethod
    def __declaration_as_interface(entity: DartEntity) -> Text:
        result = Text(f'abstract class {get_full_name(entity)} {{')
        props = entity.instance_properties
        for i, prop in enumerate(props):
            name = prop.declaration_name
            type_decl = prop.type_declaration
            comment = f'{" " + prop.comment}' + '\n'
            result += f'{comment}  {type_decl} get {name};'
            if i != len(props) - 1:
                result += '\n'
        result += '}'
        return result
