#!/usr/bin/env python3
'''Helper to generate animation scenarios.'''
from copy import deepcopy
import os
import json
from typing import Tuple, List, Dict, Any

INTERPOLATORS = [
    "linear",
    "ease",
    "ease_in",
    "ease_out",
    "ease_in_out",
    "spring",
]

DURATION = 500


def _scale_animation() -> Dict[str, Any]:
    return {
        "name": "scale",
        "start_value": 1,
        "duration": DURATION,
        "end_value": 0.6
    }


def _fade_animation() -> Dict[str, Any]:
    return {
        "name": "fade",
        "start_value": 1,
        "duration": DURATION,
        "end_value": 0.2
    }


def _set_animation(*animations: Tuple[Dict[str, Any]]) -> Dict[str, Any]:
    return {"name": "set", "items": list(animations)}


def _no_animation() -> Dict[str, Any]:
    return {"name": "no_animation"}


def _cap(s: str) -> str:
    return f'{s[0].upper()}{s[1:]}'


def _create_for_animation(
        template: Dict[str, Any], animation: Dict[str, Any], interpolator: str,
        **params: Dict[str, Any]) -> Tuple[str, Dict[str, Any]]:
    name = animation['name']
    text = [f"{_cap(name).replace('_', ' ')}"]
    log_id = ['animation']
    if interpolator is not None:
        text.append(f'interpolator: {interpolator}')
        log_id.append(interpolator)
        animation['interpolator'] = interpolator
    params_str = ', '.join([f'{k}: {v}' for k, v in params.items()])
    if params_str:
        text.append(params_str)
    log_id.append(name)
    template['templates']['label_text']['action_animation'] = animation
    div = template['card']['states'][0]['div']
    div['text'] = ', '.join(text)
    div['actions'][0]['log_id'] = '_'.join(log_id)
    return f'interpolator_{interpolator}/{name}.json', template


def _create_for_scale(template: Dict[str, Any],
                      interpolator: str) -> Tuple[str, Dict[str, Any]]:
    template_copy = deepcopy(template)
    animation = _scale_animation()
    return _create_for_animation(template_copy,
                                 animation,
                                 interpolator,
                                 duration=animation['duration'])


def _create_for_fade(template: Dict[str, Any],
                     interpolator: str) -> Tuple[str, Dict[str, Any]]:
    template_copy = deepcopy(template)
    animation = _fade_animation()
    return _create_for_animation(template_copy,
                                 animation,
                                 interpolator,
                                 duration=animation['duration'])


def _create_for_set(template: Dict[str, Any],
                    interpolator: str) -> Tuple[str, Dict[str, Any]]:
    template_copy = deepcopy(template)
    animation = _set_animation(_fade_animation(), _scale_animation())
    return _create_for_animation(template_copy,
                                 animation,
                                 interpolator,
                                 duration=animation['items'][0]['duration'])


def _create_for_no_animation(
        template: Dict[str, Any]) -> Tuple[str, Dict[str, Any]]:
    template_copy = deepcopy(template)
    animation = _no_animation()
    _, result = _create_for_animation(template_copy,
                                      animation,
                                      interpolator=None)
    return 'no_animation.json', result


def _generate_animation_scenarios(
        template: Dict[str, Any]) -> List[Tuple[str, Dict[str, Any]]]:
    result = []
    for interpolator in INTERPOLATORS:
        result.append(_create_for_scale(template, interpolator))
        result.append(_create_for_fade(template, interpolator))
        result.append(_create_for_set(template, interpolator))
    result.append(_create_for_no_animation(template))
    return result


def _cwd(path: str) -> str:
    return os.path.abspath(os.path.join(os.path.dirname(__file__), path))


def _animation_dir(path: str) -> str:
    return _cwd(f'../../../test_data/regression_test_data/action_animation/{path}')


def main():
    with open(_cwd('animation_scenario_template.json')) as f:
        template = json.load(f)
    print('paths:')
    for filename, cardjson in _generate_animation_scenarios(template):
        scenario_dir = _animation_dir(os.path.dirname(filename))
        if not os.path.exists(scenario_dir):
            os.mkdir(scenario_dir)
        with open(_animation_dir(filename), 'w') as f:
            json.dump(cardjson, f, ensure_ascii=False, indent=2)
            f.write('\n')
        print(f"  - 'regression_test_data/action_animation/{filename}'")
    print('DONE.')


if __name__ == '__main__':
    main()
