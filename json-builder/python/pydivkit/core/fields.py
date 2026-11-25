from __future__ import annotations

import uuid
from collections.abc import Mapping
from dataclasses import dataclass, field
from typing import Any


@dataclass()
class _Field:
    uid: uuid.UUID = field(default_factory=uuid.uuid4)
    name: str | None = field(default=None)
    default: Any | None = field(default=None)
    description: str | None = field(default=None)
    constraints: dict[str, Any] = field(default_factory=dict)
    ref_to: _Field | None = field(default=None)
    is_ref: bool = field(default=False)

    @property
    def field_name(self) -> str:
        if not self.name:
            raise ValueError("Field 'name' not initialized")
        return self.name

    def apply_constraints(self, constraints: Mapping[str, Any]) -> None:
        for c_key, c_value in constraints.items():
            if self.constraints.get(c_key) and self.constraints[c_key] != c_value:
                raise ValueError(
                    f"Incompatible constraints: {c_key} = "
                    f"{self.constraints[c_key]} and {c_key} = {c_value}",
                )
            self.constraints[c_key] = c_value


def _make_constraints(**kwargs: Any) -> dict[str, Any]:
    return {k: v for k, v in kwargs.items() if v is not None}


def Field(
    name: str | None = None,
    description: str | None = None,
    default: Any | None = None,
    format: str | None = None,
    gt: float | None = None,
    ge: float | None = None,
    lt: float | None = None,
    le: float | None = None,
    multiple_of: float | None = None,
    min_items: int | None = None,
    max_items: int | None = None,
    unique_items: bool | None = None,
    min_length: int | None = None,
    max_length: int | None = None,
    regex: str | None = None,
) -> Any:
    return _Field(
        name=name,
        description=description,
        default=default,
        constraints=_make_constraints(
            **{
                "format": format,
                "exclusiveMinimum": gt,
                "minimum": ge,
                "exclusiveMaximum": lt,
                "maximum": le,
                "multipleOf": multiple_of,
                "minItems": min_items,
                "maxItems": max_items,
                "uniqueItems": unique_items,
                "minLength": min_length,
                "maxLength": max_length,
                "regex": regex,
            },
        ),
    )


def Ref(field: Any) -> Any:
    if not isinstance(field, _Field):
        raise ValueError(
            f"Value {field} has wrong type. Expected type is Field.",
        )
    field.is_ref = True
    return _Field(ref_to=field)


class Expr:
    __slots__ = ("v",)
    v: str

    def __init__(self, v: str):
        if not v.startswith("@{") or not v.endswith("}"):
            raise ValueError(f"failed to initiate Expr with {v}")
        self.v = v

    def __repr__(self) -> str:
        return f"Expr({self.v})"

    def __str__(self) -> str:
        return self.v
