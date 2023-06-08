from __future__ import annotations

import uuid
from dataclasses import dataclass, field
from typing import Any, Dict, Mapping, Optional


@dataclass()
class _Field:
    uid: uuid.UUID = field(default_factory=uuid.uuid4)
    name: Optional[str] = field(default=None)
    default: Optional[Any] = field(default=None)
    description: Optional[str] = field(default=None)
    constraints: Dict[str, Any] = field(default_factory=dict)
    ref_to: Optional[_Field] = field(default=None)
    is_ref: bool = field(default=False)

    @property
    def field_name(self) -> str:
        if not self.name:
            raise ValueError("Field 'name' not initialized")
        return self.name

    def apply_constraints(self, constraints: Mapping[str, Any]) -> None:
        for c_key, c_value in constraints.items():
            if self.constraints.get(c_key) and self.constraints[
                c_key
            ] != c_value:
                raise ValueError(
                    f"Incompatible constraints: {c_key} = "
                    f"{self.constraints[c_key]} and {c_key} = {c_value}",
                )
            self.constraints[c_key] = c_value


def _make_constraints(**kwargs: Any) -> Dict[str, Any]:
    return {k: v for k, v in kwargs.items() if v is not None}


def Field(
    name: Optional[str] = None,
    description: Optional[str] = None,
    default: Optional[Any] = None,
    format: Optional[str] = None,
    gt: Optional[float] = None,
    ge: Optional[float] = None,
    lt: Optional[float] = None,
    le: Optional[float] = None,
    multiple_of: Optional[float] = None,
    min_items: Optional[int] = None,
    max_items: Optional[int] = None,
    unique_items: Optional[bool] = None,
    min_length: Optional[int] = None,
    max_length: Optional[int] = None,
    regex: Optional[str] = None,
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
