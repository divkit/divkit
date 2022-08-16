export const isProd = process.env.NODE_ENV === 'production';
export const urlPath = location.pathname.replace(/\/$/, '');
export const serverHostPath = process.env.NODE_ENV === 'production' ? location.host + urlPath + '/' : 'localhost:3000/';
export const clientHostPath = location.host + urlPath + '/';
