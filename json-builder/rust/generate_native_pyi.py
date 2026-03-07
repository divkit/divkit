#!/usr/bin/env python3
"""Generate `python/divkit_rs/_native.pyi` from DivKit schema."""

from __future__ import annotations

import argparse
import contextlib
import difflib
import io
import keyword
from dataclasses import dataclass
from pathlib import Path

import api_generator.utils as ag_utils
from api_generator.config import Config
from api_generator.generators.rust.rust_entities import (
    RustArray,
    RustBool,
    RustBoolInt,
    RustColor,
    RustDictionary,
    RustDouble,
    RustEntity,
    RustInt,
    RustObject,
    RustProperty,
    RustRawArray,
    RustRawObject,
    RustStaticString,
    RustString,
    RustUrl,
    rust_full_name,
    update_base,
)
from api_generator.schema.modeling import build_objects
from api_generator.schema.modeling.entities import (
    Declarable,
    Entity,
    EntityEnumeration,
    Object,
    PropertyType,
    StringEnumeration,
)
from api_generator.schema.preprocessing import schema_preprocessing
from api_generator.utils import upper_snake_case

HEADER = """# Generated code. Do not modify.

from __future__ import annotations

import enum
from typing import Any, ClassVar, TypeAlias
"""


@dataclass(frozen=True)
class EntityDecl:
    name: str
    props: list[RustProperty]


@dataclass(frozen=True)
class EnumDecl:
    name: str
    cases: list[str]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Generate python/divkit_rs/_native.pyi from DivKit schema."
    )
    parser.add_argument("-s", "--schema", required=True, help="Path to schema directory")
    parser.add_argument(
        "-o",
        "--output",
        default="python/divkit_rs/_native.pyi",
        help="Output .pyi path",
    )
    parser.add_argument(
        "--check",
        action="store_true",
        help="Fail if generated output differs from existing file",
    )
    return parser.parse_args()


def load_objects(project_root: Path, schema_dir: Path) -> list[Declarable]:
    config_path = project_root / "codegen_config.json"
    generator_path = project_root.parent.parent / "api_generator" / "api_generator"
    output_for_hashes = str(project_root / "src" / "generated")

    config = Config(
        str(generator_path),
        str(config_path),
        str(schema_dir),
        output_for_hashes,
    )
    with contextlib.redirect_stdout(io.StringIO()):
        root = schema_preprocessing(config)
        objects = sorted(build_objects(root, config.generation), key=lambda obj: obj.name)
    return [update_base(obj) for obj in objects]


def collect_entities(entity: RustEntity) -> list[EntityDecl]:
    result: list[EntityDecl] = [
        EntityDecl(
            name=entity.rust_name,
            props=[
                p
                for p in entity.non_static_properties
                if not isinstance(p.property_type, RustStaticString)
            ],
        )
    ]

    inner_entities = sorted(
        [inner for inner in entity.inner_types if isinstance(inner, Entity)],
        key=lambda d: d.name,
    )
    for inner in inner_entities:
        update_base(inner)
        result.extend(collect_entities(inner))
    return result


def collect_string_enums(entity: RustEntity) -> list[EnumDecl]:
    result: list[EnumDecl] = []

    inner_enums = sorted(
        [inner for inner in entity.inner_types if isinstance(inner, StringEnumeration)],
        key=lambda d: d.name,
    )
    for inner in inner_enums:
        result.append(
            EnumDecl(name=rust_full_name(inner), cases=[value for _, value in inner.cases])
        )

    inner_entities = sorted(
        [inner for inner in entity.inner_types if isinstance(inner, Entity)],
        key=lambda d: d.name,
    )
    for inner in inner_entities:
        update_base(inner)
        result.extend(collect_string_enums(inner))

    return result


