from typing import Any, Callable, Generic, Type, TypeVar


T = TypeVar("T")
V = TypeVar("V")


# noinspection PyPep8Naming
class classproperty(Generic[T, V]):
    """ mypy friendly classproperty """

    def __init__(self, getter: Callable[[Type[T]], V]) -> None:
        self.getter = getattr(getter, "__func__", getter)

    def __get__(self, instance: Any, owner: Type[T]) -> V:
        return self.getter(owner)


__all__ = "classproperty",
