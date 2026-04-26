function showAlert(msg, type) {
    document.getElementById('alert-container').innerHTML =
        `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
}

function renderPermissions(permissions, selectedIds) {
    const container = document.getElementById('permissions-container');
    if (!permissions.length) {
        container.innerHTML = '<span class="text-muted small">No permissions available</span>';
        return;
    }
    container.innerHTML = permissions.map(p => `
        <div class="form-check">
            <input class="form-check-input" type="checkbox"
                   id="perm-${p.id}" value="${p.id}"
                   ${selectedIds.includes(p.id) ? 'checked' : ''}>
            <label class="form-check-label" for="perm-${p.id}">${p.name}</label>
        </div>
    `).join('');
}

function getCheckedPermissions() {
    return Array.from(
        document.querySelectorAll('#permissions-container input[type=checkbox]:checked')
    ).map(cb => cb.value);
}

document.getElementById('role-form').addEventListener('submit', function (e) {
    e.preventDefault();
    const btn = document.getElementById('submit-btn');
    btn.disabled = true;

    const payload = {
        name: document.getElementById('name').value.trim(),
        permissionIds: getCheckedPermissions()
    };

    fetch('/api/users/roles', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(res => res.ok ? res : res.text().then(t => Promise.reject(t)))
    .then(() => { window.location.href = '/users/roles'; })
    .catch(err => {
        showAlert(err || 'Failed to create role.', 'danger');
        btn.disabled = false;
    });
});

fetch('/api/users/permissions')
    .then(res => {
        if (!res.ok) throw new Error(res.status);
        return res.json();
    })
    .then(perms => renderPermissions(perms, []))
    .catch(() => showAlert('Could not load permissions.', 'warning'));
