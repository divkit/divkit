from __future__ import annotations
from typing import List, Optional, cast

import re as _re

from ...schema.modeling.entities import (
    Declarable,
    DescriptionLanguage,
    PropertyType,
    Property,
    Entity,
    Array,
    Bool,
    BoolInt,
    Color,
    Dictionary,
    RawObject,
    RawArray,
    Double,
    Int,
    Object,
    StaticString,
    String,
    StringEnumeration,
    EntityEnumeration,
    Url,
)
from ... import utils
from ...schema.modeling.text import Text, EMPTY


# Rust reserved keywords that need r# prefix when used as identifiers
RUST_RESERVED = frozenset({
    'as', 'break', 'const', 'continue', 'crate', 'else', 'enum', 'extern',
    'false', 'fn', 'for', 'if', 'impl', 'in', 'let', 'loop', 'match',
    'mod', 'move', 'mut', 'pub', 'ref', 'return', 'self', 'static',
    'struct', 'super', 'trait', 'true', 'type', 'unsafe', 'use', 'where',
    'while', 'async', 'await', 'dyn', 'abstract', 'become', 'box', 'do',
    'final', 'macro', 'override', 'priv', 'try', 'typeof', 'unsized',
    'virtual', 'yield',
})

# Keywords that cannot be used with r# (truly reserved)
RUST_STRICT_KEYWORDS = frozenset({'self', 'Self', 'crate', 'super'})

# Mapping for enum variant names that are Rust keywords (CamelCase)
VARIANT_RENAMES = {
    'Self': 'Self_',
    'Crate': 'Crate_',
    'Super': 'Super_',
}


def update_base(obj: Declarable) -> Declarable:
    """Upgrade an Entity to RustEntity (in-place class swap)."""
    if isinstance(obj, Entity):
        obj.__class__ = RustEntity
        cast(RustEntity, obj).update_base()
    return obj


def update_property_type_base(property_type: PropertyType):
    """Upgrade a PropertyType to the corresponding Rust wrapper."""
    if isinstance(property_type, Array):
        property_type.__class__ = RustArray
        cast(RustArray, property_type).update_base()
    elif isinstance(property_type, Bool):
        property_type.__class__ = RustBool
    elif isinstance(property_type, BoolInt):
        property_type.__class__ = RustBoolInt
    elif isinstance(property_type, Color):
        property_type.__class__ = RustColor
    elif isinstance(property_type, Dictionary):
        property_type.__class__ = RustDictionary
    elif isinstance(property_type, RawObject):
        property_type.__class__ = RustRawObject
    elif isinstance(property_type, RawArray):
        property_type.__class__ = RustRawArray
    elif isinstance(property_type, Double):
        property_type.__class__ = RustDouble
    elif isinstance(property_type, Int):
        property_type.__class__ = RustInt
    elif isinstance(property_type, Object):
        property_type.__class__ = RustObject
        cast(RustObject, property_type).update_base()
    elif isinstance(property_type, StaticString):
        property_type.__class__ = RustStaticString
    elif isinstance(property_type, String):
        property_type.__class__ = RustString
    elif isinstance(property_type, Url):
        property_type.__class__ = RustUrl
    else:
        raise NotImplementedError


def rust_full_name(obj: Declarable) -> str:
    """Get the full Rust type name (CamelCase), prefixed with parent if nested."""
    full_name = utils.capitalize_camel_case(obj.name)
    if obj.parent is not None:
        full_name = utils.capitalize_camel_case(obj.parent.name) + full_name
    return full_name


def rust_field_name(name: str) -> str:
    """Convert a property name to a Rust field name (snake_case), escaping reserved words."""
    sname = utils.snake_case(name)
    if sname in RUST_RESERVED:
        return f'r#{sname}'
    return sname


def as_rust_doc_comment(s: str) -> Optional[Text]:
    """Format a description string as Rust /// doc comments."""
    if not s or not s.strip():
        return None
    result = Text()
    cleaned = s.replace('<li>', '').replace('</li>', '')
    words = cleaned.split()
    line = ''
    for word in words:
        if not line:
            line = str(word)
        elif (len(line) + len(word)) > 80:
            result += f'/// {line}'
            line = str(word)
        else:
            line += f' {word}'
    if line:
        result += f'/// {line}'
    return result


class RustEntity(Entity):
    def update_base(self):
        for prop in self.properties:
            prop.__class__ = RustProperty
            cast(RustProperty, prop).update_base()

    @property
    def static_type_value(self) -> Optional[str]:
        """Return the static 'type' value if present (e.g. 'text' for DivText)."""
        return self.static_type

    @property
    def rust_name(self) -> str:
        return rust_full_name(self)

    @property
    def non_static_properties(self) -> List[RustProperty]:
        """Properties that are NOT the static type field."""
        return [cast(RustProperty, p) for p in self.properties
                if not isinstance(p.property_type, StaticString)]

    @property
    def required_field_names(self) -> List[str]:
        """Field names that are required (not optional)."""
        return [utils.snake_case(p.name) for p in self.non_static_properties if not p.optional]

    @property
    def all_field_names(self) -> List[str]:
        """All non-static field names."""
        return [utils.snake_case(p.name) for p in self.non_static_properties]