def collect_declarations(
    objects: list[Declarable],
) -> tuple[list[EntityDecl], list[EnumDecl], dict[str, list[str]]]:
    entities: list[EntityDecl] = []
    enums: list[EnumDecl] = []

    for obj in objects:
        if isinstance(obj, RustEntity):
            entities.extend(collect_entities(obj))
            enums.extend(collect_string_enums(obj))

    top_level_enums = [obj for obj in objects if isinstance(obj, StringEnumeration)]
    for enum_obj in top_level_enums:
        enums.append(
            EnumDecl(name=rust_full_name(enum_obj), cases=[value for _, value in enum_obj.cases])
        )

    alias_members: dict[str, list[str]] = {}
    top_level_entity_enums = [obj for obj in objects if isinstance(obj, EntityEnumeration)]
    for enum_obj in top_level_entity_enums:
        alias_name = rust_full_name(enum_obj)
        members: list[str] = []
        seen: set[str] = set()
        for member_name, member_obj in enum_obj.entities:
            resolved = (
                rust_full_name(member_obj)
                if member_obj is not None
                else ag_utils.capitalize_camel_case(member_name)
            )
            if resolved not in seen:
                seen.add(resolved)
                members.append(resolved)
        alias_members[alias_name] = members

    # Deduplicate by name, preserving last entry (deterministic due sorted walk).
    entity_by_name = {ent.name: ent for ent in entities}
    enum_by_name = {enum_decl.name: enum_decl for enum_decl in enums}

    sorted_entities = [entity_by_name[name] for name in sorted(entity_by_name)]
    sorted_enums = [enum_by_name[name] for name in sorted(enum_by_name)]
    sorted_alias_members = {k: alias_members[k] for k in sorted(alias_members)}
    return sorted_entities, sorted_enums, sorted_alias_members


def is_identifier(name: str) -> bool:
    return name.isidentifier() and not keyword.iskeyword(name)


def join_union(types: list[str]) -> str:
    seen: set[str] = set()
    ordered: list[str] = []
    for t in types:
        if t not in seen:
            seen.add(t)
            ordered.append(t)
    return " | ".join(ordered)


def has_str_union(annotation: str) -> bool:
    parts = {part.strip() for part in annotation.split("|")}
    return "str" in parts


def map_property_type(
    ptype: PropertyType,
    alias_names: set[str],
) -> str:
    if isinstance(ptype, RustInt):
        return "int"
    if isinstance(ptype, RustDouble):
        return "float"
    if isinstance(ptype, RustBool):
        return "bool"
    if isinstance(ptype, RustBoolInt):
        return "bool | int"
    if isinstance(ptype, (RustString, RustColor, RustUrl)):
        return "str"
    if isinstance(ptype, (RustDictionary, RustRawObject)):
        return "dict[str, Any]"
    if isinstance(ptype, RustRawArray):
        return "list[Any]"
    if isinstance(ptype, RustArray):
        inner = map_property_type(ptype.property_type, alias_names)
        return f"list[{inner}]"
    if isinstance(ptype, RustObject):
        obj = ptype.object
        if obj is None:
            return "Any"
        if isinstance(obj, StringEnumeration):
            enum_name = rust_full_name(obj)
            return f"{enum_name} | str"
        if isinstance(obj, EntityEnumeration):
            alias_name = rust_full_name(obj)
            return alias_name if alias_name in alias_names else "PyDivEntity"
        if isinstance(obj, Entity):
            return rust_full_name(obj)
        return "Any"
    if isinstance(ptype, Object):
        return "Any"
    return "Any"


def property_annotation(prop: RustProperty, alias_names: set[str]) -> str:
    annotation = map_property_type(prop.property_type, alias_names)
    if prop.supports_expressions and not has_str_union(annotation):
        annotation = join_union([annotation, "str"])
    return annotation


