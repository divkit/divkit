"""Grouped table of contents for the generated documentation pages.

The grouping is described by a JSON file referenced from the documentation generator config
(``"toc": "<path>"``, resolved relative to the config file). Format::

    {
      "comment": "<optional free text>",
      "root": <group node>,
      "helpers": ["<page>", ...]
    }

A node is one of:

* ``"<page>"`` -- a page without children, named after the page;
* ``{"page": "<page>", "name": {...}, "items": [...], "any_of": "<page>"}`` -- a page node; ``name`` is
  optional and overrides the page name;
* ``{"name": {"ru": "...", "en": "..."}, "items": [...], "any_of": "<page>", "expanded": true}`` -- a
  group node without a page.

``any_of`` names an enumeration schema (a schema with ``anyOf``). Its members that are not placed
anywhere else in the tree are appended to the node's items in alphabetical order, so new elements,
actions or variable types show up without editing the config.

``helpers`` lists pages that stay ``hidden: true`` (base types and other pages that are only linked).
Every generated page must be placed in the tree or listed in ``helpers`` exactly once; every configured
name must have a generated page. Violations raise ``DocumentationTocError``.
"""

import json
import re
from dataclasses import dataclass, field
from typing import Dict, List, Optional, Set

GENERATED_PAGES_HREF_PREFIX = "concepts/divs/2/"

_PAGE_NODE_KEYS = {"page", "name", "items", "any_of"}
_GROUP_NODE_KEYS = {"name", "items", "any_of", "expanded"}


class DocumentationTocError(Exception):
    pass


@dataclass
class TocNode:
    page: Optional[str]
    name: Optional[Dict[str, str]]
    items: List["TocNode"] = field(default_factory=list)
    any_of: Optional[str] = None
    expanded: bool = False

    def display_name(self, lang: str) -> str:
        if self.name is not None:
            return self.name[lang]
        return self.page


@dataclass
class TocConfig:
    root: TocNode
    helpers: List[str]


@dataclass
class Toc:
    root: TocNode
    hidden: List[str]

    def render(self, lang: str) -> str:
        lines = ["items:"]
        _render_node(self.root, lang, indent=2, lines=lines)
        for page in self.hidden:
            lines.append(f"  - name: {_scalar(page)}")
            lines.append(f"    href: {_scalar(_href(page))}")
            lines.append("    hidden: true")
        return "\n".join(lines) + "\n"


def load_toc_config(path: str, languages: List[str]) -> TocConfig:
    try:
        with open(path, encoding="utf-8") as file:
            dictionary = json.load(file)
    except OSError as error:
        raise DocumentationTocError(f"{path}: cannot read the TOC config: {error}")
    except json.JSONDecodeError as error:
        raise DocumentationTocError(f"{path}: invalid JSON: {error}")
    errors: List[str] = []
    if (
        not isinstance(dictionary, dict)
        or not {"root", "helpers"} <= set(dictionary.keys())
        or not set(dictionary.keys()) <= {"root", "helpers", "comment"}
    ):
        raise DocumentationTocError(
            f'{path}: expected an object with the keys "root" and "helpers" (and an optional "comment")'
        )
    root = _parse_node(dictionary["root"], "root", languages, errors)
    if root is not None and root.page is not None:
        errors.append('root: must be a group node without "page"')
    helpers = dictionary["helpers"]
    if not isinstance(helpers, list) or not all(
        isinstance(helper, str) for helper in helpers
    ):
        errors.append("helpers: must be a list of page names")
        helpers = []
    if errors:
        raise DocumentationTocError(_format_errors(path, errors))
    return TocConfig(root=root, helpers=helpers)


