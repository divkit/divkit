from collections.abc import Callable
from typing import Any, Generic, TypeVar

T = TypeVar("T")
V = TypeVar("V")


# noinspection PyPep8Naming
class classproperty(Generic[T, V]):
    """mypy friendly classproperty"""

    def __init__(self, getter: Callable[[type[T]], V]) -> None:
        self.getter = getattr(getter, "__func__", getter)

    def __get__(self, instance: Any, owner: type[T]) -> V:
        return self.getter(owner)


__all__ = ("classproperty",)
