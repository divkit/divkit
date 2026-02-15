from __future__ import annotations
from typing import List, cast, Optional

from .rust_entities import (
    update_base,
    rust_full_name,
    rust_field_name,
    as_rust_doc_comment,
    VARIANT_RENAMES,
    RustEntity,
    RustProperty,
)
from ..base import Generator
from ...schema.modeling.text import Text, EMPTY
from ...schema.modeling.entities import (
    StringEnumeration,
    EntityEnumeration,
    Entity,
    Declarable,
    StaticString,
)
from ... import utils


class RustGenerator(Generator):
    def filename(self, name: str) -> str:
        return f'{utils.snake_case(name)}.rs'

    def generate(self, objects: List[Declarable]):
        objects_rust: List[Declarable] = list(map(lambda obj: update_base(obj), objects))
        super(RustGenerator, self).generate(objects_rust)
        self.__generate_mod_file(objects_rust)
        self.__generate_python_registration(objects_rust)
        self.__generate_schema_registry(objects_rust)

    # ----------------------------------------------------------------
    # File name → mod.rs generation
    # ----------------------------------------------------------------

    # Modules to exclude from mod.rs (e.g., types that have custom
    # implementations elsewhere in the crate).
    EXCLUDED_MODULES = frozenset({'div_data'})

    def __generate_mod_file(self, objects: List[Declarable]):
        """Generate mod.rs that re-exports all generated modules."""
        result = Text('// Generated code. Do not modify.')
        result += '#![allow(unused_imports)]'
        result += EMPTY

        module_names = set()
        for obj in sorted(objects, key=lambda d: d.name):
            mod_name = utils.snake_case(obj.name)
            if mod_name not in self.EXCLUDED_MODULES:
                module_names.add(mod_name)

        for mod_name in sorted(module_names):
            result += f'mod {mod_name};'

        # Python registration module
        result += 'pub mod python_generated;'
        # Schema registry module
        result += 'pub mod schema_registry;'

        result += EMPTY

        # Re-export all public items
        for mod_name in sorted(module_names):
            result += f'pub use {mod_name}::*;'

        with open(f'{self._config.output_path}/mod.rs', 'w') as f:
            f.write(str(result) + '\n')

    # ----------------------------------------------------------------
    # Python registration code generation
    # ----------------------------------------------------------------

    def __generate_python_registration(self, objects: List[Declarable]):
        """Generate python_generated.rs with register_all_entities/enums/aliases functions."""
        result = Text('// Generated code. Do not modify.')
        result += EMPTY
        result += 'use pyo3::prelude::*;'
        result += EMPTY
        result += 'use crate::py_entity::register_entity_class;'
        result += EMPTY

        # --- register_all_entities ---
        result += '/// Register all entity types on the module.'
        result += 'pub fn register_all_entities(py: Python<\'_>, m: &Bound<\'_, PyModule>) -> PyResult<()> {'

        entities = sorted(
            [obj for obj in objects if isinstance(obj, RustEntity)],
            key=lambda e: e.name,
        )
        for entity in entities:
            ent = cast(RustEntity, entity)
            class_name = ent.rust_name
            type_name = ent.static_type_value
            all_fields = ent.all_field_names
            required_fields = ent.required_field_names

            type_name_str = f'Some("{type_name}")' if type_name else 'None'
            fields_str = ', '.join(f'"{f}"' for f in all_fields)
            required_str = ', '.join(f'"{f}"' for f in required_fields)

            result += Text(
                f'register_entity_class(py, m, "{class_name}", {type_name_str},\n'
                f'    &[{fields_str}], &[{required_str}])?;'
            ).indented(indent_width=4)

            # Also register inner entities
            for inner in self.__collect_inner_entities(ent):
                inner_ent = cast(RustEntity, inner)
                i_class_name = rust_full_name(inner_ent)
                i_type_name = inner_ent.static_type_value
                i_all_fields = inner_ent.all_field_names
                i_required_fields = inner_ent.required_field_names

                i_type_name_str = f'Some("{i_type_name}")' if i_type_name else 'None'
                i_fields_str = ', '.join(f'"{f}"' for f in i_all_fields)
                i_required_str = ', '.join(f'"{f}"' for f in i_required_fields)

                result += Text(
                    f'register_entity_class(py, m, "{i_class_name}", {i_type_name_str},\n'
                    f'    &[{i_fields_str}], &[{i_required_str}])?;'
                ).indented(indent_width=4)

        result += Text('Ok(())').indented(indent_width=4)
        result += '}'
        result += EMPTY

        # --- register_enums ---
        result += '/// Register enum types as real `str, enum.Enum` subclasses.'
        result += '///'
        result += '/// This makes enums:'
        result += '/// - Callable as constructors: `DivFontWeight("bold")` works'
        result += '/// - Support `isinstance()`: `isinstance(DivFontWeight.BOLD, DivFontWeight)` is True'
        result += '/// - Usable as Pydantic type annotations'
        result += '/// - Compatible with pattern matching'
        result += '///'
        result += '/// Backward compatibility: `DivFontWeight.BOLD == "bold"` still holds because'
        result += '/// these are `str` subclasses.'
        result += 'pub fn register_enums(py: Python<\'_>, m: &Bound<\'_, PyModule>) -> PyResult<()> {'
        result += Text('let enums: &[(&str, &[(&str, &str)])] = &[').indented(indent_width=4)

        string_enums = sorted(
            [obj for obj in objects if isinstance(obj, StringEnumeration)],
            key=lambda e: e.name,
        )
        for senum in string_enums:
            se = cast(StringEnumeration, senum)
            enum_name = rust_full_name(se)
            variants_str = ', '.join(
                f'("{utils.upper_snake_case(value)}", "{value}")'
                for _, value in se.cases
            )
            result += Text(f'("{enum_name}", &[{variants_str}]),').indented(indent_width=8)

        # Also collect inner string enums from entities
        for entity in entities:
            ent = cast(RustEntity, entity)
            for inner_enum in self.__collect_inner_string_enums(ent):
                ie = cast(StringEnumeration, inner_enum)
                enum_name = rust_full_name(ie)
                variants_str = ', '.join(
                    f'("{utils.upper_snake_case(value)}", "{value}")'
                    for _, value in ie.cases
                )
                result += Text(f'("{enum_name}", &[{variants_str}]),').indented(indent_width=8)

        result += Text('];').indented(indent_width=4)
        result += EMPTY
        result += Text(
            '// Create shared namespace with base enum class.\n'
            '// _DivEnum is a str+Enum base that preserves backward-compatible __str__ behavior:\n'
            '//   str(DivFontWeight.BOLD) == "bold"  (not "DivFontWeight.BOLD")\n'
            'let ns = pyo3::types::PyDict::new(py);\n'
            'ns.set_item("_Enum", py.import("enum")?.getattr("Enum")?)?;\n'
            '\n'
            'let base_code = std::ffi::CString::new(concat!(\n'
            '    "class _DivEnum(str, _Enum):\\n",\n'
            '    "    def __str__(self):\\n",\n'
            '    "        return self.value\\n",\n'
            '    "    def __format__(self, format_spec):\\n",\n'
            '    "        return str.__format__(self.value, format_spec)\\n",\n'
            '))\n'
            '.unwrap();\n'
            'py.run(&base_code, Some(&ns), Some(&ns))?;\n'
            '\n'
            'for (enum_name, variants) in enums {\n'
            '    let variants_code = variants\n'
            '        .iter()\n'
            '        .map(|(name, val)| format!("    {} = \'{}\'", name, val))\n'
            '        .collect::<Vec<_>>()\n'
            '        .join("\\n");\n'
            '\n'
            '    let code = format!(\n'
            '        "class {}(_DivEnum):\\n    __module__ = \'divkit_rs._native\'\\n{}\\n",\n'
            '        enum_name, variants_code\n'
            '    );\n'
            '    let code_cstr = std::ffi::CString::new(code).unwrap();\n'
            '    py.run(&code_cstr, Some(&ns), Some(&ns))?;\n'
            '\n'
            '    let cls = ns.get_item(*enum_name)?.ok_or_else(|| {\n'
            '        pyo3::exceptions::PyRuntimeError::new_err(format!(\n'
            '            "Failed to create enum {}",\n'
            '            enum_name\n'
            '        ))\n'
            '    })?;\n'
            '    m.setattr(*enum_name, cls)?;\n'
            '}'
        ).indented(indent_width=4)
        result += EMPTY
        result += Text('Ok(())').indented(indent_width=4)
        result += '}'
        result += EMPTY

        # --- register_type_aliases ---
        result += '/// Register type aliases (Union types) on the module.'
        result += 'pub fn register_type_aliases(_py: Python<\'_>, m: &Bound<\'_, PyModule>) -> PyResult<()> {'
        result += Text('let entity_cls = m.getattr("PyDivEntity")?;').indented(indent_width=4)
        result += EMPTY

        entity_enums = sorted(
            [obj for obj in objects if isinstance(obj, EntityEnumeration)],
            key=lambda e: e.name,
        )
        for ee in entity_enums:
            enum = cast(EntityEnumeration, ee)
            alias_name = rust_full_name(enum)
            result += Text(f'm.setattr("{alias_name}", &entity_cls)?;').indented(indent_width=4)

        result += EMPTY
        result += Text('Ok(())').indented(indent_width=4)
        result += '}'

        with open(f'{self._config.output_path}/python_generated.rs', 'w') as f:
            f.write(str(result) + '\n')

    # ----------------------------------------------------------------
    # Schema registry generation
    # ----------------------------------------------------------------

    # Entity names to exclude from the schema registry
    # (they have custom implementations and are not generated as entities)
    EXCLUDED_REGISTRY_ENTITIES = frozenset({'DivData', 'DivDataState'})

    def __generate_schema_registry(self, objects: List[Declarable]):
        """Generate schema_registry.rs with entity name → field_descriptors lookup."""
        result = Text('// Generated code. Do not modify.')
        result += EMPTY
        result += 'use crate::entity::Entity;'
        result += 'use crate::field::FieldDescriptor;'
        result += 'use super::*;'
        result += EMPTY
        result += '/// Look up an entity\'s field descriptors by name.'
        result += '/// Returns `None` if the name is unknown.'
        result += 'pub fn entity_field_descriptors(name: &str) -> Option<Vec<FieldDescriptor>> {'
        result += Text('match name {').indented(indent_width=4)

        # Collect all entities (including inner entities)
        entities = sorted(
            [obj for obj in objects if isinstance(obj, RustEntity)],
            key=lambda e: e.name,
        )
        all_entries = []
        for entity in entities:
            ent = cast(RustEntity, entity)
            name = ent.rust_name
            if name not in self.EXCLUDED_REGISTRY_ENTITIES:
                all_entries.append(name)
            for inner in self.__collect_inner_entities(ent):
                inner_ent = cast(RustEntity, inner)
                inner_name = rust_full_name(inner_ent)
                if inner_name not in self.EXCLUDED_REGISTRY_ENTITIES:
                    all_entries.append(inner_name)

        for name in sorted(set(all_entries)):
            result += Text(f'"{name}" => Some({name}::new().field_descriptors()),').indented(indent_width=8)

        result += Text('_ => None,').indented(indent_width=8)
        result += Text('}').indented(indent_width=4)
        result += '}'

        with open(f'{self._config.output_path}/schema_registry.rs', 'w') as f:
            f.write(str(result) + '\n')

    # ----------------------------------------------------------------
    # Helpers for collecting inner types
    # ----------------------------------------------------------------

    def __collect_inner_entities(self, entity: RustEntity) -> List[RustEntity]:
        result = []
        for inner in entity.inner_types:
            if isinstance(inner, Entity):
                update_base(inner)
                result.append(cast(RustEntity, inner))
                result.extend(self.__collect_inner_entities(cast(RustEntity, inner)))
        return result

    def __collect_inner_string_enums(self, entity: RustEntity) -> List[StringEnumeration]:
        result = []
        for inner in entity.inner_types:
            if isinstance(inner, StringEnumeration):
                result.append(inner)
            elif isinstance(inner, Entity):
                update_base(inner)
                result.extend(self.__collect_inner_string_enums(cast(RustEntity, inner)))
        return result

    # ----------------------------------------------------------------
    # Entity declaration (one file per entity)
    # ----------------------------------------------------------------

    def _entity_declaration(self, entity: RustEntity) -> Text:
        result = Text()
        result += self.__entity_declaration_body(entity)

        # Inner types
        for inner in self.__inner_types_declarations(entity):
            result += EMPTY
            result += inner

        return result

    def __entity_declaration_body(self, entity: RustEntity) -> Text:
        result = Text()

        # Doc comment
        description = entity.description_doc()
        doc = None
        if description and description.strip():
            doc = as_rust_doc_comment(description)

        rust_name = entity.rust_name
        type_value = entity.static_type_value

        if type_value is not None:
            # Entity with a type field
            result += 'div_entity! {'
            if doc is not None:
                result += doc.indented(indent_width=4)
            result += Text(f'pub struct {rust_name} {{').indented(indent_width=4)
            result += Text(f'type_name: "{type_value}",').indented(indent_width=8)
            result += Text('fields: {').indented(indent_width=8)
            for prop in cast(List[RustProperty], entity.non_static_properties):
                field_line = self.__field_declaration(prop)
                result += Text(field_line).indented(indent_width=12)
            result += Text('}').indented(indent_width=8)
            result += Text('}').indented(indent_width=4)
            result += '}'
        else:
            # Entity without a type field
            result += 'div_entity! {'
            if doc is not None:
                result += doc.indented(indent_width=4)
            result += Text(f'pub struct {rust_name} {{').indented(indent_width=4)
            result += Text('fields: {').indented(indent_width=8)
            for prop in cast(List[RustProperty], entity.non_static_properties):
                field_line = self.__field_declaration(prop)
                result += Text(field_line).indented(indent_width=12)
            result += Text('}').indented(indent_width=8)
            result += Text('}').indented(indent_width=4)
            result += '}'

        return result

    def __field_declaration(self, prop: RustProperty) -> str:
        """Generate a single field line for div_entity! macro."""
        prefix = ''
        if prop.is_required:
            prefix = '#[required]\n'
        field_name = prop.rust_name
        field_type = prop.rust_type_annotation
        schema_info = self._build_schema_info_expr(prop)
        return f'{prefix}{field_name}: {field_type} => {schema_info},'

    def _build_schema_info_expr(self, prop: RustProperty) -> str:
        """Build a Rust FieldSchemaInfo { ... } expression for a property."""
        schema_type = prop.schema_field_type_expr()
        description = prop.description_expr()
        default_val = prop.default_value_expr()
        constraints = prop.constraints_expr()
        return (
            f'FieldSchemaInfo {{ '
            f'schema_type: {schema_type}, '
            f'description: {description}, '
            f'default_value: {default_val}, '
            f'constraints: {constraints} '
            f'}}'
        )

    def __inner_types_declarations(self, entity: RustEntity) -> List[Text]:
        """Generate declarations for inner types (nested entities, enums)."""
        result = []

        def sort_key(d: Declarable):
            return d.name

        # Inner string enumerations
        for inner in sorted(filter(lambda t: isinstance(t, StringEnumeration), entity.inner_types), key=sort_key):
            result.append(self._string_enumeration_declaration(cast(StringEnumeration, inner)))

        # Inner entity enumerations
        for inner in sorted(filter(lambda t: isinstance(t, EntityEnumeration), entity.inner_types), key=sort_key):
            result.append(self._entity_enumeration_declaration(cast(EntityEnumeration, inner)))

        # Inner entities
        for inner in sorted(filter(lambda t: isinstance(t, Entity), entity.inner_types), key=sort_key):
            update_base(inner)
            inner_entity = cast(RustEntity, inner)
            inner_decl = self.__entity_declaration_body(inner_entity)
            # Recursively add inner types of the inner entity
            for nested in self.__inner_types_declarations(inner_entity):
                inner_decl += EMPTY
                inner_decl += nested
            result.append(inner_decl)

        return result

    # ----------------------------------------------------------------
    # String enumeration declaration
    # ----------------------------------------------------------------

    def _string_enumeration_declaration(self, string_enumeration: StringEnumeration) -> Text:
        result = Text()
        enum_name = rust_full_name(string_enumeration)

        description = string_enumeration.description_doc()

        result += 'div_enum! {'
        if description and description.strip():
            doc = as_rust_doc_comment(description)
            if doc is not None:
                result += doc.indented(indent_width=4)
        result += Text(f'pub enum {enum_name} {{').indented(indent_width=4)
        for _, value in string_enumeration.cases:
            variant_name = utils.capitalize_camel_case(value)
            # Rename variants that are Rust keywords
            variant_name = VARIANT_RENAMES.get(variant_name, variant_name)
            result += Text(f'{variant_name} => "{value}",').indented(indent_width=8)
        result += Text('}').indented(indent_width=4)
        result += '}'
        return result

    # ----------------------------------------------------------------
    # Entity enumeration declaration (union/type alias)
    # ----------------------------------------------------------------

    def _entity_enumeration_declaration(self, entity_enumeration: EntityEnumeration) -> Text:
        result = Text()
        alias_name = rust_full_name(entity_enumeration)

        result += f'// {alias_name} is a union type (entity enumeration) of:'
        for name, obj in entity_enumeration.entities:
            if obj is not None:
                member_name = rust_full_name(obj)
            else:
                member_name = utils.capitalize_camel_case(name)
            result += f'//   - {member_name}'
        result += f'// In Rust, all these share the DivValue dynamic type.'

        return result
