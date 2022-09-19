# -*- coding: utf-8 -*-
from setuptools import setup

packages = \
['pydivkit', 'pydivkit.core', 'pydivkit.core.types', 'pydivkit.div']

package_data = \
{'': ['*']}

install_requires = \
['inflection>=0.5.1,<0.6.0']

setup_kwargs = {
    'name': 'pydivkit',
    'version': '3.0.1',
    'description': '',
    'long_description': None,
    'author': 'Vladislav Bakaev',
    'author_email': 'bakaev-vlad@yandex-team.ru',
    'maintainer': None,
    'maintainer_email': None,
    'url': None,
    'packages': packages,
    'package_data': package_data,
    'install_requires': install_requires,
    'python_requires': '>=3.8,<4.0',
}


setup(**setup_kwargs)
