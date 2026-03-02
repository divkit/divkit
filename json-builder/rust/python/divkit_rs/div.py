from types import SimpleNamespace

from divkit_rs import *  # noqa: F401,F403
from divkit_rs import __all__ as _root_all


div_timer = SimpleNamespace(DivTimer=DivTimer)
div_trigger = SimpleNamespace(DivTrigger=DivTrigger)
div_variable = SimpleNamespace(
    DivVariable=DivVariable,
    ArrayVariable=ArrayVariable,
    BooleanVariable=BooleanVariable,
    ColorVariable=ColorVariable,
    DictVariable=DictVariable,
    IntegerVariable=IntegerVariable,
    NumberVariable=NumberVariable,
    PropertyVariable=PropertyVariable,
    StringVariable=StringVariable,
    UrlVariable=UrlVariable,
)

__all__ = tuple(_root_all) + ("div_timer", "div_trigger", "div_variable")
