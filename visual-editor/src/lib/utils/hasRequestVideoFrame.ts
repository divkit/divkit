export const hasRequestVideoFrame = typeof window !== 'undefined' &&
    'requestVideoFrameCallback' in HTMLVideoElement.prototype;
