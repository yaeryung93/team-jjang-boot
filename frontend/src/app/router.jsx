import { createBrowserRouter, redirect } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { RouteError } from '../components/RouteError';
import { projectsApi } from '../features/projects/api';
import { membersApi } from '../features/members/api';
import { tasksApi } from '../features/tasks/api';
import { ApiError, errorMessage } from '../lib/http';
import { ProjectsPage } from '../pages/ProjectsPage';
import { NewProjectPage } from '../pages/NewProjectPage';
import { ProjectDetailPage } from '../pages/ProjectDetailPage';
async function createProject({ request }) {
    const form = await request.formData();
    try {
        const project = await projectsApi.create({ name: String(form.get('name') || '').trim(), description: String(form.get('description') || ''), deadline: String(form.get('deadline') || '') });
        return redirect(`/projects/${project.id}`);
    }
    catch (error) {
        return { message: errorMessage(error), fields: error instanceof ApiError ? error.fields : {} };
    }
}
async function projectDetail({ params, request }) {
    if (!params.projectId || !/^[1-9]\d*$/.test(params.projectId))
        throw new Response(null, { status: 404 });
    const id = Number(params.projectId);
    if (!Number.isSafeInteger(id))
        throw new Response(null, { status: 404 });
    const [project, members, tasks] = await Promise.all([
        projectsApi.get(id, request.signal), membersApi.list(id, request.signal), tasksApi.list(id, request.signal),
    ]);
    return { project, members, tasks };
}
export const router = createBrowserRouter([{
        element: <Layout />,
        hydrateFallbackElement: <p className="container" role="status">불러오는 중…</p>,
        children: [{
                errorElement: <RouteError />,
                children: [
                    { index: true, loader: () => redirect('/projects'), element: <p role="status">이동 중…</p> },
                    { path: 'projects', loader: ({ request }) => projectsApi.list(request.signal), element: <ProjectsPage />, hydrateFallbackElement: <p role="status">불러오는 중…</p> },
                    { path: 'projects/new', action: createProject, element: <NewProjectPage /> },
                    { path: 'projects/:projectId', loader: projectDetail, element: <ProjectDetailPage />, hydrateFallbackElement: <p role="status">불러오는 중…</p> },
                    { path: '*', element: <RouteError />, loader: () => { throw new Response(null, { status: 404 }); } },
                ],
            }],
    }]);
