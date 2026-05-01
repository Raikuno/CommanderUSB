document.getElementById('role-form').addEventListener('submit', function (e) {
    e.preventDefault();
    const btn = document.getElementById('submit-btn');
    btn.disabled = true;

    const payload = {
        name: document.getElementById('name').value.trim(),
        permissionIds: getCheckedPermissions()
    };

    fetch(`/api/users/roles/${window.ROLE_ID}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(res => res.ok ? res : res.text().then(t => Promise.reject(t)))
    .then(() => { window.location.href = '/users/roles'; })
    .catch(err => {
        showAlert(err || 'Failed to save role.', 'danger');
        btn.disabled = false;
    });
});


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


Promise.all([
    fetch('/api/users/permissions').then(r => {
        if (!r.ok) throw new Error(r.status);
        return r.json();
    }),
    fetch(`/api/users/roles/${window.ROLE_ID}`).then(r => {
        if (!r.ok) throw new Error(r.status);
        return r.json();
    })
]).then(([allPerms, role]) => {
    document.getElementById('name').value = role.name;
    const selectedIds = role.permissions ? role.permissions.map(p => p.id) : [];
    renderPermissions(allPerms, selectedIds);
}).catch(() => showAlert('Failed to load role data.', 'danger'));
