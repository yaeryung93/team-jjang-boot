import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError, request } from './http';
afterEach(() => vi.unstubAllGlobals());
describe('API communication', () => {
    it('sends JSON and returns a created resource', async () => {
        const fetchMock = vi.fn().mockResolvedValue(new Response(JSON.stringify({ id: 1 }), { status: 201 }));
        vi.stubGlobal('fetch', fetchMock);
        const body = JSON.stringify({ name: 'Team' });
        expect(await request('/api/projects', { method: 'POST', body })).toEqual({ id: 1 });
        expect(fetchMock.mock.calls[0][0]).toBe('http://localhost:8080/api/projects');
        expect(fetchMock.mock.calls[0][1].headers.get('Content-Type')).toBe('application/json');
        expect(fetchMock.mock.calls[0][1].body).toBe(body);
    });
    it('preserves server validation errors', async () => {
        vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(JSON.stringify({ detail: '입력 오류', errors: { name: '필수' } }), { status: 400 })));
        await expect(request('/api/projects')).rejects.toMatchObject({ status: 400, message: '입력 오류', fields: { name: '필수' } });
    });
    it('handles non-JSON errors and network failure', async () => {
        vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response('Bad gateway', { status: 502 })));
        await expect(request('/api/projects')).rejects.toMatchObject({ status: 502 });
        vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')));
        await expect(request('/api/projects')).rejects.toBeInstanceOf(ApiError);
    });
    it('keeps navigation cancellation distinct from connection errors', async () => {
        const controller = new AbortController();
        controller.abort();
        const error = new DOMException('Aborted', 'AbortError');
        vi.stubGlobal('fetch', vi.fn().mockRejectedValue(error));
        await expect(request('/api/projects', { signal: controller.signal })).rejects.toBe(error);
    });
});
