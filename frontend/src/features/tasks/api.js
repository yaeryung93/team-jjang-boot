import { request } from '../../lib/http';
export const tasksApi = {
    list: (projectId, signal) => request(`/api/projects/${projectId}/tasks`, { signal }),
    create: (projectId, input) => request(`/api/projects/${projectId}/tasks`, { method: 'POST', body: JSON.stringify(input) }),
};