class RustProperty(Property):
    def update_base(self):
        update_property_type_base(self.property_type)

    @property
    def rust_name(self) -> str:
        return rust_field_name(self.name)

    @property
    def property_type_rust(self) -> RustPropertyType:
        return cast(RustPropertyType, self.property_type)

    @property
    def rust_type_annotation(self) -> str:
        """Return the documentary type annotation for div_entity! macro."""
        return self.property_type_rust.rust_type_name()

    @property
    def is_required(self) -> bool:
        return not self.optional

    def schema_field_type_expr(self) -> str:
        """Return a Rust expression for SchemaFieldType, wrapping with Expr AnyOf if needed."""
        base = self.property_type_rust.schema_field_type_expr()
        if self.supports_expressions:
            return f'SchemaFieldType::AnyOf(vec![{base}, SchemaFieldType::Expr])'
        return base

    def description_expr(self) -> str:
        """Return a Rust expression for the description Option<&'static str>."""
        desc = self.description_doc(DescriptionLanguage.EN)
        if desc and desc.strip():
            # Escape backslashes and double quotes for Rust string literal
            escaped = desc.replace('\\', '\\\\').replace('"', '\\"').replace('\n', ' ')
            return f'Some("{escaped}")'
        return 'None'

    def default_value_expr(self) -> str:
        """Return a Rust expression for Option<serde_json::Value> default."""
        if self.default_value is None:
            return 'None'
        dv = self.default_value
        pt = self.property_type
        # Integer types
        if isinstance(pt, (Int, BoolInt)):
            try:
                val = int(dv)
                return f'Some(serde_json::json!({val}))'
            except (ValueError, TypeError):
                pass
        # Double/Number types
        if isinstance(pt, Double):
            try:
                val = float(dv)
                return f'Some(serde_json::json!({val}))'
            except (ValueError, TypeError):
                pass
        # Boolean types
        if isinstance(pt, (Bool, BoolInt)):
            if dv in ('true', '1', 'True'):
                return 'Some(serde_json::json!(true))'
            elif dv in ('false', '0', 'False'):
                return 'Some(serde_json::json!(false))'
        # String/Color/Url values
        if isinstance(pt, (Color, Url, String)):
            escaped = dv.replace('\\', '\\\\').replace('"', '\\"')
            return f'Some(serde_json::json!("{escaped}"))'
        # For enums (Object wrapping StringEnumeration)
        if isinstance(pt, Object) and isinstance(pt.object, StringEnumeration):
            escaped = dv.replace('\\', '\\\\').replace('"', '\\"')
            return f'Some(serde_json::json!("{escaped}"))'
        # Fallback: try as string
        if dv:
            escaped = dv.replace('\\', '\\\\').replace('"', '\\"')
            return f'Some(serde_json::json!("{escaped}"))'
        return 'None'

    def constraints_expr(self) -> str:
        """Return a Rust expression for Constraints { ... }."""
        parts = []
        pt = self.property_type
        # Parse Int/Double constraints like "number >= 0 && number <= 360"
        if isinstance(pt, (Int, Double)) and hasattr(pt, 'constraint') and pt.constraint:
            parts.extend(_parse_number_constraint(pt.constraint))
        # String constraints
        if isinstance(pt, String):
            if hasattr(pt, 'min_length') and pt.min_length and pt.min_length > 0:
                parts.append(f'min_length: Some({pt.min_length})')
            if hasattr(pt, 'regex') and pt.regex:
                pattern = pt.regex.pattern if hasattr(pt.regex, 'pattern') else str(pt.regex)
                escaped = pattern.replace('\\', '\\\\').replace('"', '\\"')
                parts.append(f'regex: Some("{escaped}".into())')
        # Color has uri format
        if isinstance(pt, Color):
            parts.append('format: Some("color".into())')
        if isinstance(pt, Url):
            parts.append('format: Some("uri".into())')
        # Array constraints
        if isinstance(pt, Array):
            if hasattr(pt, 'min_items') and pt.min_items and pt.min_items > 0:
                parts.append(f'min_items: Some({pt.min_items})')

        if not parts:
            return 'Constraints::default()'
        fields = ', '.join(parts)
        return f'Constraints {{ {fields}, ..Constraints::default() }}'


