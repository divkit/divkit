"""pydivkit.core compatibility surface mapped to divkit_rs.core."""

from divkit_rs import BaseDiv, BaseEntity
from divkit_rs.core import Expr, Field, Ref

__all__ = (
    "Expr",
    "Field",
    "Ref",
    "BaseDiv",
    "BaseEntity",
)
