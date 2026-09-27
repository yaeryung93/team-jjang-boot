import { request } from '../../lib/http';
export const progressApi = {
    list: (projectId, taskId, signal) => request(`/api/projects/${projectId}/tasks/${taskId}/progress`, { signal }),
    create: (projectId, taskId, input) => request(`/api/projects/${projectId}/tasks/${taskId}/progress`, { method: 'POST', body: JSON.stringify(input) }),
};
