import type {Config} from '@jest/types';

const config: Config.InitialOptions = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  snapshotSerializers: ['<rootDir>/simple-snapshot-serializer']
};

export default config;