def build_toc(
    config: TocConfig,
    pages: Set[str],
    enumerations: Dict[str, List[str]],
    config_path: str = "toc config",
) -> Toc:
    errors: List[str] = []
    placed: Dict[str, str] = {}

    def place(page: str, where: str) -> None:
        if page in placed:
            errors.append(f'{where}: page "{page}" is already placed at {placed[page]}')
        else:
            placed[page] = where

    def collect_explicit(node: TocNode, where: str) -> None:
        if node.page is not None:
            place(node.page, where)
        for index, item in enumerate(node.items):
            collect_explicit(item, f"{where}/{_node_label(item, index)}")

    collect_explicit(config.root, "root")
    for helper in config.helpers:
        place(helper, "helpers")

    for name in sorted(placed):
        if name not in pages:
            errors.append(
                f'{placed[name]}: "{name}" has no generated page (no such schema)'
            )

    def expand(node: TocNode, where: str) -> TocNode:
        items = [
            expand(item, f"{where}/{_node_label(item, index)}")
            for index, item in enumerate(node.items)
        ]
        if node.any_of is not None:
            if node.any_of not in enumerations:
                errors.append(
                    f'{where}: any_of "{node.any_of}" is not an enumeration schema (anyOf)'
                )
            else:
                for member in sorted(enumerations[node.any_of]):
                    if member in placed:
                        continue
                    if member not in pages:
                        errors.append(
                            f'{where}: any_of member "{member}" of "{node.any_of}" has no generated page'
                        )
                        continue
                    placed[member] = where
                    items.append(TocNode(page=member, name=None))
        if node.page is None and not items:
            errors.append(f"{where}: group has no items")
        return TocNode(
            page=node.page, name=node.name, items=items, expanded=node.expanded
        )

    root = expand(config.root, "root")

    for page in sorted(pages - set(placed)):
        errors.append(
            f'page "{page}" is in no group and not listed in "helpers"; '
            f'add it to a group (or to "helpers" if it is a base type that is only linked)'
        )

    if errors:
        raise DocumentationTocError(_format_errors(config_path, errors))
    return Toc(root=root, hidden=sorted(config.helpers))


def _parse_node(
    raw, where: str, languages: List[str], errors: List[str]
) -> Optional[TocNode]:
    if isinstance(raw, str):
        return TocNode(page=raw, name=None)
    if not isinstance(raw, dict):
        errors.append(f"{where}: expected a page name or an object")
        return None
    is_page = "page" in raw
    allowed = _PAGE_NODE_KEYS if is_page else _GROUP_NODE_KEYS
    unknown = sorted(set(raw.keys()) - allowed)
    if unknown:
        errors.append(f"{where}: unknown keys {unknown}")
    page = raw.get("page")
    if is_page and not isinstance(page, str):
        errors.append(f'{where}: "page" must be a string')
        page = None
    name = raw.get("name")
    if name is not None:
        if not isinstance(name, dict) or any(
            not isinstance(name.get(lang), str) or not name.get(lang)
            for lang in languages
        ):
            errors.append(
                f'{where}: "name" must have non-empty strings for {languages}'
            )
            name = None
    elif not is_page:
        errors.append(f'{where}: a group node needs "name"')
    any_of = raw.get("any_of")
    if any_of is not None and not isinstance(any_of, str):
        errors.append(f'{where}: "any_of" must be a page name')
        any_of = None
    expanded = raw.get("expanded", False)
    if not isinstance(expanded, bool):
        errors.append(f'{where}: "expanded" must be a boolean')
        expanded = False
    raw_items = raw.get("items", [])
    if not isinstance(raw_items, list):
        errors.append(f'{where}: "items" must be a list')
        raw_items = []
    node = TocNode(page=page, name=name, any_of=any_of, expanded=expanded)
    for index, raw_item in enumerate(raw_items):
        item = _parse_node(
            raw_item, f"{where}/{_node_label(node, index)}[{index}]", languages, errors
        )
        if item is not None:
            node.items.append(item)
    return node


def _node_label(node: TocNode, index: int) -> str:
    if node.page is not None:
        return node.page
    if node.name:
        return next(iter(node.name.values()))
    return f"[{index}]"


def _render_node(node: TocNode, lang: str, indent: int, lines: List[str]) -> None:
    prefix = " " * indent
    lines.append(f"{prefix}- name: {_scalar(node.display_name(lang))}")
    if node.page is not None:
        lines.append(f"{prefix}  href: {_scalar(_href(node.page))}")
    if node.expanded:
        lines.append(f"{prefix}  expanded: true")
    if node.items:
        lines.append(f"{prefix}  items:")
        for item in node.items:
            _render_node(item, lang, indent + 4, lines)


def _href(page: str) -> str:
    return f"{GENERATED_PAGES_HREF_PREFIX}{page}.md"


_PLAIN_SCALAR = re.compile(r'[^\s\-?:,\[\]{}#&*!|>\'"%@`][^#]*')
_RESERVED_SCALARS = {"true", "false", "yes", "no", "on", "off", "null", "~", "y", "n"}


def _scalar(value: str) -> str:
    plain = (
        _PLAIN_SCALAR.fullmatch(value) is not None
        and ": " not in value
        and not value.endswith((" ", ":"))
        and value.lower() not in _RESERVED_SCALARS
        and re.fullmatch(r"[-+]?[0-9][0-9_.eE+-]*", value) is None
    )
    if plain:
        return value
    return json.dumps(value, ensure_ascii=False)


def _format_errors(path: str, errors: List[str]) -> str:
    return "\n".join(
        [f"Documentation TOC config {path} does not match the schema:"]
        + [f"  - {e}" for e in errors]
    )
