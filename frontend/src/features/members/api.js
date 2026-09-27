import { request } from '../../lib/http';
export const membersApi = {
    list: (projectId, signal) => request(`/api/projects/${projectId}/members`, { signal }),
    create: (projectId, input) => request(`/api/projects/${projectId}/members`, { method: 'POST', body: JSON.stringify(input) }),
};
