import { request } from '../../lib/http';
export const projectsApi = {
    list: (signal) => request(`/api/projects`, { signal }),
    create: (input) => request(`/api/projects`, { method: 'POST', body: JSON.stringify(input) }),
    get: (id, signal) => request(`/api/projects/${id}`, { signal }),
};