def _parse_number_constraint(constraint: str) -> list:
    """Parse a constraint string like 'number >= 0 && number <= 360' into Constraints fields."""
    parts = []
    # Split by &&
    clauses = [c.strip() for c in constraint.split('&&')]
    for clause in clauses:
        # Match patterns like "number >= 0", "number > 0", "number <= 100", "number < 100"
        m = _re.match(r'number\s*(>=|>|<=|<)\s*(-?[\d.]+)', clause)
        if not m:
            continue
        op = m.group(1)
        val = m.group(2)
        # Use float representation for the value
        if '.' in val:
            rust_val = val
        else:
            rust_val = f'{val}.0'
        if op == '>=':
            parts.append(f'minimum: Some({rust_val})')
        elif op == '>':
            parts.append(f'exclusive_minimum: Some({rust_val})')
        elif op == '<=':
            parts.append(f'maximum: Some({rust_val})')
        elif op == '<':
            parts.append(f'exclusive_maximum: Some({rust_val})')
    return parts


class RustPropertyType(PropertyType):
    def rust_type_name(self) -> str:
        """Return the Rust type string for use in div_entity! macro fields."""
        if isinstance(self, RustInt):
            return 'i64'
        elif isinstance(self, RustDouble):
            return 'f64'
        elif isinstance(self, (RustBool, RustBoolInt)):
            return 'bool'
        elif isinstance(self, (RustColor, RustString, RustUrl)):
            return 'String'
        elif isinstance(self, RustStaticString):
            return 'String'
        elif isinstance(self, (RustDictionary, RustRawObject)):
            return 'DivValue'
        elif isinstance(self, RustRawArray):
            return 'Vec<DivValue>'
        elif isinstance(self, RustArray):
            inner = cast(RustArray, self).property_type_rust.rust_type_name()
            return f'Vec<{inner}>'
        elif isinstance(self, RustObject):
            obj = self.object
            if obj is None:
                return 'DivValue'
            if isinstance(obj, StringEnumeration):
                return 'String'
            if isinstance(obj, EntityEnumeration):
                return 'DivValue'
            return rust_full_name(obj)
        return 'DivValue'

    def schema_field_type_expr(self) -> str:
        """Return a Rust expression string for SchemaFieldType."""
        if isinstance(self, RustInt):
            return 'SchemaFieldType::Integer'
        elif isinstance(self, RustDouble):
            return 'SchemaFieldType::Number'
        elif isinstance(self, (RustBool, RustBoolInt)):
            return 'SchemaFieldType::Boolean'
        elif isinstance(self, (RustColor, RustUrl)):
            return 'SchemaFieldType::String'
        elif isinstance(self, RustString):
            return 'SchemaFieldType::String'
        elif isinstance(self, RustStaticString):
            return 'SchemaFieldType::String'
        elif isinstance(self, (RustDictionary, RustRawObject)):
            return 'SchemaFieldType::Object'
        elif isinstance(self, RustRawArray):
            return 'SchemaFieldType::Array(Box::new(SchemaFieldType::Any))'
        elif isinstance(self, RustArray):
            inner = cast(RustArray, self).property_type_rust.schema_field_type_expr()
            return f'SchemaFieldType::Array(Box::new({inner}))'
        elif isinstance(self, RustObject):
            obj = self.object
            if obj is None:
                return 'SchemaFieldType::Object'
            if isinstance(obj, StringEnumeration):
                values = ', '.join(f'"{v}".into()' for _, v in obj.cases)
                return f'SchemaFieldType::Enum(vec![{values}])'
            if isinstance(obj, EntityEnumeration):
                refs = ', '.join(
                    f'SchemaFieldType::Ref("{rust_full_name(e) if e is not None else utils.capitalize_camel_case(n)}".into())'
                    for n, e in obj.entities
                )
                return f'SchemaFieldType::AnyOf(vec![{refs}])'
            return f'SchemaFieldType::Ref("{rust_full_name(obj)}".into())'
        return 'SchemaFieldType::Any'


class RustArray(RustPropertyType, Array):
    def update_base(self):
        if not isinstance(self.property_type, RustPropertyType):
            update_property_type_base(self.property_type)

    @property
    def property_type_rust(self) -> RustPropertyType:
        return cast(RustPropertyType, self.property_type)


class RustBool(RustPropertyType, Bool):
    pass


class RustBoolInt(RustPropertyType, BoolInt):
    pass


class RustColor(RustPropertyType, Color):
    pass


class RustDictionary(RustPropertyType, Dictionary):
    pass


class RustRawObject(RustPropertyType, RawObject):
    pass


class RustRawArray(RustPropertyType, RawArray):
    pass


class RustDouble(RustPropertyType, Double):
    pass


class RustInt(RustPropertyType, Int):
    pass


class RustObject(RustPropertyType, Object):
    def update_base(self):
        if isinstance(self.object, Entity):
            self.object.__class__ = RustEntity
            cast(RustEntity, self.object).update_base()


class RustStaticString(RustPropertyType, StaticString):
    pass


class RustString(RustPropertyType, String):
    pass


class RustUrl(RustPropertyType, Url):
    pass
