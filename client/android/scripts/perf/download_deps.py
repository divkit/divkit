# Copyright (c) 2018 Yandex LLC. All rights reserved.
# Author: Andrey Malets <malets@yandex-team.ru>

import argparse
import contextlib
import os
import shutil
import tempfile
import zipfile

import requests
import yaml


@contextlib.contextmanager
def temporary_file(**tempfile_args):
    file_ = tempfile.NamedTemporaryFile(delete=False, **tempfile_args)
    try:
        file_.close()
        yield file_.name
    finally:
        os.remove(file_.name)


def parse_args():
    p = argparse.ArgumentParser(
        description='Download dependencies required to run performance tests')
    default_config = os.path.join(os.path.dirname(__file__), 'deps.yaml')
    p.add_argument('--config', '-c', default=default_config,
                   help='Configuration file.')
    return p.parse_args()


def load_config(args):
    with open(args.config) as config_file:
        return yaml.load(config_file)


def download_and_unpack_zip(url, dest, params={}):
    resp = requests.get(url, params=params, stream=True)
    resp.raise_for_status()
    with temporary_file(prefix='perf_dep_', suffix='.zip') as temp_file:
        with open(temp_file, 'w') as output:
            resp.raw.decode_content = True
            shutil.copyfileobj(resp.raw, output)
        if os.path.exists(dest):
            shutil.rmtree(dest)
        with zipfile.ZipFile(temp_file) as zip_input:
            zip_input.extractall(dest)


def move_to_root(root, from_):
    for path in os.listdir(os.path.join(root, from_)):
        shutil.move(os.path.join(root, from_, path),
                    os.path.join(root, path))
    shutil.rmtree(os.path.join(root, from_.split('/')[0]))


def download_catapult(config, dest):
    relative = config['bro_catapult']['relative']
    url = ('https://bitbucket.browser.yandex-team.ru'
           '/rest/api/1.0/projects/STARDUST/repos/browser/archive')
    params = {'at': config['bro_catapult']['commit'], 'format': 'zip',
              'path': ['{}/{}'.format(relative, path)
                       for path in config['bro_catapult']['paths']]}
    download_and_unpack_zip(url, dest, params)
    move_to_root(dest, relative)


def main():
    args = parse_args()
    config = load_config(args)
    third_party = os.path.join(os.path.dirname(__file__), 'third_party')
    download_catapult(config, os.path.join(third_party, 'catapult'))


if __name__ == '__main__':
    main()