def emit_core_class_stubs() -> str:
    return """
class PyDivEntity:
    template_name: ClassVar[str]

    def __init__(self, **kwargs: Any) -> None: ...
    def dict(self) -> dict[str, Any]: ...
    def build(self) -> dict[str, Any]: ...
    def related_templates(self) -> set[type[PyDivEntity]]: ...
    def schema(self, exclude_fields: list[str] | None = None) -> dict[str, Any]: ...
    @classmethod
    def template(cls) -> dict[str, Any]: ...
    @classmethod
    def update_forward_refs(cls) -> None: ...


class PyDivData:
    def __init__(self, log_id: str, states: list[PyDivDataState]) -> None: ...
    def dict(self) -> dict[str, Any]: ...
    def build(self) -> dict[str, Any]: ...


class PyDivDataState:
    def __init__(self, state_id: int, div: PyDivEntity) -> None: ...


def make_div(div: PyDivEntity) -> dict[str, Any]: ...
def make_card(log_id: str, div: PyDivEntity) -> dict[str, Any]: ...
def compat_dump(value: Any) -> Any: ...
def normalize_pydivkit_json(value: Any) -> Any: ...
def register_type_meta(
    class_name: str,
    type_name: str | None,
    field_names: list[str],
    required_fields: list[str],
) -> None: ...
"""


def emit_enum_stub(enum_decl: EnumDecl) -> str:
    lines = [f"class {enum_decl.name}(str, enum.Enum):"]
    if not enum_decl.cases:
        lines.append("    pass")
    else:
        for case in enum_decl.cases:
            lines.append(f"    {upper_snake_case(case)} = {case!r}")
    return "\n".join(lines)


def emit_entity_stub(entity: EntityDecl, alias_names: set[str]) -> str:
    lines = [f"class {entity.name}(PyDivEntity):", "    def __init__("]

    required_entries: list[tuple[str, RustProperty]] = []
    optional_entries: list[tuple[str, RustProperty]] = []

    for prop in entity.props:
        name = ag_utils.snake_case(prop.name)
        if not is_identifier(name):
            continue
        if prop.optional:
            optional_entries.append((name, prop))
        else:
            required_entries.append((name, prop))

    has_params = bool(required_entries or optional_entries)
    params: list[str] = ["        self,"]
    if has_params:
        params.append("        *,")

    for name, prop in required_entries:
        params.append(f"        {name}: {property_annotation(prop, alias_names)},")

    for name, prop in optional_entries:
        ann = property_annotation(prop, alias_names)
        params.append(f"        {name}: {ann} | None = None,")

    lines.extend(params)
    lines.append("    ) -> None: ...")
    return "\n".join(lines)


def emit_aliases(alias_members: dict[str, list[str]]) -> str:
    lines: list[str] = []
    for alias_name, members in alias_members.items():
        if members:
            lines.append(f"{alias_name}: TypeAlias = {' | '.join(members)}")
        else:
            lines.append(f"{alias_name}: TypeAlias = PyDivEntity")
    return "\n".join(lines)


def render_stub(
    entities: list[EntityDecl],
    enums: list[EnumDecl],
    alias_members: dict[str, list[str]],
) -> str:
    alias_set = set(alias_members)
    parts: list[str] = [HEADER.rstrip(), emit_core_class_stubs().strip()]

    if enums:
        parts.append("\n\n".join(emit_enum_stub(enum_decl) for enum_decl in enums))

    if entities:
        parts.append("\n\n".join(emit_entity_stub(entity, alias_set) for entity in entities))

    if alias_members:
        parts.append(emit_aliases(alias_members))

    return "\n\n\n".join(parts).rstrip() + "\n"


def write_or_check(output_path: Path, content: str, check: bool) -> int:
    existing = output_path.read_text(encoding="utf-8") if output_path.exists() else ""
    if check:
        if existing == content:
            return 0
        diff = difflib.unified_diff(
            existing.splitlines(keepends=True),
            content.splitlines(keepends=True),
            fromfile=str(output_path),
            tofile=f"{output_path} (generated)",
        )
        print("".join(diff), end="")
        return 1

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(content, encoding="utf-8")
    return 0


def main() -> int:
    args = parse_args()
    project_root = Path(__file__).resolve().parent
    schema_dir = Path(args.schema).resolve()
    output_path = Path(args.output)
    if not output_path.is_absolute():
        output_path = project_root / output_path

    objects = load_objects(project_root, schema_dir)
    entities, enums, alias_members = collect_declarations(objects)
    content = render_stub(entities, enums, alias_members)
    return write_or_check(output_path, content, args.check)


if __name__ == "__main__":
    raise SystemExit(main())
