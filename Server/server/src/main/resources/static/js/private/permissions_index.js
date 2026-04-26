function showAlert(msg, type) {
    document.getElementById('alert-container').innerHTML =
        `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
}

fetch('/api/users/permissions')
    .then(res => {
        if (!res.ok) throw new Error(res.status);
        return res.json();
    })
    .then(permissions => {
        const tbody = document.getElementById('permissions-tbody');
        if (!permissions || permissions.length === 0) {
            tbody.innerHTML = '<tr><td class="text-center text-muted">No permissions found</td></tr>';
            return;
        }
        tbody.innerHTML = permissions.map(p => `<tr><td>${p.name}</td></tr>`).join('');
    })
    .catch(() => showAlert('Failed to load permissions.', 'danger'));
