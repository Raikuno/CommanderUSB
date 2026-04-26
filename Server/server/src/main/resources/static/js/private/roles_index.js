function escapeHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function showAlert(msg, type) {
    document.getElementById('alert-container').innerHTML =
        `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
}

function renderRoles(roles) {
    const container = document.getElementById('roles-container');
    if (!roles.length) {
        container.innerHTML = '<div class="col-12 text-center text-muted">No roles found</div>';
        return;
    }
    container.innerHTML = roles.map(r => {
        const isAdmin = r.name.toLowerCase() === 'administrator';
        const permList = r.permissions && r.permissions.length
            ? r.permissions.map(p => `<span class="badge bg-secondary me-1">${escapeHtml(p.name)}</span>`).join('')
            : '<span class="text-muted">No permissions</span>';
        const editBtn = isAdmin
            ? `<button class="btn btn-sm btn-outline-secondary" disabled
                       title="Administrator role cannot be edited">Edit</button>`
            : `<a href="/users/roles/${r.id}/edit" class="btn btn-sm btn-outline-primary">Edit</a>`;
        return `
            <div class="col-md-4 col-lg-3">
                <div class="card h-100">
                    <div class="card-body">
                        <h5 class="card-title d-flex align-items-center gap-2">
                            ${escapeHtml(r.name)}
                            ${isAdmin ? '<span class="badge bg-dark">Protected</span>' : ''}
                        </h5>
                        <p class="card-text small mb-3">${permList}</p>
                    </div>
                    <div class="card-footer bg-transparent">
                        ${editBtn}
                    </div>
                </div>
            </div>`;
    }).join('');
}

fetch('/api/users/roles')
    .then(res => {
        if (!res.ok) throw new Error(res.status);
        return res.json();
    })
    .then(renderRoles)
    .catch(() => showAlert('Failed to load roles.', 'danger'));
